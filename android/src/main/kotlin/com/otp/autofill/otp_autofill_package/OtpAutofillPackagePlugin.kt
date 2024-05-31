package com.otp.autofill.otp_autofill_package

import android.app.Activity
import android.content.Context
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** OtpAutofillPackagePlugin */
class OtpAutofillPackagePlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
    private var otpReceiver: OtpReceiver? = null
    private val channelName: String = "otp_autofill_package"
    private val tag = OtpAutofillPackagePlugin::class.qualifiedName


    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            ("startListening") -> {
                if (otpReceiver == null && activity != null && context != null) {
                    otpReceiver = FlutterSmsRetriever(activity!!, context!!, ::onOtpReceived)
                }
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

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, channelName)
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        dispose()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        dispose()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        dispose()
    }
}
