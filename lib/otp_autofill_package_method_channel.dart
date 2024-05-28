import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'otp_autofill_package_platform_interface.dart';

/// An implementation of [OtpAutofillPackagePlatform] that uses method channels.
class MethodChannelOtpAutofillPackage extends OtpAutofillPackagePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('otp_autofill_package');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
