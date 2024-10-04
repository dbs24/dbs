import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/jwt_result_enity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';

abstract interface class InternalAuthenticationRepository {

  /// Internal refresher jwt tokens
  Future<JwtResultEntity> refreshJwt(
    AuthorizedUserEntity authorizedUser,
  );
}

/// Core implementation of internal authentication repository
class InternalAuthenticationRepositoryImpl
    implements InternalAuthenticationRepository {
  final AuthenticationDataProvider _authenticationDataProvider;

  /// Constructor
  InternalAuthenticationRepositoryImpl(
      {required AuthenticationDataProvider authenticationDataProvider})
      : _authenticationDataProvider = authenticationDataProvider;

  @override
  Future<JwtResultEntity> refreshJwt(AuthorizedUserEntity authorizedUser) =>
      _authenticationDataProvider.refreshJwt(authorizedUser);
}
