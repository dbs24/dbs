import 'package:auto_route/auto_route.dart';
import 'package:flutter/material.dart';
import 'package:sizzle_starter/src/feature/auth/widget/auth_scope.dart';

@RoutePage()
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) => Scaffold(
        body: CustomScrollView(
          slivers: [
            const SliverAppBar(title: Text("Chess24")),
            SliverToBoxAdapter(
              child: ElevatedButton(
                  onPressed: () {
                    AuthScope.of(context).logout();
                  },
                  child: const Text("Log out"),),
            ),
          ],
        ),
        bottomNavigationBar: NavigationBar(
          destinations: const [
            NavigationDestination(icon: Icon(Icons.play_arrow), label: "Play"),
            NavigationDestination(icon: Icon(Icons.person), label: "Profile"),
          ],
          onDestinationSelected: (value) {},
        ),
      );
}
