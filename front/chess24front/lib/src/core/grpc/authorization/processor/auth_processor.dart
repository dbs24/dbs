import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_entity.dart';

/// Result of processing authorization
final class AuthProcessorResult {
  /// Public constructor
  AuthProcessorResult({required this.errors, this.authorizedUserEntity});

  /// User Entity
  AuthorizedUserEntity? authorizedUserEntity;

  List<ErrorEntity> errors;

  /// For 2FA purposes
}

/// Interface of authorization algorithm that should return AuthorizedUserEntity
abstract interface class AuthProcessor {
  /// The algoritm of authorization
  Future<AuthProcessorResult> authorize();
}
