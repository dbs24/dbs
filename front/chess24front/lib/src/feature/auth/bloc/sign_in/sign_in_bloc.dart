import 'package:bloc/bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_login_data_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_login_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/processor/default_auth_processor.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/core/grpc/executor/grpc_executor.dart';
import 'package:sizzle_starter/src/feature/auth/repository/authentication_repository.dart';

part 'sign_in_bloc.freezed.dart';
part 'sign_in_event.dart';
part 'sign_in_state.dart';

class SignInBloc extends Bloc<SignInEvent, SignInState> {
  final UserRepository _userRepository;
  final GrpcExecutorService _grpcExecutorService;
  final AuthenticationRepository _authenticationRepository;

  SignInBloc({
    required UserRepository userRepository,
    required GrpcExecutorService grpcExecutorService,
    required AuthenticationRepository authenticationRepository,
    required SignInState initialState,
  })  : _userRepository = userRepository,
        _grpcExecutorService = grpcExecutorService,
        _authenticationRepository = authenticationRepository,
        super(initialState) {
    on<SignInEvent>(
      (event, emit) => event.map(
        defaultSignIn: (event) => _defaultSignIn(event, emit),
      ),
    );
  }

  Future<void> _defaultSignIn(
    _DefaultSignInEvent event,
    Emitter<SignInState> emit,
  ) async {
    emit(SignInState.processing(cause: state.cause));

    final requestAuthenticationResult = await _grpcExecutorService.execute(
      () async => _authenticationRepository.defaultAuthentication(
        DefaultLoginDataEntity(login: event.username, password: event.password),
      ),
    );

    
    final resultState = requestAuthenticationResult.map(
      success: (requestResult) => requestResult.result.resultCode.maybeMap(
          ok: (code) {  
            /// TODO: remove null unsafe check in future.
            _userRepository.signIn(requestResult.result.entity!);
            return const SignInState.passed();},
          orElse: () => SignInState.error(cause: requestResult.result.errors),
        ),
      failed: (requestResult) => SignInState.error(cause: requestResult.error),
    );


    emit(resultState);
  }
}
