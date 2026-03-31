package com.batspace.app.data.model

data class ClonedApp(
    val id: String,
    val originalPackage: String,
    val displayName: String,
    val customLabel: String = "",
    val iconPath: String = "",
    val isLocked: Boolean = false,
    val lockType: LockType = LockType.NONE,
    val pinHash: String = "",
    val virtualAndroidId: String = "",
    val virtualDeviceId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = 0L,
    val notificationsEnabled: Boolean = true,
    val isEnabled: Boolean = true,
    val instanceIndex: Int = 1
)

enum class LockType { NONE, PIN, FINGERPRINT }
