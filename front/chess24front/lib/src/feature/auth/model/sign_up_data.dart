class SignUpData {
  final String username;
  final String password;
  final String repeatPassword;
  final String email;

  SignUpData(
      {required this.username,
      required this.password,
      required this.repeatPassword,
      required this.email});

  String? isPasswordsMatching() =>
      password == repeatPassword ? null : "Passwords mismatch";

  String? isValidPassword() => password.length >= 8
      ? null
      : "Password length should be more or equal 8 symbols";

  String? isValidUsername() => username.length >= 4
      ? null
      : "Username length should be more or equal 4 symbols";

  String? isValidEmail() =>
      RegExp(r"([a-z]|[A-Z][0-9]|_|\\+|=)+@([a-z]|[A-Z][0-9]|_|\\+|=)+")
              .hasMatch(email)
          ? null
          : "Not valid email";
}