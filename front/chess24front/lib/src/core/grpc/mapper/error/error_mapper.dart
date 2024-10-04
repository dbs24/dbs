import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_entity.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';

class ErrorMapper extends ComplexObjectMapper<ErrorMessage, ErrorEntity> {
  @override
  ErrorEntity map(ErrorMessage src) =>
      ErrorEntity(code: src.code, description: src.message);
}
