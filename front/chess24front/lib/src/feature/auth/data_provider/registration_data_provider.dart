import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/server_result_entity.dart';
import 'package:sizzle_starter/src/core/grpc/generated/auth-server-service.pbgrpc.dart';
import 'package:sizzle_starter/src/core/grpc/generated/player-service.pbgrpc.dart';
import 'package:sizzle_starter/src/core/grpc/h2_chess_services.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/result_code/result_code_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/standard_response_mappers.dart';
import 'package:sizzle_starter/src/feature/auth/data_provider/mapper/sign_up_data_mapper.dart';
import 'package:sizzle_starter/src/feature/auth/data_provider/mapper/sign_up_result_mapper.dart';
import 'package:sizzle_starter/src/feature/auth/model/player_short_data.dart';
import 'package:sizzle_starter/src/feature/auth/model/results.dart';
import 'package:sizzle_starter/src/feature/auth/model/sign_up_data.dart';

abstract interface class RegistrationDataProvider {
  Future<DefaultRegistrationResult> defaultSignUp(SignUpData signUpData);
}

class RegistrationDataProviderImpl implements RegistrationDataProvider {
  final H2ChessServices _h2ChessServices;

  final StandardResponseMappers _standardResponseMappers;

  RegistrationDataProviderImpl(
      {required H2ChessServices h2ChessServices,
      required StandardResponseMappers standardResponseMappers})
      : _h2ChessServices = h2ChessServices,
        _standardResponseMappers = standardResponseMappers;

  @override
  Future<DefaultRegistrationResult> defaultSignUp(SignUpData signUpData) async {
    final request = SignUpDataMapper().map(signUpData);

    final response =
        await _h2ChessServices.playerServiceClient.createOrUpdatePlayer(request);

    final mappedResponse = SignUpResultMapper(
      standardResponseMappers: _standardResponseMappers,
    ).map(response);

    return mappedResponse;
  }
}
