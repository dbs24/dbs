import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';

part 'server_result_code.freezed.dart';

@freezed
class ServerResultCode with _$ServerResultCode {
  const ServerResultCode._();

  const factory ServerResultCode.ok() = _Ok;
  const factory ServerResultCode.forbidden() = _Forbidden;
  const factory ServerResultCode.badGateway() = _BadGateway;
  const factory ServerResultCode.internalError() = _InternalError;
  const factory ServerResultCode.invalidRequestData() = _InvalidRequestData;
  const factory ServerResultCode.invalidResponseData() = _InvalidResponseData;
  const factory ServerResultCode.serviceUnavailable() = _ServiceUnavailable;
  const factory ServerResultCode.unauthorized() = _Unauthorized;
}
