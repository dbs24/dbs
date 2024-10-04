import 'package:sizzle_starter/src/core/grpc/generated/auth-server-service.pbgrpc.dart';

/// Client service for grpc
class H2AuthServices {
  /// Public constructor for clients service
  H2AuthServices({required this.authServiceClient});

  /// Authorization service
  final AuthServerClientServiceClient authServiceClient;
}
