// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_login_data_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_user_data.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/jwt_result_enity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/authentication_data_provider.dart';

/// Repository for authenticating users
abstract interface class AuthenticationRepository {
  /// Default authentication using login and password
  Future<JwtResultEntity> defaultAuthentication(
    DefaultLoginDataEntity userData,
  );

}

/// Implementation of [AuthenticationRepository]
class AuthenticationRepositoryImpl implements AuthenticationRepository {
  final AuthenticationDataProvider _authenticationDataProvider;

  AuthenticationRepositoryImpl({
    required AuthenticationDataProvider authenticationDataProvider,
  }) : _authenticationDataProvider = authenticationDataProvider;

  @override
  Future<JwtResultEntity> defaultAuthentication(
    DefaultLoginDataEntity userData,
  ) =>
      _authenticationDataProvider.defaultLogin(userData);

}
