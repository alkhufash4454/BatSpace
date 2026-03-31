package com.batspace.app.data.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val versionName: String = "",
    val isSystemApp: Boolean = false,
    val cloneCount: Int = 0
)
