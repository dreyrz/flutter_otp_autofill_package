
import 'otp_autofill_package_platform_interface.dart';

class OtpAutofillPackage {
  Future<String?> getPlatformVersion() {
    return OtpAutofillPackagePlatform.instance.getPlatformVersion();
  }
}
