import 'package:sizzle_starter/src/core/grpc/mapper/error/error_mapper.dart';
import 'package:sizzle_starter/src/core/grpc/mapper/result_code/result_code_mapper.dart';

class StandardResponseMappers {
  final ErrorMapper errorMapper;
  final ResultCodeMapper resultCodeMapper;

  StandardResponseMappers(
      {required this.errorMapper, required this.resultCodeMapper});
}
