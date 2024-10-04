import 'package:auto_route/auto_route.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/feature/route/app_router.dart';

/// Secures route from unauthorized access
class AuthRouteGuard extends AutoRouteGuard {
  final UserRepository authorizationService;

  // Guard for secured routes (authorized users)
  AuthRouteGuard(this.authorizationService);

  @override
  Future<void> onNavigation(
    NavigationResolver resolver,
    StackRouter router,
  ) async {
    if (await authorizationService.userStateStream.last == null) {
      //await resolver.redirect(const LoginRoute());

      return;
    }

    resolver.next();
  }
}
