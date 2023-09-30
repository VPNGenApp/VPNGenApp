package com.vpngen.app.utils.config

import android.content.Context
import java.io.File
import java.io.FileWriter


class ConfigReader {

    fun writeConfig(context: Context, config: StoredConfig) {
        val dir = File(context.filesDir, "configs")

        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            val configFile = File(dir, config.name)
            val writer = FileWriter(configFile)
            writer.append(config.value)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
