class BadServerAnswer implements Exception {
  const BadServerAnswer();
}

class EmptyOkAnswer extends BadServerAnswer {
  const EmptyOkAnswer();
}
