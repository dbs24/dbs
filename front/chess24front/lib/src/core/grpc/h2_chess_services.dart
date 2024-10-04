import 'package:sizzle_starter/src/core/grpc/generated/player-service.pbgrpc.dart';

abstract interface class H2ChessRepository{
  
}



/// Chess services
class H2ChessServices {
  
  final PlayerServiceClient playerServiceClient;

  /// Consructor
  H2ChessServices({required this.playerServiceClient});

}


