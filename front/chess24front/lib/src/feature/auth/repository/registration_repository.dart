import 'package:sizzle_starter/src/feature/auth/data_provider/registration_data_provider.dart';
import 'package:sizzle_starter/src/feature/auth/model/results.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';

abstract interface class RegistrationRepository {
  Future<DefaultRegistrationResult> defaultSignUp(SignUpData signUpData);
}

class RegistrationRepositoryImpl implements RegistrationRepository {
  final RegistrationDataProvider _registrationDataProvider;

  RegistrationRepositoryImpl(
      {required RegistrationDataProvider registrationDataProvider})
      : _registrationDataProvider = registrationDataProvider;
  @override
  Future<DefaultRegistrationResult> defaultSignUp(SignUpData signUpData) {
    return _registrationDataProvider.defaultSignUp(signUpData);
  }
}
