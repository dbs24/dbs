import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_login_data_entity.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';

class DefaultLoginRequestMapper
    extends ComplexObjectMapper<DefaultLoginDataEntity, PlayerLoginRequest> {
  @override
  PlayerLoginRequest map(DefaultLoginDataEntity src) =>
      PlayerLoginRequest(playerLogin: src.login, playerPassword: src.password);
}
