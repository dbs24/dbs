abstract class ExecutionException implements Exception {
  Object? get cause;

  ExecutionException();
}

class WrongPointTypeException extends ExecutionException {
  final Object pointDetails;

  WrongPointTypeException({required this.pointDetails});

  @override
  // TODO: implement cause
  Object? get cause =>
      "Wrong point type: used non secured execution on secured point ($pointDetails)";
}
