package com.vpngen.app.screens.details

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.vpngen.app.utils.config.ConfigParser
import com.vpngen.app.utils.config.StoredConfig

data class DetailsScreen(val storedConfig: StoredConfig) : Screen {

    private val configParser: ConfigParser = ConfigParser()
    private val config = configParser.deserializeConfig(storedConfig.value)

    @Composable
    override fun Content() {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                item {
                    ConfigValue(name = "Description", value = config.description)
                }
                item {
                    ConfigValue(name = "Hostname", value = config.hostName)
                }
                item {
                    ConfigValue(name = "DNS 1", value = config.dns1)
                }
                item {
                    ConfigValue(name = "DNS 2", value = config.dns2)
                }
                item {
                    ConfigValue(name = "Default container", value = config.defaultContainer)
                }

                config.containers.forEach { containter ->
                    item {
                        ConfigValue(name = "Container", value = containter.container)
                        ConfigValue(name = "Cloak", value = "")
                        ConfigValue(name = "Last Config", value = containter.cloak.lastConfigString)
                        ConfigValue(name = "Port", value = containter.cloak.port)
                        ConfigValue(name = "Transport proto", value = containter.cloak.transportProto)
                        ConfigValue(name = "OpenVPN", value = "")
                        ConfigValue(name = "Last Config", value = containter.openvpn.lastConfig)
                        ConfigValue(name = "ShadowSocks", value = "")
                        ConfigValue(name = "Last Config", value = containter.shadowsocks.lastConfig)
                    }
                }
            }
        }
    }

    @Composable
    private fun ConfigValue(name: String, value: String) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(text = name)
            Text(modifier = Modifier.padding(start = 8.dp), text = value)
        }
    }
}
