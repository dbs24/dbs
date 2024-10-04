import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_login_data_entity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/jwt_result_enity.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/default_login_request_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_login_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/entity/jwts_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/mapper/jwt_refresh_response_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-player-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-server-service.pbgrpc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/h2_auth_services.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/result_code/result_code_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/standard_response_mappers.dart';

/// Authorization Interface
abstract interface class AuthenticationDataProvider {
  /// Login via username and password
  Future<JwtResultEntity> defaultLogin(DefaultLoginDataEntity loginData);

  /// Refeshing jwt
  Future<JwtResultEntity> refreshJwt(AuthorizedUserEntity user);
}

/// RPC authorization
class AuthenticationNetworkRpcDataProvider
    implements AuthenticationDataProvider {
  final H2AuthServices _h2AuthServices;

  final JwtRefreshResponseMapper jwtRefreshResponseMapper;

  final JwtsMapper jwtsMapper;

  final StandardResponseMappers standardResponseMappers;

  /// Public consctructor
  AuthenticationNetworkRpcDataProvider(
      {required H2AuthServices h2AuthServices,
      required this.standardResponseMappers,
      required this.jwtRefreshResponseMapper,
      required this.jwtsMapper,})
      : _h2AuthServices = h2AuthServices;

  @override
  Future<JwtResultEntity> defaultLogin(DefaultLoginDataEntity loginData) async {
    final mappedRequest = DefaultLoginRequestMapper().map(loginData);

    final response = await _h2AuthServices.authServiceClient.playerLogin(
      mappedRequest,
    );

    final result = JwtLoginResponseMapper(
            standardResponseMappers: standardResponseMappers)
        .map(response);

    return result;
  }

  @override
  Future<JwtResultEntity> refreshJwt(AuthorizedUserEntity user) async {
    final request = RefreshPlayerJwtRequest(
      jwts: jwtsMapper.map(user),
    );

    final response =
        await _h2AuthServices.authServiceClient.refreshPlayerJwt(request);

    final result = jwtRefreshResponseMapper.map(response);

    return result;
  }
}
