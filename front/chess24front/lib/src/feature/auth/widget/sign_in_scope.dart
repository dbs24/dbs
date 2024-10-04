import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:sizzle_starter/src/core/utils/extensions/context_extension.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/sign_in/sign_in_bloc.dart';

abstract interface class DefaultSignIn {
  void defaultSignIn({required String login, required String password});
}

class SignInScope extends StatefulWidget {
  const SignInScope({required this.signInBloc, required this.child, super.key});

  final SignInBloc signInBloc;

  final Widget child;

  @override
  State<SignInScope> createState() => _SignInScopeState();

  static SignInState stateOf(BuildContext context, {bool listen = true}) =>
      context.inhOf<_InheritSignIn>(listen: listen).signInState;

  static DefaultSignIn controllerOf(BuildContext context) =>
      context.inhOf<_InheritSignIn>().signInScopeController;
}

class _SignInScopeState extends State<SignInScope> implements DefaultSignIn {
  @override
  Widget build(BuildContext context) => BlocBuilder<SignInBloc, SignInState>(
        bloc: widget.signInBloc,
        builder: (context, state) => _InheritSignIn(
          signInState: state,
          signInScopeController: this,
          child: widget.child,
        ),
      );

  @override
  void defaultSignIn({required String login, required String password}) {
    widget.signInBloc.add(SignInEvent.defaultSignIn(login, password));
  }
}

class _InheritSignIn extends InheritedWidget {
  const _InheritSignIn(
      {required this.signInState,
      required this.signInScopeController,
      required super.child,});

  final SignInState signInState;

  final DefaultSignIn signInScopeController;

  @override
  bool updateShouldNotify(_InheritSignIn oldWidget) =>
      signInState != oldWidget.signInState;
}
