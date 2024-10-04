import 'package:sizzle_starter/src/core/grpc/generated/players-dto.pb.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';

class SignUpDataMapper
    implements ObjectMapper<SignUpData, CreateOrUpdatePlayerRequest> {
  @override
  CreateOrUpdatePlayerRequest map(SignUpData src) =>
      CreateOrUpdatePlayerRequest(
        login: src.username,
        email: src.email,
        password: src.password,
      );
}
