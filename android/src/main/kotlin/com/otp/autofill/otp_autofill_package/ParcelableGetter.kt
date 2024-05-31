package com.otp.autofill.otp_autofill_package

import android.os.Build
import android.os.Bundle
import androidx.core.os.BundleCompat

inline fun <reified Status> Bundle.parcelable(key: String): Status? = when {
    Build.VERSION.SDK_INT >= 33 -> {
        BundleCompat.getParcelable(this, key, Status::class.java)
    }

    else -> @Suppress("DEPRECATION") getParcelable(key) as? Status
}