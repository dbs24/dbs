import 'package:auto_route/auto_route.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';
import 'package:sizzle_starter/src/feature/auth/widget/screen/auth_screen.dart';
import 'package:sizzle_starter/src/feature/auth/widget/screen/sign_in_screen.dart';
import 'package:sizzle_starter/src/feature/auth/widget/screen/sign_up_screen.dart';
import 'package:sizzle_starter/src/feature/home/widget/home_screen.dart';
import 'package:sizzle_starter/src/feature/home/widget/page/lobby_page.dart';
import 'package:sizzle_starter/src/feature/home/widget/page/play_page.dart';
import 'package:sizzle_starter/src/feature/home/widget/page/profile_page.dart';
import 'package:sizzle_starter/src/feature/route/widget/app_root_screen.dart';
import 'package:sizzle_starter/src/feature/settings_test/widget/settings_test_screen.dart';

part 'app_router.gr.dart';

@AutoRouterConfig()
class AppRouter extends _$AppRouter {
  final UserRepository authorizationService;

  AppRouter({required this.authorizationService});

  @override
  List<AutoRoute> get routes => [
        AutoRoute(page: SettingsTestRoute.page),
        AutoRoute(path: "/", page: AppRootRoute.page, initial: true, children: [
          AutoRoute(
            page: HomeRoute.page,
            path: "home",
          ),
          AutoRoute(
            page: AuthRoute.page,
            path: "auth",
            children: [
              AutoRoute(page: SignInRoute.page, initial: true, path: "sign_in"),
              AutoRoute(page: SignUpRoute.page, path: "sign_up"),
            ],
          ),
        ],),
      ];
}
