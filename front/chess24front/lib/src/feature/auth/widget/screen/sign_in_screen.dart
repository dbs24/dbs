import 'package:auto_route/auto_route.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';
import 'package:sizzle_starter/src/feature/auth/widget/sign_in_scope.dart';
import 'package:sizzle_starter/src/feature/auth/widget/sign_up_scope.dart';
import 'package:sizzle_starter/src/feature/auth/widget/validation_text_field.dart';
import 'package:sizzle_starter/src/feature/route/app_router.dart';
import 'package:sizzle_starter/src/feature/widget/overlay_loading/overlay_loading.dart';

class DefaultSignInData {
  final String username;
  final String password;

  DefaultSignInData({required this.username, required this.password});

  String? isValidUsername() =>
      username.length > 4 ? null : "Username must be 5 or more symbols";
  String? isValidPassword() =>
      password.length >= 8 ? null : "Password must be 8 or more symbols";
}

@RoutePage()
class SignInScreen extends StatelessWidget {
  const SignInScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final signInState = SignInScope.stateOf(context);
    print('inProgress: ${signInState.inProgress}');
    return OverlayLoading(
      isLoading: signInState.inProgress,
      loadingWidget: const Card(child: CircularProgressIndicator()),
      child: const SignInScreenView(),
    );
  }
}

class SignInScreenView extends StatefulWidget {
  const SignInScreenView({super.key});

  @override
  State<SignInScreenView> createState() => _SignInScreenViewState();
}

class _SignInScreenViewState extends State<SignInScreenView> {
  late final TextEditingController usernameController;
  late final TextEditingController passwordController;

  late final List<TextEditingController> _controllers;
  late final Listenable _observer;

  late final ValueNotifier<String?> _usernameError;
  late final ValueNotifier<String?> _passwordError;

  late final List<ValueNotifier<String?>> _errors;

  late final List<String? Function(DefaultSignInData data)> _validations;

  late final ValueNotifier<bool> _validNotifier;

  late DefaultSignInData _defaultSignInData;

  @override
  Widget build(BuildContext context) => Scaffold(
        body: Column(
          children: [
            Expanded(
              flex: 6,
              child: Container(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text("LOGO"),
                    Padding(
                      padding: const EdgeInsets.all(12.0),
                      child: Column(
                        children: [
                          ValidationTextField(
                            error: _usernameError,
                            decorationLabel: const Text("Password"),
                            controller: usernameController,
                          ),
                          ValidationTextField(
                            error: _passwordError,
                            controller: passwordController,
                            decorationLabel: const Text("Username"),
                          ),
                          const SizedBox(
                            height: 15,
                          ),
                          Align(
                            alignment: Alignment.centerRight,
                            child: ListenableBuilder(
                              listenable: _validNotifier,
                              builder: (context, child) => ElevatedButton(
                                onPressed: SignInScope.stateOf(context)
                                            .inProgress ||
                                        !_validNotifier.value
                                    ? null
                                    : () {
                                        SignInScope.controllerOf(context)
                                            .defaultSignIn(
                                          login: _defaultSignInData.username,
                                          password: _defaultSignInData.password,
                                        );
                                      },
                                child: child,
                              ),
                              child: const Text("Sign In"),
                            ),
                          ),
                          const SizedBox(
                            height: 15,
                          ),
                          const _AlterStartPlayMethodWidget(),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const Expanded(flex: 4, child: SizedBox()),
          ],
        ),
      );
  @override
  void initState() {
    super.initState();

    usernameController = TextEditingController();
    passwordController = TextEditingController();

    _usernameError = ValueNotifier(null);
    _passwordError = ValueNotifier(null);

    _validations = [
      (data) => _usernameError.value = data.isValidUsername(),
      (data) => _passwordError.value = data.isValidPassword(),
    ];

    _controllers = [usernameController, passwordController];

    _errors = [_usernameError, _passwordError];

    _validNotifier = ValueNotifier(false);

    _observer = Listenable.merge(_controllers);

    _observer.addListener(_onChanged);
  }

  void _onChanged() {
    _defaultSignInData = DefaultSignInData(
        username: usernameController.text, password: passwordController.text);

    _validNotifier.value = _validate(_defaultSignInData);
  }

  bool _validate(DefaultSignInData signInData) {
    for (final validation in _validations) {
      if (validation(signInData) != null) {
        return false;
      }
    }

    return true;
  }

  @override
  void dispose() {
    _observer.removeListener(_onChanged);

    for (final controller in _controllers) {
      controller.dispose();
    }

    for (final error in _errors) {
      error.dispose();
    }

    _validNotifier.dispose();
    super.dispose();
  }
}

class _AlterStartPlayMethodWidget extends StatelessWidget {
  const _AlterStartPlayMethodWidget();

  @override
  Widget build(BuildContext context) => RichText(
        text: TextSpan(
          text: 'A new user? ',
          children: [
            TextSpan(
              text: 'Sign up',
              style: const TextStyle(color: Colors.blue),
              recognizer: TapGestureRecognizer()
                ..onTap = () => context.pushRoute(const SignUpRoute()),
            ),
            const TextSpan(text: " or play "),
            TextSpan(
              text: 'without registration',
              style: const TextStyle(color: Colors.blue),
              recognizer: TapGestureRecognizer()
                ..onTap = () => SignUpScope.of(context).simpleSignUp(),
            ),
          ],
        ),
      );
}
