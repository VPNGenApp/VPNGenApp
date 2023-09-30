package com.vpngen.app.screens.main

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cafe.adriel.voyager.livedata.LiveScreenModel
import com.vpngen.app.utils.config.ConfigExtractor
import com.vpngen.app.utils.config.ConfigParser
import com.vpngen.app.utils.config.ConfigReader
import java.io.BufferedReader
import java.io.InputStreamReader

class MainScreenModel : LiveScreenModel<MainScreenModel.State>(State.Disabled) {

    sealed class State {
        object Disabled : State()
        object Enabled : State()
    }

    private val _configs: MutableLiveData<List<String>> = MutableLiveData()
    val configs: LiveData<List<String>> = _configs

    private val _selectedConfig: MutableLiveData<String?> = MutableLiveData()
    val selectedConfig: LiveData<String?> = _selectedConfig

    private val configExtractor: ConfigExtractor = ConfigExtractor()
    private val configReader: ConfigReader = ConfigReader()
    private val configParser: ConfigParser = ConfigParser()

    private fun enableVpn() {
        mutableState.value = State.Enabled
    }

    private fun disableVpn() {
        mutableState.value = State.Disabled
    }

    fun selectedConfigChanged(selectedConfig: String) {
        _selectedConfig.value = selectedConfig
    }

    fun addConfigClicked(launcher: ManagedActivityResultLauncher<String, Uri?>) {
        launcher.launch("*/*")
    }

    fun onConfigReceived(configUri: Uri, context: Context) {
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

        configReader.writeConfig(
            context = context,
            config = configString
        )

        val config = configParser.deserializeConfig(configString)

        _configs.value = _configs.value?.plus(listOf("NEW"))
    }

    fun toggleVpn() {
        selectedConfig.value?.let {
            if (state.value is State.Enabled) disableVpn()
            else enableVpn()
        }
    }

    init {
        _configs.value = listOf()

        _selectedConfig.value = _configs.value?.firstOrNull()
    }
}
