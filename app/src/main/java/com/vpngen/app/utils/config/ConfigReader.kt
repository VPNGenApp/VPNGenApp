package com.vpngen.app.utils.config

import android.content.Context
import java.io.File
import java.io.FileWriter


class ConfigReader {

    fun writeConfig(context: Context, config: String) {
        val dir = File(context.filesDir, "configs")

        if (!dir.exists()) {
            dir.mkdir()
        }

        try {
            val configFile = File(dir, "config1")
            val writer = FileWriter(configFile)
            writer.append(config)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
