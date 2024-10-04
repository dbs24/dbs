import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_state.dart';
import 'package:sizzle_starter/src/core/utils/extensions/context_extension.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/auth_bloc/auth_bloc.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';

/// Controller which deals with authorization
abstract interface class AuthScopeController {
  void logout();

  UserState get userState;
}

enum _AuthScopeAspect { authorization }

class AuthScope extends StatefulWidget {
  const AuthScope({required this.authBloc, required this.child, super.key});

  final AuthBloc authBloc;

  final Widget child;

  static AuthScopeController of(BuildContext context, {bool listen = true}) =>
      context.inhOf<_InheritedAuthScope>(listen: listen).authScopeController;

  @override
  State<AuthScope> createState() => _AuthScopeState();
}

class _AuthScopeState extends State<AuthScope> implements AuthScopeController {
  @override
  Widget build(BuildContext context) => BlocBuilder<AuthBloc, AuthState>(
        bloc: widget.authBloc,
        builder: (context, state) => _InheritedAuthScope(
          authScopeController: this,
          authState: state,
          child: widget.child,
        ),
      );

  @override
  void logout() {
    widget.authBloc.add(const AuthEvent.logout());
  }

  @override
  UserState get userState => widget.authBloc.state.userState;
}

class _InheritedAuthScope extends InheritedModel<_AuthScopeAspect> {
  final AuthScopeController authScopeController;

  final AuthState authState;

  const _InheritedAuthScope({
    required super.child,
    required this.authScopeController,
    required this.authState,
  });

  @override
  bool updateShouldNotify(_InheritedAuthScope oldWidget) =>
      authState != oldWidget.authState;

  @override
  bool updateShouldNotifyDependent(
    _InheritedAuthScope oldWidget,
    Set<_AuthScopeAspect> dependencies,
  ) {
    var shouldNotify = false;

    if (dependencies.contains(_AuthScopeAspect.authorization)) {
      shouldNotify = shouldNotify || authState != oldWidget.authState;
    }

    return shouldNotify;
  }
}
