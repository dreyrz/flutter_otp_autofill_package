import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'otp_autofill_package_method_channel.dart';

abstract class OtpAutofillPackagePlatform extends PlatformInterface {
  /// Constructs a OtpAutofillPackagePlatform.
  OtpAutofillPackagePlatform() : super(token: _token);

  static final Object _token = Object();

  static OtpAutofillPackagePlatform _instance = MethodChannelOtpAutofillPackage();

  /// The default instance of [OtpAutofillPackagePlatform] to use.
  ///
  /// Defaults to [MethodChannelOtpAutofillPackage].
  static OtpAutofillPackagePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [OtpAutofillPackagePlatform] when
  /// they register themselves.
  static set instance(OtpAutofillPackagePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
