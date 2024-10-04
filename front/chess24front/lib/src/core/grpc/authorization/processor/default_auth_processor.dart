import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_login_data_entity.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_login_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/processor/auth_processor.dart';

class DefaultAuthProcessor implements AuthProcessor {
  final String username;
  final String password;

  final AuthenticationDataProvider userDataProvider;

  DefaultAuthProcessor({
    required this.username,
    required this.password,
    required this.userDataProvider,
  });

  @override
  Future<AuthProcessorResult> authorize() async {
    final response = await userDataProvider.defaultLogin(
      DefaultLoginDataEntity(
        login: username,
        password: password,
      ),
    );
    final user = response.entity;

    return AuthProcessorResult(
      errors: response.errors,
      authorizedUserEntity: user == null
          ? null
          : AuthorizedUserEntity(
              refreshToken: user.refreshToken,
              accessToken: user.accessToken,
            ),
    );
  }
}
