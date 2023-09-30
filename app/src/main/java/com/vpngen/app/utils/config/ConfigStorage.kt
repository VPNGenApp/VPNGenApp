package com.vpngen.app.utils.config

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ConfigStorage(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("configs", Context.MODE_PRIVATE)

    fun loadConfigs(): List<StoredConfig> {
        val json: String? = sharedPreferences.getString(CONFIGS_PREF_NAME, null)

        json?.let {
            return Json.decodeFromString(json)
        }

        return emptyList()
    }

    fun saveConfigs(configs: List<StoredConfig>) {
        val json = Json.encodeToString(configs)

        sharedPreferences.edit().run {
            putString(CONFIGS_PREF_NAME, json)
            apply()
        }
    }

    private companion object {
        const val CONFIGS_PREF_NAME = "configs"
    }
}

@Serializable
data class StoredConfig(
    val name: String,
    val value: String
)
