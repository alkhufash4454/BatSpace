package com.batspace.app.data.prefs

import android.content.Context
import com.batspace.app.data.model.ClonedApp
import com.batspace.app.data.model.LockType
import org.json.JSONArray
import org.json.JSONObject

class ClonePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("bat_space_clones", Context.MODE_PRIVATE)

    fun saveClone(clone: ClonedApp) {
        val all = getAllClones().toMutableList()
        val idx = all.indexOfFirst { it.id == clone.id }
        if (idx >= 0) all[idx] = clone else all.add(clone)
        val arr = JSONArray(); all.forEach { arr.put(it.toJson()) }
        prefs.edit().putString("clones", arr.toString()).apply()
    }

    fun getAllClones(): List<ClonedApp> {
        val str = prefs.getString("clones", "[]") ?: "[]"
        val arr = JSONArray(str)
        return (0 until arr.length()).mapNotNull {
            try { ClonedApp.fromJson(arr.getJSONObject(it)) } catch (e: Exception) { null }
        }
    }

    fun deleteClone(id: String) {
        val all = getAllClones().filter { it.id != id }
        val arr = JSONArray(); all.forEach { arr.put(it.toJson()) }
        prefs.edit().putString("clones", arr.toString()).apply()
    }

    fun getGlobalPin(): String = prefs.getString("global_pin", "") ?: ""
    fun setGlobalPin(hash: String) = prefs.edit().putString("global_pin", hash).apply()
    fun isGlobalPinSet(): Boolean = getGlobalPin().isNotEmpty()
}

fun ClonedApp.toJson(): JSONObject = JSONObject().apply {
    put("id", id); put("originalPackage", originalPackage)
    put("displayName", displayName); put("customLabel", customLabel)
    put("iconPath", iconPath); put("isLocked", isLocked)
    put("lockType", lockType.name); put("pinHash", pinHash)
    put("virtualAndroidId", virtualAndroidId); put("virtualDeviceId", virtualDeviceId)
    put("createdAt", createdAt); put("lastUsed", lastUsed)
    put("notificationsEnabled", notificationsEnabled)
    put("isEnabled", isEnabled); put("instanceIndex", instanceIndex)
}

fun ClonedApp.Companion.fromJson(j: JSONObject): ClonedApp = ClonedApp(
    id = j.getString("id"),
    originalPackage = j.getString("originalPackage"),
    displayName = j.getString("displayName"),
    customLabel = j.optString("customLabel", ""),
    iconPath = j.optString("iconPath", ""),
    isLocked = j.optBoolean("isLocked", false),
    lockType = try { LockType.valueOf(j.optString("lockType", "NONE")) } catch (e: Exception) { LockType.NONE },
    pinHash = j.optString("pinHash", ""),
    virtualAndroidId = j.optString("virtualAndroidId", ""),
    virtualDeviceId = j.optString("virtualDeviceId", ""),
    createdAt = j.optLong("createdAt", 0L),
    lastUsed = j.optLong("lastUsed", 0L),
    notificationsEnabled = j.optBoolean("notificationsEnabled", true),
    isEnabled = j.optBoolean("isEnabled", true),
    instanceIndex = j.optInt("instanceIndex", 1)
)
