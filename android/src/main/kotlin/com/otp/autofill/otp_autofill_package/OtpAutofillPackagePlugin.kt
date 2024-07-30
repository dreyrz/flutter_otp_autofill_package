package com.otp.autofill.otp_autofill_package

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** OtpAutofillPackagePlugin */
class OtpAutofillPackagePlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    PluginRegistry.ActivityResultListener {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity

    private var otpReceiver: OtpReceiver? = null
    private var useConsentApi: Boolean? = null

    companion object {
        private const val channelName: String = "otp_autofill_package"
        private const val tag = "OtpAutofillPackage"
        private const val smsContentRequestCode = 2
    }


    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            ("startListening") -> {
                useConsentApi = call.argument("useConsentApi")
                otpReceiver = if (useConsentApi == true) {
                    FlutterSmsUserConsent(activity, context, ::onOtpReceived, smsContentRequestCode)
                } else {
                    FlutterSmsRetriever(activity, context, ::onOtpReceived)
                }
                Log.w(tag, otpReceiver!!::class.toString())
                otpReceiver?.startReceiver()
                result.success("start listening successfully")
            }

            ("dispose") -> {
                otpReceiver?.dispose()
                otpReceiver = null
                result.success("receiver disposed")
            }

            else -> {
                result.error("Not found", "method not found", null)
            }
        }
    }


    private fun dispose() {
        otpReceiver?.dispose()
        otpReceiver = null
        channel.setMethodCallHandler(null)
    }

    private fun onOtpReceived(otp: String) {
        channel.invokeMethod("otp", otp)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        try {
            when (requestCode) {
                smsContentRequestCode -> if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.w(tag, "Consent granted")
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val otp = otpReceiver?.retrieveOtpFromMessage(message)
                    if (otp != null) {
                        otpReceiver?.emitOtp(otp)
                    }
                } else {
                    Log.w(tag, "Consent denied")
                }

            }
            return true
        } catch (e: Exception) {
            Log.w(tag, "onActivityResult exception")
            return false
        }
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        Log.d(tag, "onAttachedToEngine")
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, channelName)
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        Log.w(tag, "onDetachedFromEngine")
        dispose()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        Log.w(tag, "onDetachedFromActivityForConfigChanges")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        Log.w(tag, "onReattachedToActivityForConfigChanges")
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        Log.w(tag, "onDetachedFromActivity")
        dispose()
    }

}
