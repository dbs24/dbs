import 'package:flutter_test/flutter_test.dart';
import 'package:grpc/grpc.dart';
import 'package:grpc/src/client/call.dart';
import 'package:grpc/src/client/common.dart';
import 'package:mocktail/mocktail.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/jwt_result_enity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_code.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/internal_authentication_repository.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_state.dart';
import 'package:sizzle_starter/src/core/grpc/executor/execution_error.dart';
import 'package:sizzle_starter/src/core/grpc/executor/execution_result.dart';
import 'package:sizzle_starter/src/core/grpc/executor/execution_exception.dart';
import 'package:sizzle_starter/src/core/grpc/executor/grpc_executor.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-server-service.pbgrpc.dart';
import 'package:sizzle_starter/src/feature/auth/repository/authentication_repository.dart';

class UserRepositoryMock extends Mock implements UserRepository {}

class AuthenticationRepositoryMock extends Mock
    implements InternalAuthenticationRepository {}

class AuthorizedUserEntityMock extends Mock implements AuthorizedUserEntity {}

class ServerResultEntityMock extends Mock
    implements ServerResultEntity<String> {}

class JwtResultEntityMock extends Mock implements JwtResultEntity {}

void main() {
  group("Unsecured GrpcExecutorServiceImpl test >", () {
    late GrpcExecutorService grpcExecutorService;
    late AuthorizedUserEntity user;

    setUp(() {
      final authenticationRepository = AuthenticationRepositoryMock();
      final userRepository = UserRepositoryMock();

      grpcExecutorService = GrpcExecutorServiceImpl(
        userRepository: userRepository,
        authenticationRepository: authenticationRepository,
      );

      user = AuthorizedUserEntityMock();
    });

    test("Execution test OK", () async {
      final result = ServerResultEntityMock();

      when(() => result.resultCode).thenReturn(const ServerResultCode.ok());
      final execResult = await grpcExecutorService.execute(() async => result);

      expect(execResult, ExecutionResult.success(result: result));
    });

    test("Execution test with other server code", () async {
      final result = ServerResultEntityMock();

      when(() => result.resultCode)
          .thenReturn(const ServerResultCode.internalError());
      final execResult = await grpcExecutorService.execute(() async => result);

      expect(execResult, ExecutionResult.success(result: result));
    });

    test("Simple execution test on wrong point type", () async {
      final result = ServerResultEntityMock();

      when(() => result.resultCode)
          .thenReturn(const ServerResultCode.unauthorized());
      expect(
        () => grpcExecutorService.execute(() async => result),
        throwsA(isA<WrongPointTypeException>()),
      );
    });
  });

  group("Secured GrpcExecutorServiceImpl test >", () {
    group("Success execution test >", () {
      late GrpcExecutorService grpcExecutorService;
      late AuthorizedUserEntity user;
      late AuthorizedUserEntity refreshedUser;

      setUp(() {
        final authenticationRepository = AuthenticationRepositoryMock();
        final userRepository = UserRepositoryMock();

        final okJwtResultEntity = JwtResultEntityMock();

        user = AuthorizedUserEntityMock();

        refreshedUser = AuthorizedUserEntityMock();

        // when(() => user.accessToken).thenReturn("accessToken");
        // when(() => user.refreshToken).thenReturn("refreshToken");

        when(() => okJwtResultEntity.resultCode)
            .thenReturn(const ServerResultCode.ok());

        when(() => authenticationRepository.refreshJwt(user))
            .thenAnswer((inv) async => okJwtResultEntity);

        when(() => userRepository.getCurrentUserState).thenAnswer(
          (invocation) async =>
              UserState.authorized(authorizedUserEntity: user),
        );

        grpcExecutorService = GrpcExecutorServiceImpl(
          userRepository: userRepository,
          authenticationRepository: authenticationRepository,
        );
      });

      test("Authorized execution ok test", () async {
        final result = ServerResultEntityMock();

        when(() => result.resultCode).thenReturn(const ServerResultCode.ok());

        final executionResult =
            await grpcExecutorService.securedExecute(() async => result);

        expect(executionResult, ExecutionResult.success(result: result));
      });

      test("Authorized execution with refresh token ok", () async {
        final firstResult = ServerResultEntityMock();
        final secondResult = ServerResultEntityMock();

        when(() => firstResult.resultCode)
            .thenReturn(const ServerResultCode.unauthorized());

        when(() => secondResult.resultCode)
            .thenReturn(const ServerResultCode.ok());

        when(() => secondResult.entity).thenReturn("someEntity");

        int requestNumber = 1;

        final executionResult =
            await grpcExecutorService.securedExecute(() async {
          if (requestNumber == 1) {
            requestNumber++;
            return firstResult;
          }
          return secondResult;
        });

        final resultEntity = executionResult.map(
            success: (res) => res.result.entity, failed: (_) => null);

        expect(resultEntity, "someEntity");
      });
    });

    group("Fail execution test on first execution >", () {
      late GrpcExecutorService grpcExecutorService;
      late AuthorizedUserEntity user;

      setUp(() {
        final authenticationRepository = AuthenticationRepositoryMock();
        final userRepository = UserRepositoryMock();

        user = AuthorizedUserEntityMock();

        when(() => userRepository.getCurrentUserState).thenAnswer(
          (invocation) async =>
              UserState.authorized(authorizedUserEntity: user),
        );

        grpcExecutorService = GrpcExecutorServiceImpl(
          userRepository: userRepository,
          authenticationRepository: authenticationRepository,
        );
      });

      test("Call connection fail", () async {
        final firstCall = ServerResultEntityMock();

        final result =
            await grpcExecutorService.securedExecute<ServerResultEntityMock>(
                () => throw const GrpcError.unavailable());

        final executionError = result.mapOrNull(failed: (value) => value.error);

        expect(executionError, const ExecutionError.connectionFailed());
      });
    });

    group('Fail execution on jwt refresh', () {
      late GrpcExecutorService grpcExecutorService;
      late AuthorizedUserEntity user;

      setUp(() {
        final authenticationRepository = AuthenticationRepositoryMock();
        final userRepository = UserRepositoryMock();

        user = AuthorizedUserEntityMock();

        when(() => userRepository.getCurrentUserState).thenAnswer(
          (invocation) async =>
              UserState.authorized(authorizedUserEntity: user),
        );

        when(() => authenticationRepository.refreshJwt(user))
            .thenThrow(const GrpcError.unavailable());

        grpcExecutorService = GrpcExecutorServiceImpl(
          userRepository: userRepository,
          authenticationRepository: authenticationRepository,
        );
      });

      test("Refused fail on refresh", () async {
        final firstCall = ServerResultEntityMock();

        const serverCodeResponse = ServerResultCode.unauthorized();
        when(() => firstCall.resultCode).thenReturn(serverCodeResponse);

        final result = await grpcExecutorService.securedExecute(
          () async => firstCall,
        );

        final executionError = result.mapOrNull(failed: (value) => value.error);

        expect(
          executionError,
          const ExecutionError.connectionFailed(),
        );
      });
    });

    group('Fail execution on final request', () {
      late GrpcExecutorService grpcExecutorService;
      late AuthorizedUserEntity user;

      setUp(() {
        final authenticationRepository = AuthenticationRepositoryMock();
        final userRepository = UserRepositoryMock();

        final refreshedUser = AuthorizedUserEntityMock();

        final refreshJwtResponse = JwtResultEntityMock();
        user = AuthorizedUserEntityMock();

        when(() => refreshJwtResponse.entity).thenReturn(refreshedUser);

        when(() => refreshJwtResponse.resultCode).thenReturn(const ServerResultCode.ok());

        when(() => userRepository.getCurrentUserState).thenAnswer(
          (invocation) async =>
              UserState.authorized(authorizedUserEntity: user),
        );

        when(() => authenticationRepository.refreshJwt(user))
            .thenAnswer((invocation) async => refreshJwtResponse);


        grpcExecutorService = GrpcExecutorServiceImpl(
          userRepository: userRepository,
          authenticationRepository: authenticationRepository,
        );
      });

      test("Call connection fail on refresh", () async {
        final firstCall = ServerResultEntityMock();

        int executionNumber = 0;
        const serverCodeResponse = ServerResultCode.unauthorized();

        when(() => firstCall.resultCode).thenReturn(serverCodeResponse);

        final result = await grpcExecutorService.securedExecute(
          () async {
            executionNumber++;

            if (executionNumber == 1) {
              return firstCall;
            }

            throw const GrpcError.unavailable();
          },
        );

        final executionError = result.mapOrNull(failed: (value) => value.error);

        expect(
          executionError,
          const ExecutionError.connectionFailed(),
        );
      });
    });
  });
}
