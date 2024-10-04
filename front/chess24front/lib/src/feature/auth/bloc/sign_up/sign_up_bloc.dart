import 'package:bloc/bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_login_data_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_user_data.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/core/grpc/executor/grpc_executor.dart';
import 'package:sizzle_starter/src/feature/auth/data_provider/registration_data_provider.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';
import 'package:sizzle_starter/src/feature/auth/repository/authentication_repository.dart';
import 'package:sizzle_starter/src/feature/auth/repository/registration_repository.dart';

part 'sign_up_bloc.freezed.dart';
part 'sign_up_event.dart';
part 'sign_up_state.dart';

class SignUpBloc extends Bloc<SignUpEvent, SignUpState> {
  final UserRepository _userRepo;

  final RegistrationRepository _registrationRepository;

  final AuthenticationRepository _authenticationRepository;

  final GrpcExecutorService _grpcExecutorService;

  SignUpBloc(
      {required SignUpState initialState,
      required UserRepository userRepo,
      required RegistrationRepository registrationRepository,
      required AuthenticationRepository authenticationRepository,
      required GrpcExecutorService grpcExecutorService})
      : _userRepo = userRepo,
        _registrationRepository = registrationRepository,
        _grpcExecutorService = grpcExecutorService,
        _authenticationRepository = authenticationRepository,
        super(initialState) {
    on<SignUpEvent>((event, emit) async {
      await event.map(
        defaultSignUp: (event) => _onDefaultSignUp(event, emit),
        withoutRegistrationSignUp: (event) =>
            _onWithoutRegistrationSignUp(event, emit),
      );
    });
  }

  Future<void> _onDefaultSignUp(
    _DefaultSignUp event,
    Emitter<SignUpState> emit,
  ) async {
    emit(const SignUpState.processing());

    final result = await _grpcExecutorService.execute(
        () async => _registrationRepository.defaultSignUp(event.signUpData));

    final resultState = await result.map(
        success: (executionResult) async => executionResult.result.resultCode
            .maybeMap(
                ok: (_) async {
                  final receivedUsername =
                      executionResult.result.entity.username;
                  return _authenticateRegisteredUser(receivedUsername, event);
                },
                orElse: () async => SignUpState.error(
                    error: executionResult.result.errors.toString())),
        failed: (executionResult) async =>
            SignUpState.error(error: executionResult.error.toString()));

    emit(resultState);
  }

  Future<SignUpState> _authenticateRegisteredUser(
      String receivedUsername, _DefaultSignUp event) async {
    final authenticationResult = await _grpcExecutorService.execute(() =>
        _authenticationRepository.defaultAuthentication(DefaultLoginDataEntity(
            login: receivedUsername, password: event.signUpData.password)));

    return authenticationResult.map(
        success: (executionResult) => executionResult.result.resultCode
            .maybeMap(
                ok: (_) {
                  _userRepo.signIn(executionResult.result.entity!);
                  return const SignUpState.success();
                },
                orElse: () => SignUpState.error(
                    error: executionResult.result.resultCode.toString())),
        failed: (executionResult) =>
            SignUpState.error(error: executionResult.error.toString()));
  }

  Future<void> _onWithoutRegistrationSignUp(
    _WithoutRegistrationSignUp event,
    Emitter<SignUpState> emit,
  ) async {}
}
