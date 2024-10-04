import 'package:auto_route/auto_route.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:sizzle_starter/src/feature/auth/bloc/sign_up/sign_up_bloc.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';
import 'package:sizzle_starter/src/feature/auth/widget/sign_up_scope.dart';
import 'package:sizzle_starter/src/feature/auth/widget/validation_text_field.dart';
import 'package:sizzle_starter/src/feature/widget/overlay_loading/overlay_loading.dart';

@RoutePage()
class SignUpScreen extends StatefulWidget {
  const SignUpScreen({super.key});

  @override
  State<SignUpScreen> createState() => _SignUpScreenState();
}

class _SignUpScreenState extends State<SignUpScreen> {
  late SignUpState _signUpState;

  @override
  Widget build(BuildContext context) => OverlayLoading(
        isLoading: _signUpState.inProgress,
        loadingWidget: const Card(
          child: CircularProgressIndicator(),
        ),
        child: SignUpScreenView(),
      );

  @override
  void initState() {
    super.initState();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    _signUpState = SignUpScope.signUpStateOf(context);
  }
}

class SignUpScreenView extends StatefulWidget {
  SignUpScreenView({
    super.key,
  });

  @override
  State<SignUpScreenView> createState() => _SignUpScreenViewState();
}

class _SignUpScreenViewState extends State<SignUpScreenView> {
  final _usernameController = TextEditingController();

  final _passwordFirstController = TextEditingController();

  final _passwordSecondController = TextEditingController();

  final _emailController = TextEditingController();

  late SignUpController _signUpController;

  late final List<TextEditingController> _controllers;

  late final Listenable _observer;

  final ValueNotifier<String?> _usernameError = ValueNotifier<String?>(null);

  final ValueNotifier<String?> _passwordFirstError =
      ValueNotifier<String?>(null);

  final ValueNotifier<String?> _passwordSecondError =
      ValueNotifier<String?>(null);

  final ValueNotifier<String?> _emailError = ValueNotifier<String?>(null);

  final ValueNotifier<bool> _validNotifier = ValueNotifier<bool>(false);

  late final List<String? Function(SignUpData data)> _validators = [
    (data) => _usernameError.value = data.isValidUsername(),
    (data) => _emailError.value = data.isValidEmail(),
    (data) => _passwordFirstError.value = data.isValidPassword(),
    (data) => _passwordSecondError.value = data.isPasswordsMatching(),
  ];

  late SignUpData signUpData;

  late SignUpState _signUpState;

  @override
  Widget build(BuildContext context) => Scaffold(
        body: CustomScrollView(
          slivers: [
            const SliverAppBar(
              title: Text("Sign Up"),
            ),
            SliverList.list(
              children: [
                ValidationTextField(
                  error: _usernameError,
                  controller: _usernameController,
                  decorationLabel: const Text("Your username"),
                ),
                ValidationTextField(
                  error: _emailError,
                  controller: _emailController,
                  decorationLabel: const Text("Your email"),
                ),
                ValidationTextField(
                  error: _passwordFirstError,
                  controller: _passwordFirstController,
                  decorationLabel: const Text("Your password"),
                ),
                ValidationTextField(
                  error: _passwordSecondError,
                  controller: _passwordSecondController,
                  decorationLabel: const Text("Repeat password"),
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    SizedBox(
                      width: 300,
                      child: Text(
                        _signUpState.mapOrNull(
                              error: (value) => value.error,
                            ) ??
                            "",
                        maxLines: 20,
                        softWrap: true,
                      ),
                    ),
                    ListenableBuilder(
                      builder: (context, child) => FilledButton(
                        onPressed: _validNotifier.value
                            ? () {
                                _signUpController.defaultSignUp(signUpData);
                              }
                            : null,
                        child: child,
                      ),
                      listenable: _validNotifier,
                      child: const Text("Sign up"),
                    ),
                  ],
                ),
              ],
            ),
          ],
        ),
      );

  @override
  void initState() {
    super.initState();
    _controllers = [
      _usernameController,
      _passwordFirstController,
      _passwordSecondController,
      _emailController,
    ];

    _observer = Listenable.merge(_controllers)..addListener(_onChanged);
  }

  void _onChanged() {
    signUpData = SignUpData(
      username: _usernameController.text,
      password: _passwordFirstController.text,
      repeatPassword: _passwordSecondController.text,
      email: _emailController.text,
    );
    _validNotifier.value = _validate(signUpData);
  }

  bool _validate(SignUpData signUpData) {
    for (final validator in _validators) {
      if (validator(signUpData) != null) {
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

    _validNotifier.dispose();

    super.dispose();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    _signUpController = SignUpScope.of(context);
    _signUpState = SignUpScope.signUpStateOf(context);
  }
}
