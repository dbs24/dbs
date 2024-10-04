import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

part 'user_state.freezed.dart';

@freezed
class UserState with _$UserState {

  const UserState._();
  
  const factory UserState.authorized({
    required AuthorizedUserEntity authorizedUserEntity,
  }) = _AuthorizedState;
  const factory UserState.empty() = _EmptyState;
}