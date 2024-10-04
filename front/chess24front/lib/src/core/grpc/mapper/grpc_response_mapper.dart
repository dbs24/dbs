import 'package:meta/meta.dart';
import 'package:protobuf/protobuf.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/exception/bad_server_answer.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/result_code/result_code_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/standard_response_mappers.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_entity.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';

/// Abstract mapper for gRPC DTO's to out entites
/// TFrom - Type of the whole generated message
/// TFromResponseEntity - Type of ResponseEnity inside ResponseAnswer
/// TToCustomEntity - Type of entity to be conveted in;
abstract class GrpcResponseMapper<TFrom extends GeneratedMessage,
        TFromResponseEntity extends GeneratedMessage, TToCustomEntity>
    implements ObjectMapper<TFrom, ServerResultEntity<TToCustomEntity>> {
  final StandardResponseMappers standardResponseMappers;

  /// Public Contructor
  GrpcResponseMapper({required this.standardResponseMappers});

  /// Getter for common response
  @protected
  ResponseAnswer getResponseAnswer(TFrom response);

  /// Getter for an instance of entity, in which ResponseAnswer will be unpacked
  @protected
  TFromResponseEntity get entityEmptyInstance;

  /// Mapper from DTO entity to our entity
  @protected
  TToCustomEntity mapEntity(TFromResponseEntity from);

  @override
  ServerResultEntity<TToCustomEntity> map(TFrom response) {
    final answer = getResponseAnswer(response);

    final emptyInstanceToBeInUnpacked = entityEmptyInstance;

    final filledInstance = emptyInstanceToBeInUnpacked;

    final errors =
        standardResponseMappers.errorMapper.mapList(answer.errorMessages);

    final resultCode =
        standardResponseMappers.resultCodeMapper.map(answer.responseCode);

    resultCode.mapOrNull(
      ok: (_) {
        if (answer.responseEntity.hasValue()) {
          answer.responseEntity.unpackInto(filledInstance);
        } else {
          throw const EmptyOkAnswer();
        }
      },
    );

    final mappedEntity = mapEntity(filledInstance);

    return ServerResultEntity(
      entity: mappedEntity,
      errors: errors,
      resultCode: resultCode,
    );
  }
}
