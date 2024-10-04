// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'dart:io';

import 'package:grpc/grpc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_code.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/internal_authentication_repository.dart';

import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/core/grpc/executor/execution_error.dart';
import 'package:sizzle_starter/src/core/grpc/executor/execution_result.dart';

import 'package:sizzle_starter/src/core/grpc/executor/execution_exception.dart';
import 'package:sizzle_starter/src/core/grpc/executor/internal/internal_execution_error.dart';
import 'package:sizzle_starter/src/core/grpc/executor/internal/internal_execution_result.dart';


typedef GrpcCall<E> = Future<ServerResultEntity<E>> Function();

abstract interface class GrpcExecutorService {
  Future<ExecutionResult<E>> securedExecute<E>(GrpcCall<E> call);
  Future<ExecutionResult<E>> execute<E>(GrpcCall<E> call);
}

class GrpcExecutorServiceImpl implements GrpcExecutorService {
  final InternalAuthenticationRepository authenticationRepository;
  final UserRepository userRepository;
  GrpcExecutorServiceImpl({
    required this.userRepository,
    required this.authenticationRepository,
  });

  @override
  Future<ExecutionResult<E>> securedExecute<E>(
    GrpcCall<E> call,
  ) async {
    final firstExecutionResult = await _handleExecution(call);

    return firstExecutionResult.map(
      success: (result) => _processFirstSuccessfulExecution(result, call),
      failed: _processFirstFailedExecution,
    );
  }

  @override
  Future<ExecutionResult<E>> execute<E>(GrpcCall<E> call) async {
    final executionResult = await _handleExecution(call);
    executionResult.mapOrNull(
      success: (value) => value.result.resultCode.mapOrNull(
        unauthorized: (_) => throw WrongPointTypeException(pointDetails: call),
      ),
    );

    return executionResult.map(
      success: (res) => ExecutionResult.success(result: res.result),
      failed: (res) => ExecutionResult.failed(
        error: res.error.map(
          connectionUnavailable: (c) =>
              ExecutionError.connectionFailed(message: c.message),
        ),
      ),
    );
  }

  Future<InternalExecutionResult<E>> _handleExecution<E>(
    GrpcCall<E> call,
  ) async {
    try {
      final result = await call();

      return InternalExecutionResult.success(result: result);

      // example: connection timeout;
    } on GrpcError catch (error) {
      // need to check the grpc error of connection timeout

      final executionError = _mapGrpcErrorToExecutionError(error);

      if (executionError == null) {
        rethrow;
      }

      return InternalExecutionResult.failed(error: executionError);
    }
  }

  InternalExecutionError? _mapGrpcErrorToExecutionError(GrpcError error) {
    final map = <int, InternalExecutionError>{
      StatusCode.unavailable:
          InternalExecutionError.connectionUnavailable(message: error.message),
    };

    return map[error.code];
  }

  Future<ExecutionResult<E>> _processFirstSuccessfulExecution<E>(
    SuccessInternalExecutionResult<E> executionResult,
    GrpcCall<E> execution,
  ) {
    ExecutionError $mapRefreshError(InternalExecutionError err) => err.map(
        connectionUnavailable: (c) =>
            ExecutionError.connectionFailed(message: c.message));

    Future<ExecutionResult<E>> $processJwtRefresh() async {
      final user = await userRepository.getCurrentUserState;

      return user.map(
        authorized: (state) async {
          final refreshResult = await _handleExecution(
            () =>
                authenticationRepository.refreshJwt(state.authorizedUserEntity),
          );

          return refreshResult.map(
            success: (jwtRefreshResult) async =>
                jwtRefreshResult.result.resultCode.maybeMap(
                    ok: (okRefreshResult) async {
                      final resultWithRefreshedToken =
                          await _handleExecution(execution);

                      return resultWithRefreshedToken.map(
                        success: (execResult) =>
                            execResult.result.resultCode.maybeMap(
                          orElse: () => ExecutionResult.success(
                            result: execResult.result,
                          ),
                        ),
                        failed: (execResult) => ExecutionResult.failed(
                          error: execResult.error.map(
                            connectionUnavailable: (_) =>
                                const ExecutionError.connectionFailed(),
                          ),
                        ),
                      );
                    },
                    orElse: () => ExecutionResult.failed(
                        error: ExecutionError.refreshAccessTokenRefused(
                            resultCode: jwtRefreshResult.result.resultCode))),
            failed: (value) =>
                ExecutionResult.failed(error: $mapRefreshError(value.error)),
          );
        },
        empty: (state) => const ExecutionResult.failed(
          error: ExecutionError.connectionFailed(),
        ),
      );
    }

    final serverCode = executionResult.result.resultCode;
    return serverCode.maybeMap(
        ok: (_) async =>
            ExecutionResult.success(result: executionResult.result),
        unauthorized: (_) async => (await $processJwtRefresh()).map(
              success: (res) => ExecutionResult.success(result: res.result),
              failed: (res) => ExecutionResult.failed(
                error: res.error,
              ),
            ),
        orElse: () async =>
            ExecutionResult.success(result: executionResult.result));
  }

  Future<ExecutionResult<E>> _processFirstFailedExecution<E>(
    FailedInternalExecutionResult<E> executionResult,
  ) async =>
      ExecutionResult.failed(
          error: executionResult.error.map(
        connectionUnavailable: (c) =>
            ExecutionError.connectionFailed(message: c.message),
      ));
}

class SuccessExecutionResult {}
