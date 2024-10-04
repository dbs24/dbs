abstract interface class ObjectMapper<F, T> {
  T map(F src);
}

abstract interface class ObjectListMapper<F, T> {
  List<T> mapList(List<F> src);
}

abstract class ComplexObjectMapper<F, T>
    implements ObjectMapper<F, T>, ObjectListMapper<F, T> {
  @override
  T map(F src);

  @override
  List<T> mapList(List<F> src) => src.map((e) => map(e)).toList();
}
