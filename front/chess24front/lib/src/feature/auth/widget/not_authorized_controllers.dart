import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/default_user_data.dart';

/// A controller that deals with user sign in
abstract interface class SignInScopeController {

  /// Default Sign In to accoung using username and password
  void defaultSignIn(DefaultUserData userData);
}


/// A controller that deals with user sign up
abstract interface class SignUpScopeController {

  /// Default Sign Up method (First, just using username and password)
  void defaultSignUp(DefaultUserData userData);
}
