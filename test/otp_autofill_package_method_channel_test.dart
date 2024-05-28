import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:otp_autofill_package/otp_autofill_package_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelOtpAutofillPackage platform = MethodChannelOtpAutofillPackage();
  const MethodChannel channel = MethodChannel('otp_autofill_package');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
