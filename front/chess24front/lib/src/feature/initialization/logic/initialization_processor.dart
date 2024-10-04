import 'dart:async';

import 'package:grpc/grpc.dart';
import 'package:grpc/grpc_connection_interface.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/authorized_interceptor.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/internal_authentication_repository.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/result_code/result_code_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/standard_response_mappers.dart';
import 'package:sizzle_starter/src/feature/auth/repository/authentication_repository.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/entity/jwts_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_refresh_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_state.dart';
import 'package:sizzle_starter/src/core/grpc/executor/grpc_executor.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-server-service.pbgrpc.dart';
import 'package:sizzle_starter/src/core/grpc/generated/player-service.pbgrpc.dart';
import 'package:sizzle_starter/src/core/grpc/h2_auth_services.dart';
import 'package:sizzle_starter/src/core/grpc/h2_chess_services.dart';
import 'package:sizzle_starter/src/core/utils/logger.dart';
import 'package:sizzle_starter/src/feature/app/logic/tracking_manager.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/auth_bloc/auth_bloc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/user_storage_data_source.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/user_local_storage_data_source.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';
import 'package:sizzle_starter/src/feature/initialization/model/dependencies.dart';
import 'package:sizzle_starter/src/feature/initialization/model/environment.dart';
import 'package:sizzle_starter/src/feature/initialization/model/environment_store.dart';
import 'package:sizzle_starter/src/feature/route/app_router.dart';
import 'package:sizzle_starter/src/feature/settings/bloc/settings_bloc.dart';
import 'package:sizzle_starter/src/feature/settings/data/locale_datasource.dart';
import 'package:sizzle_starter/src/feature/settings/data/locale_repository.dart';
import 'package:sizzle_starter/src/feature/settings/data/theme_datasource.dart';
import 'package:sizzle_starter/src/feature/settings/data/theme_mode_codec.dart';
import 'package:sizzle_starter/src/feature/settings/data/theme_repository.dart';

part 'initialization_factory.dart';

/// {@template initialization_processor}
/// A class which is responsible for processing initialization steps.
/// {@endtemplate}
final class InitializationProcessor {
  final ExceptionTrackingManager _trackingManager;
  final EnvironmentStore _environmentStore;

  /// {@macro initialization_processor}
  const InitializationProcessor({
    required ExceptionTrackingManager trackingManager,
    required EnvironmentStore environmentStore,
  })  : _trackingManager = trackingManager,
        _environmentStore = environmentStore;

  Future<Dependencies> _initDependencies() async {
    final sharedPreferences = await SharedPreferences.getInstance();

    final settingsBloc = await _initSettingsBloc(sharedPreferences);

    final userStorageDataSource =
        await _initUserStorageDataSource(sharedPreferences);

    final authChannel = await _initAuthChannel();

    final chessChannel = await _initChessChannel();

    final h2authServices = await _initH2AuthServices(authChannel);

    final h2chessServices = await _initH2ChessServices(
      chessChannel,
      userStorageDataSource,
    );

    final errorMapper = ErrorMapper();

    final jwtsMapper = JwtsMapper();

    final resultCodeMapper = ResultCodeMapper();

    final standardResponseMappers = StandardResponseMappers(
        errorMapper: errorMapper, resultCodeMapper: resultCodeMapper);

    final jwtRefreshResponseMapper = JwtRefreshResponseMapper(
        standardResponseMappers: standardResponseMappers);

    final authenticationDataProvider = _initAuthenticationProvider(
        h2authServices,
        jwtsMapper,
        jwtRefreshResponseMapper,
        standardResponseMappers);

    final userRepo = await _initUserRepository(
      userStorageDataSource,
      authenticationDataProvider,
    );

    final internalAuthenticationRepository =
        InternalAuthenticationRepositoryImpl(
      authenticationDataProvider: authenticationDataProvider,
    );

    final grpcExecutorService = await _initGrpcExecutorService(
      userRepo,
      internalAuthenticationRepository,
    );

    final appRouter = AppRouter(authorizationService: userRepo);

    final authBloc = await _initAuthBloc(userRepo);

    return Dependencies(
      sharedPreferences: sharedPreferences,
      appRouter: appRouter,
      settingsBloc: settingsBloc,
      authBloc: authBloc,
      userRepository: userRepo,
      h2authServices: h2authServices,
      h2chessServices: h2chessServices,
      grpcExecutorService: grpcExecutorService,
      internalAuthenticationRepository: internalAuthenticationRepository,
      standardResponseMappers: standardResponseMappers,
    );
  }

  FutureOr<AuthenticationDataProvider> _initAuthRepo(
          H2AuthServices h2authServices,
          StandardResponseMappers standardResponseMappers,
          JwtsMapper jwtsMapper) =>
      AuthenticationNetworkRpcDataProvider(
          h2AuthServices: h2authServices,
          standardResponseMappers: standardResponseMappers,
          jwtsMapper: jwtsMapper,
          jwtRefreshResponseMapper: JwtRefreshResponseMapper(
              standardResponseMappers: standardResponseMappers));

  FutureOr<SettingsBloc> _initSettingsBloc(SharedPreferences prefs) async {
    final localeRepository = LocaleRepositoryImpl(
      localeDataSource: LocaleDataSourceLocal(sharedPreferences: prefs),
    );

    final themeRepository = ThemeRepositoryImpl(
      themeDataSource: ThemeDataSourceLocal(
        sharedPreferences: prefs,
        codec: const ThemeModeCodec(),
      ),
    );

    final localeFuture = localeRepository.getLocale();
    final theme = await themeRepository.getTheme();
    final locale = await localeFuture;

    final initialState = SettingsState.idle(appTheme: theme, locale: locale);

    final settingsBloc = SettingsBloc(
      localeRepository: localeRepository,
      themeRepository: themeRepository,
      initialState: initialState,
    );
    return settingsBloc;
  }

  FutureOr<UserRepository> _initUserRepository(
          UserStorageDataProvider authDataSource,
          AuthenticationDataProvider userDataProvider) async =>
      UserRepositoryImpl(_mapUserToState(await authDataSource.getUser()),
          userDataSource: authDataSource);

  FutureOr<UserStorageDataProvider> _initUserStorageDataSource(
    SharedPreferences sharedPreferences,
  ) =>
      UserLocalStorageDataSource(sharedPreferences: sharedPreferences);

  FutureOr<ClientChannel> _initAuthChannel() =>
      _environmentStore.environment == Environment.dev
          ? ClientChannel(
              "clouds-dev.k11dev.tech",
              port: 11443,
              options:
                  const ChannelOptions(connectionTimeout: Duration(seconds: 5)),
            )
          : ClientChannel(
              "clouds-dev.k11dev.tech",
              port: 11443,
              options:
                  const ChannelOptions(connectionTimeout: Duration(seconds: 5)),
            );

  FutureOr<ClientChannel> _initChessChannel() =>
      _environmentStore.environment == Environment.dev
          ? ClientChannel(
              "clouds-dev.k11dev.tech",
              port: 18443,
              options:
                  const ChannelOptions(connectTimeout: Duration(seconds: 5)),
            )
          : ClientChannel("prodHostCHESS");

  FutureOr<H2AuthServices> _initH2AuthServices(ClientChannel channel) =>
      H2AuthServices(
        authServiceClient: AuthServerClientServiceClient(channel),
      );
  FutureOr<GrpcExecutorService> _initGrpcExecutorService(
          UserRepository userRepository,
          InternalAuthenticationRepository internalAuthenticationRepository) =>
      GrpcExecutorServiceImpl(
          userRepository: userRepository,
          authenticationRepository: internalAuthenticationRepository);

  FutureOr<H2ChessServices> _initH2ChessServices(
    ClientChannel channel,
    UserStorageDataProvider userStorageDataSource,
  ) =>
      H2ChessServices(
        playerServiceClient: PlayerServiceClient(
          channel,
          interceptors: [
            AuthorizedInterceptor(userStorageDataSource: userStorageDataSource),
          ],
        ),
      );
  AuthenticationDataProvider _initAuthenticationProvider(
          H2AuthServices h2authServices,
          JwtsMapper jwtsMapper,
          JwtRefreshResponseMapper jwtRefreshResponseMapper,
          StandardResponseMappers standardResponseMappers) =>
      AuthenticationNetworkRpcDataProvider(
        h2AuthServices: h2authServices,
        jwtsMapper: jwtsMapper,
        jwtRefreshResponseMapper: jwtRefreshResponseMapper,
        standardResponseMappers: standardResponseMappers,
      );

  UserState _mapUserToState(AuthorizedUserEntity? user) => user != null
      ? UserState.authorized(authorizedUserEntity: user)
      : const UserState.empty();

  /// Method that starts the initialization process
  /// and returns the result of the initialization.
  ///
  /// This method may contain additional steps that need initialization
  /// before the application starts
  /// (for example, caching or enabling tracking manager)
  Future<InitializationResult> initialize() async {
    if (_environmentStore.enableTrackingManager) {
      await _trackingManager.enableReporting();
    }
    final stopwatch = Stopwatch()..start();

    logger.info('Initializing dependencies...');
    // initialize dependencies
    final dependencies = await _initDependencies();
    logger.info('Dependencies initialized');

    stopwatch.stop();
    final result = InitializationResult(
      dependencies: dependencies,
      msSpent: stopwatch.elapsedMilliseconds,
    );
    return result;
  }

  Future<AuthBloc> _initAuthBloc(UserRepository userRepo) async {
    final storedUser = await userRepo.getCurrentUserState;
    return AuthBloc(
      userRepo: userRepo,
      initialState: AuthState.idle(storedUser),
    );
  }
}
