import 'package:sizzle_starter/src/core/grpc/generated/players-dto.pb.dart';
import 'package:sizzle_starter/src/core/grpc/generated/protobuf-response.pb.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/grpc_response_mapper.dart';
import 'package:sizzle_starter/src/core/utils/mapper/object_mapper.dart';
import 'package:sizzle_starter/src/feature/auth/model/player_short_data.dart';

class SignUpResultMapper extends GrpcResponseMapper<CreatePlayerResponse,
    CreatedPlayerDto, PlayerShortData> {
      
  SignUpResultMapper(
      {required super.standardResponseMappers});

  @override
  CreatedPlayerDto get entityEmptyInstance => CreatedPlayerDto();

  @override
  ResponseAnswer getResponseAnswer(CreatePlayerResponse response) =>
      response.responseAnswer;

  @override
  PlayerShortData mapEntity(CreatedPlayerDto from) => PlayerShortData(
      email: from.email, status: from.status, username: from.playerLogin);
}
