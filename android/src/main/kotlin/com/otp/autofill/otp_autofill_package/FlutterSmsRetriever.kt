package com.otp.autofill.otp_autofill_package

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
    private val tag = FlutterSmsRetriever::class.qualifiedName
    override fun dispose() {
        Log.w(tag, "dispose")
        context.unregisterReceiver(this)
    }

    override fun startReceiver() {
        val client = SmsRetriever.getClient(activity)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.w(tag, "Waiting for SMS")
            context.registerReceiver(this, intentFilter)
        }
        task.addOnFailureListener {
            Log.w(tag, "Waiting for SMS failed")
        }
    }

    private fun emitOtp(otp: String?) {
        if (otp != null) {
            Log.w(tag, "Otp emitted $otp")
            onOtpReceived(otp)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
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
    }
}