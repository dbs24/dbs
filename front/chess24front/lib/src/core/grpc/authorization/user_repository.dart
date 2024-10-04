import 'dart:async';

import 'package:rxdart/subjects.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/user_storage_data_source.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_state.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
/// Interface of auth service
abstract interface class UserRepository {
  /// stream
  Stream<UserState> get userStateStream;

  /// Stored user
  Future<UserState> get getCurrentUserState;

  /// Logging out user
  Future<void> logout();

  /// Sign in using token
  Future<void> signIn(AuthorizedUserEntity user);
}

/// Service which let us know whether user authorized or not
/// and manipulate with it status
class UserRepositoryImpl implements UserRepository {
  final UserStorageDataProvider _userDataSource;

  final BehaviorSubject<UserState> _userStateStream;

  UserRepositoryImpl(
    UserState initialState, {
    required UserStorageDataProvider userDataSource,
  })  : _userDataSource = userDataSource,
        _userStateStream = BehaviorSubject.seeded(initialState);

  @override
  Future<void> logout() async {
    await _userDataSource.setUser(null);

    _userStateStream.add(const UserState.empty());
  }

  @override
  Stream<UserState> get userStateStream => _userStateStream;

  @override
  Future<UserState> get getCurrentUserState async {
    final user = await _userDataSource.getUser();

    return user == null
        ? const UserState.empty()
        : UserState.authorized(authorizedUserEntity: user);
  }

  @override
  Future<void> signIn(AuthorizedUserEntity user) async {
    await _userDataSource.setUser(user);
    _userStateStream.add(UserState.authorized(authorizedUserEntity: user));
  }
}
