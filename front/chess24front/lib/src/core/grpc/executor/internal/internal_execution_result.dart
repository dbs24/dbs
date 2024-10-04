import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/executor/internal/internal_execution_error.dart';

part 'internal_execution_result.freezed.dart';

@freezed
class InternalExecutionResult<E> with _$InternalExecutionResult<E> {
  const InternalExecutionResult._();

  const factory InternalExecutionResult.success(
          {required final ServerResultEntity<E> result}) =
      SuccessInternalExecutionResult;

  const factory InternalExecutionResult.failed({
    required final InternalExecutionError error,
  }) = FailedInternalExecutionResult;
}
