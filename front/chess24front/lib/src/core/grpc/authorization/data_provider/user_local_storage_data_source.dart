import 'dart:async';

import 'package:sizzle_starter/src/core/utils/preferences_dao.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/user_storage_data_source.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

/// [UserLocalStorageDataSource] is used to store authorization
/// data of user on the local device
final class UserLocalStorageDataSource extends PreferencesDao
    implements UserStorageDataProvider {
  /// Public constructor
  UserLocalStorageDataSource({required super.sharedPreferences});

  @override
  Future<AuthorizedUserEntity?> getUser() async {
    final accessToken = _accessTokenEntry.read();
    final refreshToken = _refreshTokenEntry.read();

    if (refreshToken == null || accessToken == null) return null;

    return AuthorizedUserEntity(
      refreshToken: refreshToken,
      accessToken: accessToken,
    );
  }

  PreferencesEntry<String> get _accessTokenEntry =>
      stringEntry("auth.accessToken");

  PreferencesEntry<String> get _refreshTokenEntry =>
      stringEntry("auth.refreshToken");

  @override
  Future<void> setUser(AuthorizedUserEntity? user) async {
    if (user == null) {
      await _accessTokenEntry.remove();
      await _refreshTokenEntry.remove();
    } else {
      await _accessTokenEntry.set(user.accessToken);
      await _refreshTokenEntry.set(user.refreshToken);
    }
  }
}
