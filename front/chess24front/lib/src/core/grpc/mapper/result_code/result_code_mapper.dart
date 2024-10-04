import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_code.dart';
import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';

class ResultCodeMapper implements ObjectMapper<ResponseCode, ServerResultCode> {
  final _map = <ResponseCode, ServerResultCode>{
    ResponseCode.RC_OK: const ServerResultCode.ok(),
    ResponseCode.RC_BAD_GATEWAY: const ServerResultCode.badGateway(),
    ResponseCode.RC_FORBIDDEN: const ServerResultCode.forbidden(),
    ResponseCode.RC_INTERNAL_ERROR: const ServerResultCode.internalError(),
    ResponseCode.RC_INVALID_REQUEST_DATA:
        const ServerResultCode.invalidRequestData(),
    ResponseCode.RC_INVALID_RESPONSE_DATA:
        const ServerResultCode.invalidResponseData(),
    ResponseCode.RC_SERVICE_UNAVAILABLE:
        const ServerResultCode.serviceUnavailable(),
    ResponseCode.RC_UNAUTHORIZED: const ServerResultCode.unauthorized()
  };
  @override
  ServerResultCode map(ResponseCode src) {
    ////////////////////////!!!!!!!!!!!!!!!!!!!!!!!! !!!!! !!!!
    return _map[src]!;
  }
}
