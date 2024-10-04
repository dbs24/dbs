part of 'sign_up_bloc.dart';

@freezed
class SignUpState with _$SignUpState {

  const SignUpState._();

  bool get inProgress => maybeMap(orElse: () => false, processing: (_) => true);

  const factory SignUpState.idle() = _Idle;

  const factory SignUpState.processing() = _Processing;

  const factory SignUpState.error({String? error}) = _Error;

  const factory SignUpState.success() = _Success;
}
