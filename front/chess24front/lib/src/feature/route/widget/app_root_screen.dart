import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_state.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/feature/auth/widget/auth_scope.dart';
import 'package:sizzle_starter/src/feature/initialization/widget/dependencies_scope.dart';
import 'package:sizzle_starter/src/feature/route/app_router.dart';

@RoutePage()
class AppRootScreen extends StatelessWidget {
  const AppRootScreen({super.key});

  @override
  Widget build(BuildContext context) => AuthScope(
        authBloc: DependenciesScope.of(context).authBloc,
        child: Builder(
          builder: (context) {
            final UserState userState = AuthScope.of(context).userState;
            return AutoRouter.declarative(
                routes: (handle) => [
                      userState.map(
                          authorized: (_) => const HomeRoute(),
                          empty: (_) => const AuthRoute(),)
                    ]);
          },
        ),
      );
}
