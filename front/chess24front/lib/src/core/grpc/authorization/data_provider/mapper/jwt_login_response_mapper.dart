import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/google/protobuf/any.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/jwt-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/grpc_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';

class JwtLoginResponseMapper extends GrpcResponseMapper<PlayerLoginResponse,
    Jwts, AuthorizedUserEntity?> {
  JwtLoginResponseMapper(
      {required super.standardResponseMappers});

  @override
  Jwts get entityEmptyInstance => Jwts();

  @override
  AuthorizedUserEntity mapEntity(Jwts from) => AuthorizedUserEntity(
        refreshToken: from.refreshJwt,
        accessToken: from.accessJwt,
      );

  @override
  ResponseAnswer getResponseAnswer(PlayerLoginResponse response) =>
      response.responseAnswer;
}
