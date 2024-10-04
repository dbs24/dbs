import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

/// Authorization Data interface
abstract interface class UserStorageDataProvider {
  /// Getting user
  Future<AuthorizedUserEntity?> getUser();

  /// Saving user
  Future<void> setUser(AuthorizedUserEntity? user);
}
