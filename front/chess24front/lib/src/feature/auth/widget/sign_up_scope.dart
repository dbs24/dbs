import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:sizzle_starter/src/core/utils/extensions/context_extension.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/sign_up/sign_up_bloc.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';

abstract interface class SignUpController {
  void simpleSignUp();
  void defaultSignUp(SignUpData signUpData);
}

class SignUpScope extends StatefulWidget {
  const SignUpScope({required this.signUpBloc, required this.child, super.key});

  final SignUpBloc signUpBloc;

  final Widget child;

  static SignUpController of(BuildContext context) =>
      context.inhOf<_InheritSignUp>().signUpController;

  static SignUpState signUpStateOf(
    BuildContext context, {
    bool listen = true,
  }) =>
      context.inhOf<_InheritSignUp>(listen: listen).signUpState;

  @override
  State<SignUpScope> createState() => _SignUpScopeState();
}

class _SignUpScopeState extends State<SignUpScope> implements SignUpController {
  @override
  Widget build(BuildContext context) => BlocBuilder<SignUpBloc, SignUpState>(
        bloc: widget.signUpBloc,
        builder: (context, state) => _InheritSignUp(
          signUpController: this,
          signUpState: state,
          child: widget.child,
        ),
      );

  @override
  void simpleSignUp() {
    widget.signUpBloc.add(const SignUpEvent.withoutRegistrationSignUp());
  }

  @override
  void defaultSignUp(SignUpData signUpData) {
    widget.signUpBloc
        .add(SignUpEvent.defaultSignUp(signUpData: signUpData));
  }
}

class _InheritSignUp extends InheritedWidget {
  const _InheritSignUp({
    required super.child,
    required this.signUpController,
    required this.signUpState,
  });

  final SignUpController signUpController;

  final SignUpState signUpState;
  @override
  bool updateShouldNotify(_InheritSignUp oldWidget) => true;
}
