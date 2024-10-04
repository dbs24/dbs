import 'package:shared_preferences/shared_preferences.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/internal_authentication_repository.dart';
import 'package:sizzle_starter/src/core/grpc/executor/grpc_executor.dart';
import 'package:sizzle_starter/src/core/grpc/h2_auth_services.dart';
import 'package:sizzle_starter/src/core/grpc/h2_chess_services.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/standard_response_mappers.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/auth_bloc/auth_bloc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/feature/auth/repository/authentication_repository.dart';
import 'package:sizzle_starter/src/feature/route/app_router.dart';
import 'package:sizzle_starter/src/feature/settings/bloc/settings_bloc.dart';

/// {@template dependencies}
/// Dependencies container
/// {@endtemplate}
base class Dependencies {
  /// {@macro dependencies}
  const Dependencies({
    required this.sharedPreferences,
    required this.appRouter,
    required this.settingsBloc,
    required this.authBloc,
    required this.standardResponseMappers,
    required this.userRepository,
    required this.h2authServices,
    required this.h2chessServices,
    required this.grpcExecutorService,
    required this.internalAuthenticationRepository,
    //required this.authenticationDataProvider,
  });

  /// [SharedPreferences] instance, used to store Key-Value pairs.
  final SharedPreferences sharedPreferences;

  /// [SettingsBloc] instance, used to manage theme and locale.
  final SettingsBloc settingsBloc;

  /// [UserRepository] instance
  final UserRepository userRepository;

  /// [AuthenticationDataProvider] instance
  /// It's a part of authentication, not whole app
  // final AuthenticationDataProvider authRepo;

  /// [AuthBloc] instance
  final AuthBloc authBloc;

  /// [AppRouter] instance
  final AppRouter appRouter;

  /// [H2AuthServices] instance
  final H2AuthServices h2authServices;

  /// [H2ChessServices] instance
  final H2ChessServices h2chessServices;

  /// [GrpcExecutorService] instance
  final GrpcExecutorService grpcExecutorService;

  /// [AuthenticationRepository] instance
  final InternalAuthenticationRepository internalAuthenticationRepository;

  /// [StandardResponseMappers] instance for mapping standard responses
  final StandardResponseMappers standardResponseMappers;
  /// [AuthenticationDataProvider] instance
  /// It's a part of authentication, not whole app
  //final AuthenticationDataProvider authenticationDataProvider;
}

/// {@template initialization_result}
/// Result of initialization
/// {@endtemplate}
final class InitializationResult {
  /// {@macro initialization_result}
  const InitializationResult({
    required this.dependencies,
    required this.msSpent,
  });

  /// The dependencies
  final Dependencies dependencies;

  /// The number of milliseconds spent
  final int msSpent;

  @override
  String toString() => '$InitializationResult('
      'dependencies: $dependencies, '
      'msSpent: $msSpent'
      ')';
}
