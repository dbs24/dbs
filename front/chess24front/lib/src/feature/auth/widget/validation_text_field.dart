import 'package:flutter/material.dart';

class ValidationTextField extends StatelessWidget {
  const ValidationTextField({
    required ValueNotifier<String?> error,
    required TextEditingController controller,
    this.decorationLabel,
    super.key,
  })  : _error = error,
        _controller = controller;

  final ValueNotifier<String?> _error;
  final TextEditingController _controller;
  final Widget? decorationLabel;
  @override
  Widget build(BuildContext context) => ListenableBuilder(
        listenable: _error,
        builder: (context, child) => TextField(
          decoration: InputDecoration(
            label: child,
            errorText: _error.value,
            helperText: "",
          ),
          controller: _controller,
        ),
        child: decorationLabel,
      );
}