import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_code.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_entity.dart';

class ServerResultEntity<E> {
  final List<ErrorEntity> errors;

  final ServerResultCode resultCode;

  final E entity;

  ServerResultEntity(
      {required this.entity,
      required this.errors,
      required this.resultCode});

  @override
  String toString() =>
      'JwtResultEntity(authorizedUserEntity: $entity, errors: $errors)';
}
