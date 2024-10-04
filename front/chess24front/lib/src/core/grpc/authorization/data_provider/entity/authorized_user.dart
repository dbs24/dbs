/// Authorized User with jwt and access tokens
class AuthorizedUserEntity {
  /// Use refreshToken to refresh accessToken
  final String refreshToken;

  /// Use accessToken to get access to services
  final String accessToken;

  /// Public constructor
  AuthorizedUserEntity({required this.refreshToken, required this.accessToken});

  @override
  String toString() => "AuthorizedUserEntity($accessToken, $refreshToken)";
}
