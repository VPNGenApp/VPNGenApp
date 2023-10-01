package com.vpngen.app.screens.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cafe.adriel.voyager.livedata.LiveScreenModel
import com.vpngen.app.utils.config.ConfigExtractor
import com.vpngen.app.utils.config.ConfigParser
import com.vpngen.app.utils.config.ConfigReader
import com.vpngen.app.utils.config.ConfigStorage
import com.vpngen.app.utils.config.StoredConfig
import de.blinkt.openvpn.activities.ConfigConverter
import java.io.BufferedReader
import java.io.InputStreamReader

class MainScreenModel(private val context: Context) : LiveScreenModel<MainScreenModel.State>(State.Disabled) {

    sealed class State {
        object Disabled : State()
        object Enabled : State()
    }

    private val _configs: MutableLiveData<List<StoredConfig>> = MutableLiveData()
    val configs: LiveData<List<StoredConfig>> = _configs

    private val _selectedConfig: MutableLiveData<String?> = MutableLiveData()
    val selectedConfig: LiveData<String?> = _selectedConfig

    private val configExtractor: ConfigExtractor = ConfigExtractor()
    private val configReader: ConfigReader = ConfigReader()
    private val configParser: ConfigParser = ConfigParser()
    private val configStorage: ConfigStorage = ConfigStorage(context)

    private fun enableVpn() {
        mutableState.value = State.Enabled

        context.startActivity(
            Intent(context, Class.forName("de.blinkt.openvpn.LaunchVPN")).apply {
                action = Intent.ACTION_MAIN
                //putExtra("de.blinkt.openvpn.shortcutProfileName", selectedConfig.value!!)
                putExtra("de.blinkt.openvpn.shortcutProfileUUID", "180ddbe4-83a5-41cf-827b-342288744aa5")
            }
        )
    }

    private fun disableVpn() {
        mutableState.value = State.Disabled

        context.startActivity(
            Intent(context, Class.forName("de.blinkt.openvpn.activities.DisconnectVPN"))
        )
    }

    fun selectedConfigChanged(selectedConfig: String) {
        _selectedConfig.value = selectedConfig
    }

    fun addConfigClicked(launcher: ManagedActivityResultLauncher<String, Uri?>) {
        launcher.launch("*/*")
    }

    fun onConfigReceived(configUri: Uri) {
        val startImport = Intent(context, ConfigConverter::class.java)
        startImport.action = ConfigConverter.IMPORT_PROFILE
        startImport.data = configUri
        context.startActivity(startImport)

        val contentResolver = context.contentResolver

        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(configUri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }

        val configString = configExtractor.extractConnectionFromCode(stringBuilder.toString())
        val config = configParser.deserializeConfig(configString)

        val storedConfig = StoredConfig(name = config.description, value = configString)

        if (_configs.value?.firstOrNull { it.name == storedConfig.name } != null) {
            Toast.makeText(context, "Config already exists", Toast.LENGTH_LONG).show()
            return
        }

        configReader.writeConfig(
            context = context,
            config = storedConfig
        )

        saveConfig(storedConfig)
    }

    private fun saveConfig(config: StoredConfig) {
        _configs.value = _configs.value?.plus(listOf(config))

        configStorage.saveConfigs(_configs.value!!)
    }

    fun toggleVpn() {
        selectedConfig.value?.let {
            if (state.value is State.Enabled) disableVpn()
            else enableVpn()
        }
    }

    init {
        _configs.value = configStorage.loadConfigs()

        _selectedConfig.value = _configs.value?.firstOrNull()?.name
    }
}
