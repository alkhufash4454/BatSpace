package com.batspace.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.batspace.app.data.model.ClonedApp
import com.batspace.app.data.model.InstalledApp
import com.batspace.app.data.prefs.ClonePreferences
import java.security.MessageDigest
import java.util.UUID

class CloneRepository(private val context: Context) {

    private val prefs = ClonePreferences(context)

    fun getInstalledApps(): List<InstalledApp> {
        val pm = context.packageManager
        val flags = PackageManager.GET_META_DATA
        return pm.getInstalledApplications(flags)
            .filter { it.packageName != context.packageName }
            .sortedBy { pm.getApplicationLabel(it).toString().lowercase() }
            .map { info ->
                InstalledApp(
                    packageName = info.packageName,
                    appName = pm.getApplicationLabel(info).toString(),
                    icon = pm.getApplicationIcon(info),
                    versionName = try { pm.getPackageInfo(info.packageName, 0).versionName ?: "" } catch (e: Exception) { "" },
                    isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    cloneCount = prefs.getAllClones().count { it.originalPackage == info.packageName }
                )
            }
    }

    fun getUserApps(): List<InstalledApp> = getInstalledApps().filter { !it.isSystemApp }

    fun getAllClones(): List<ClonedApp> = prefs.getAllClones()

    fun createClone(packageName: String, appName: String, customLabel: String = ""): ClonedApp {
        val existingCount = prefs.getAllClones().count { it.originalPackage == packageName }
        val clone = ClonedApp(
            id = UUID.randomUUID().toString(),
            originalPackage = packageName,
            displayName = appName,
            customLabel = customLabel.ifBlank { "$appName ${existingCount + 1}" },
            virtualAndroidId = generateVirtualId(),
            virtualDeviceId = generateVirtualId(),
            instanceIndex = existingCount + 1
        )
        prefs.saveClone(clone)
        return clone
    }

    fun updateClone(clone: ClonedApp) = prefs.saveClone(clone)

    fun deleteClone(id: String) = prefs.deleteClone(id)

    fun getCloneById(id: String) = prefs.getAllClones().find { it.id == id }

    fun refreshVirtualIdentity(id: String): ClonedApp? {
        val clone = getCloneById(id) ?: return null
        val updated = clone.copy(
            virtualAndroidId = generateVirtualId(),
            virtualDeviceId = generateVirtualId()
        )
        updateClone(updated)
        return updated
    }

    private fun generateVirtualId(): String {
        val chars = ('A'..'F') + ('0'..'9')
        return (1..16).map { chars.random() }.joinToString("")
    }

    fun hashPin(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
