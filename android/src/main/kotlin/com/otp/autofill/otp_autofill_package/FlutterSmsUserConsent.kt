package com.otp.autofill.otp_autofill_package

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class FlutterSmsUserConsent(
    private val activity: Activity,
    private val context: Context,
    private val onOtpReceived: OtpCallback,
    private val smsContentRequestCode: Int,
) : BroadcastReceiver(), OtpReceiver {

    private val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    private val tag = "FlutterSmsUserConsent"

    override fun startReceiver() {
        val task = SmsRetriever.getClient(context).startSmsUserConsent(null)
        task.addOnSuccessListener {
            Log.w(tag, "Waiting for sms")
            context.registerReceiver(this, intentFilter)
        }
        task.addOnFailureListener {
            Log.w(tag, "Failed to listen to sms")
        }
    }

    override fun onReceive(onReceiveContext: Context, intent: Intent?) {
        Log.w(tag, "onReceive")
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras: Bundle? = intent.extras
            val smsRetrieverStatus: Status? = extras?.parcelable(SmsRetriever.EXTRA_STATUS)
            Log.w(tag, "SMS_RETRIEVED_ACTION")
            when (smsRetrieverStatus?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val consentIntent = extras.parcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    try {
                        if (consentIntent != null) {
                            Log.w(tag, "startActivityForResult")
                            startActivityForResult(
                                activity,
                                consentIntent,
                                smsContentRequestCode,
                                null
                            )
                        }
                    } catch (e: Exception) {
                        Log.w(tag, "Exception")
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    Log.w(tag, "consent TIMEOUT")
                }
            }
        }
    }

    override fun dispose() {
        Log.w(tag, "dispose")
        context.unregisterReceiver(this)
    }

    override fun emitOtp(otp: String?) {
        if (otp != null) {
            Log.w(tag, "Otp emitted $otp")
            onOtpReceived(otp)
        }
    }


}




