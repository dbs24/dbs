import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

part 'jwt_refresh_result.freezed.dart';

@freezed
class JwtRefreshResult with _$JwtRefreshResult{
  const JwtRefreshResult._();

  const factory JwtRefreshResult.success({
    required AuthorizedUserEntity user
  }) = _Success;

  const factory JwtRefreshResult.refreshTokenExpired() = _RefreshTokenExpired;
}