import 'package:freezed_annotation/freezed_annotation.dart';

part 'internal_execution_error.freezed.dart';

@freezed
class InternalExecutionError with _$InternalExecutionError {
  const InternalExecutionError._();

  const factory InternalExecutionError.connectionUnavailable({String? message}) =
      _ConnectionUnavailable;
}
