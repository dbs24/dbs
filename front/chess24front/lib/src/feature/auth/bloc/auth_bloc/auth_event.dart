part of 'auth_bloc.dart';

@freezed
class AuthEvent with _$AuthEvent {
  const factory AuthEvent.logout() = _Logout;

  const factory AuthEvent.authChanged({required UserState state}) = _AuthChanged;
}
