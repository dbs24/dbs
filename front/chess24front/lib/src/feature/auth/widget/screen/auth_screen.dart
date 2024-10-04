import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/entity/jwts_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_refresh_response_mapper.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/sign_in/sign_in_bloc.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/sign_up/sign_up_bloc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_login_response_mapper.dart';
import 'package:sizzle_starter/src/feature/auth/data_provider/registration_data_provider.dart';
import 'package:sizzle_starter/src/feature/auth/repository/authentication_repository.dart';
import 'package:sizzle_starter/src/feature/auth/repository/registration_repository.dart';
import 'package:sizzle_starter/src/feature/auth/widget/sign_in_scope.dart';
import 'package:sizzle_starter/src/feature/auth/widget/sign_up_scope.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';
import 'package:sizzle_starter/src/feature/initialization/widget/dependencies_scope.dart';

@RoutePage()
class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  late final RegistrationRepository _registrationRepository;
  late final AuthenticationRepository _authenticationRepository;

  @override
  Widget build(BuildContext context) {
    final dependencies = DependenciesScope.of(context);
    return SignUpScope(
      signUpBloc: SignUpBloc(
        grpcExecutorService: dependencies.grpcExecutorService,
        authenticationRepository: _authenticationRepository,
        initialState: const SignUpState.idle(),
        registrationRepository: _registrationRepository,
        userRepo: dependencies.userRepository,
      ),
      child: SignInScope(
        signInBloc: SignInBloc(
          userRepository: dependencies.userRepository,
          grpcExecutorService: dependencies.grpcExecutorService,
          authenticationRepository: _authenticationRepository,
          initialState: const SignInState.idle(),
        ),
        child: const AutoRouter(),
      ),
    );
  }

  @override
  void initState() {
    super.initState();

    final dependencies = DependenciesScope.of(context);
    _registrationRepository = RegistrationRepositoryImpl(
        registrationDataProvider: RegistrationDataProviderImpl(
            h2ChessServices: dependencies.h2chessServices,
            standardResponseMappers: dependencies.standardResponseMappers));

    final authenticationDataProvider = AuthenticationNetworkRpcDataProvider(
        h2AuthServices: dependencies.h2authServices,
        jwtsMapper: JwtsMapper(),
        jwtRefreshResponseMapper: JwtRefreshResponseMapper(
            standardResponseMappers: dependencies.standardResponseMappers),
        standardResponseMappers: dependencies.standardResponseMappers);

    _authenticationRepository = AuthenticationRepositoryImpl(
      authenticationDataProvider: authenticationDataProvider,
    );
  }
}
