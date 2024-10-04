import 'package:grpc/grpc.dart';
import 'package:pure/pure.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/user_storage_data_source.dart';

class AuthorizedInterceptor extends ClientInterceptor {
  final UserStorageDataProvider userStorageDataSource;

  AuthorizedInterceptor({required this.userStorageDataSource});

  @override
  ResponseFuture<R> interceptUnary<Q, R>(
    ClientMethod<Q, R> method,
    Q request,
    CallOptions options,
    ClientUnaryInvoker<Q, R> invoker,
  ) {
    final optionsWithToken = options.mergedWith(
      CallOptions(
        providers: [
          _jwtProvider,
        ],
      ),
    );

    final futureResponse =
        super.interceptUnary(method, request, optionsWithToken, invoker);

    return futureResponse;
  }

  Future<void> _jwtProvider(Map<String, String> metadata, String uri) async {
    final user = await userStorageDataSource.getUser();

    final jwt = user?.accessToken;

    metadata['Authorization'] = "Bearer $jwt";
  }
}
