package com.vpngen.app.utils.config

import kotlinx.serialization.json.Json

class ConfigParser {

    private val json = Json { ignoreUnknownKeys = true }

    fun deserializeConfig(config: String): Config {
        val result = json.decodeFromString<Config>(config)
        result.containers.forEach {
            it.cloak.lastConfig = json.decodeFromString(it.cloak.lastConfigString)
        }

        return result
    }
}
