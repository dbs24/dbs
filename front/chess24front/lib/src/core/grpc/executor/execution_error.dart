import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_code.dart';

part 'execution_error.freezed.dart';

@freezed
class ExecutionError with _$ExecutionError {
  const ExecutionError._();
  const factory ExecutionError.connectionFailed({String? message}) = _ConnectionFailedExecutionError;

  const factory ExecutionError.refreshAccessTokenRefused({
    required ServerResultCode resultCode,
  }) = _RefreshAccessTokenRefused;

}
