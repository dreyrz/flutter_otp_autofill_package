package com.otp.autofill.otp_autofill_package

interface OtpReceiver {
    fun dispose()
    fun startReceiver()
    fun emitOtp(otp: String?)
    fun retrieveOtpFromMessage(message: String?): String? {
        if (message == null) {
            return null
        }
        val regex = Regex("(\\d{6})")
        val match = regex.find(message)
        return match?.value
    }
}