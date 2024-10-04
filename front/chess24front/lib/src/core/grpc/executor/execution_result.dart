import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/executor/execution_error.dart';

part 'execution_result.freezed.dart';

@freezed
class ExecutionResult<E> with _$ExecutionResult<E> {
  const ExecutionResult._();

  const factory ExecutionResult.success(
      {required ServerResultEntity<E> result}) = SuccessExecutionResult;

  const factory ExecutionResult.failed({required ExecutionError error}) =
      FailedExecutionResult;
}
