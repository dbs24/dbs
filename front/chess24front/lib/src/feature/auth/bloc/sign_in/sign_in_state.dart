part of 'sign_in_bloc.dart';

@freezed
class SignInState with _$SignInState {

  const SignInState._();

  bool get inProgress => maybeMap<bool>(orElse: () => false, processing: (_) => true);

  const factory SignInState.idle({
    Object? cause,
  }) = _IdleState;

  const factory SignInState.processing({
    Object? cause,
  }) = _ProcessingState;

  const factory SignInState.error({
    Object? cause,
  }) = _ErrorState;

  const factory SignInState.passed({
    Object? cause,
  }) = _PassedState;
}
