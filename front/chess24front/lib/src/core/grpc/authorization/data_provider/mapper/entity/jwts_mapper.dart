import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/jwt-response.pb.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

class JwtsMapper implements ObjectMapper<AuthorizedUserEntity, Jwts> {
  @override
  Jwts map(AuthorizedUserEntity src) => Jwts(
      accessJwt: src.accessToken,
      refreshJwt: src.refreshToken
    );
}