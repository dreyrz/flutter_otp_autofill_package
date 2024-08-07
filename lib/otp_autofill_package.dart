import 'dart:async';

import 'package:flutter/services.dart';

typedef OtpCallback = void Function(String otp);

class OtpAutofillPackage {
  static final OtpAutofillPackage _singleton = OtpAutofillPackage._();
  factory OtpAutofillPackage() => _singleton;

  OtpAutofillPackage._() {
    _channel.setMethodCallHandler(_emitOtpState);
  }

  static const _channel = MethodChannel('otp_autofill_package');

  StreamController<String> _otpStream = StreamController.broadcast();

  OtpCallback? _otpCallback;

  bool _isListening = false;

  Future<void> _emitOtpState(MethodCall methodCalled) async {
    if (methodCalled.method == "otp") {
      _otpCallback?.call(methodCalled.arguments);
      dispose();
    }
  }

  Future<void> startListening({
    OtpCallback? onOtpReceived,
    bool useConsentApi = false,
  }) async {
    if (_isListening) return;
    if (_otpStream.isClosed) {
      _otpStream = StreamController.broadcast();
    }
    await _channel.invokeMethod(
      'startListening',
      {"useConsentApi": useConsentApi},
    );
    _isListening = true;
    _otpCallback = onOtpReceived;
  }

  Future<void> dispose() async {
    await _channel.invokeMethod('dispose');
    await _otpStream.close();
    _isListening = false;
  }
}
