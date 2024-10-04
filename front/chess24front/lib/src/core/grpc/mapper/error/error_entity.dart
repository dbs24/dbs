// ignore_for_file: public_member_api_docs, sort_constructors_first

class ErrorEntity {
  final int code;
  final String description;

  const ErrorEntity({required this.code, required this.description});

  @override
  String toString() => 'ErrorEntity(code: $code, description: $description)';
}
