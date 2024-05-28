import 'package:flutter_test/flutter_test.dart';
import 'package:otp_autofill_package/otp_autofill_package.dart';
import 'package:otp_autofill_package/otp_autofill_package_platform_interface.dart';
import 'package:otp_autofill_package/otp_autofill_package_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockOtpAutofillPackagePlatform
    with MockPlatformInterfaceMixin
    implements OtpAutofillPackagePlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final OtpAutofillPackagePlatform initialPlatform = OtpAutofillPackagePlatform.instance;

  test('$MethodChannelOtpAutofillPackage is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelOtpAutofillPackage>());
  });

  test('getPlatformVersion', () async {
    OtpAutofillPackage otpAutofillPackagePlugin = OtpAutofillPackage();
    MockOtpAutofillPackagePlatform fakePlatform = MockOtpAutofillPackagePlatform();
    OtpAutofillPackagePlatform.instance = fakePlatform;

    expect(await otpAutofillPackagePlugin.getPlatformVersion(), '42');
  });
}
