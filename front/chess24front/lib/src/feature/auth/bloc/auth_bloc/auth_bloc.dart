import 'package:bloc/bloc.dart';
import 'package:freezed_annotation/freezed_annotation.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_state.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/data_provider/entity/authorized_user.dart';
import 'package:sizzle_starter/src/core/grpc/authorization/user_repository.dart';

part 'auth_bloc.freezed.dart';
part 'auth_event.dart';
part 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final UserRepository _userRepository;

  AuthBloc({required UserRepository userRepo, required AuthState initialState})
      : _userRepository = userRepo,
        super(initialState) {
    _userRepository.userStateStream.listen(_onAuthChanged);

    on<AuthEvent>(
      (event, emit) => event.map(
        logout: (event) => _logout(event, emit),
        authChanged: (event) => _authChanged(event, emit),
      ),
    );
  }

  Future<void> _logout(_Logout event, Emitter<AuthState> emitter) async {
    await _userRepository.logout();
  }

  Future<void> _authChanged(
    _AuthChanged event,
    Emitter<AuthState> emitter,
  ) async {
    emitter(AuthState.idle(event.state));
  }

  void _onAuthChanged(UserState state) {
    add(AuthEvent.authChanged(state: state));
  }
}
