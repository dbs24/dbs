import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/jwt-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/grpc_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

class JwtRefreshResponseMapper extends GrpcResponseMapper<
    RefreshPlayerJwtResponse, Jwts, AuthorizedUserEntity> {
  JwtRefreshResponseMapper({required super.standardResponseMappers});

  @override
  Jwts get entityEmptyInstance => Jwts();

  @override
  ResponseAnswer getResponseAnswer(RefreshPlayerJwtResponse response) =>
      response.responseAnswer;

  @override
  AuthorizedUserEntity mapEntity(Jwts from) => AuthorizedUserEntity(
      refreshToken: from.refreshJwt, accessToken: from.accessJwt);
}
