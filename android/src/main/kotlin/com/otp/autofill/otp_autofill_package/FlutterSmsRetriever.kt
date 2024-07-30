package com.otp.autofill.otp_autofill_package

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.os.BundleCompat
import androidx.core.os.BundleCompat.getParcelable
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

typealias OtpCallback = (otp: String) -> Unit

class FlutterSmsRetriever(
    private val activity: Activity,
    private val context: Context,
    private val onOtpReceived: OtpCallback,
) : BroadcastReceiver(), OtpReceiver {

    private val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    private val tag = "FlutterSmsRetriever"

    override fun startReceiver() {
        try {
            val client = SmsRetriever.getClient(activity)
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                Log.w(tag, "Waiting for SMS")
                context.registerReceiver(this, intentFilter)
            }
            task.addOnFailureListener {
                Log.w(tag, "Waiting for SMS failed")
            }
        } catch (e: Exception) {
            Log.w(tag, "startReceiver $e ${e.stackTraceToString()}")
        }
    }

    override fun dispose() {
        Log.w(tag, "dispose")
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                val extras: Bundle? = intent.extras
                val status: Status? = extras?.parcelable(SmsRetriever.EXTRA_STATUS)
                when (status?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        val otp = retrieveOtpFromMessage(message)
                        emitOtp(otp)
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        Log.w(tag, "Waiting for SMS timed out (5 minutes)")
                    }

                    else -> {
                        Log.w(tag, "onReceive unexpected error")
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "onReceive $e ${e.stackTraceToString()}")
        }
    }

    override fun emitOtp(otp: String?) {
        if (otp != null) {
            Log.w(tag, "Otp emitted $otp")
            onOtpReceived(otp)
        }
    }
}