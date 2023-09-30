package com.vpngen.app.screens.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.vpngen.app.screens.details.DetailsScreen
import com.vpngen.app.ui.theme.Wifi

class MainScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { MainScreenModel() }
        val state by screenModel.state.observeAsState()

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = { TopAppBar(scrollBehavior = scrollBehavior, screenModel = screenModel) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { screenModel.toggleVpn() },
                    expanded = true,
                    icon = { Icon(imageVector = Wifi, contentDescription = null) },
                    text = { Text(text = if (state is MainScreenModel.State.Disabled) "Connect" else "Disconnect") }
                )
            },
        ) { innerPadding ->
            ConfigList(
                modifier = Modifier.padding(innerPadding),
                vpnState = state!!,
                screenModel = screenModel
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior, screenModel: MainScreenModel) {
        var expanded by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val pickConfigLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { configUri ->
            if (configUri != null) {
                screenModel.onConfigReceived(configUri, context)
            }
        }

        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    "VPN Generator",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Add a new config") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.AddCircle,
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            screenModel.addConfigClicked(launcher = pickConfigLauncher)
                            expanded = false
                        }
                    )
                }
            },
            scrollBehavior = scrollBehavior,
        )
    }

    @Composable
    private fun ConfigList(modifier: Modifier, screenModel: MainScreenModel, vpnState: MainScreenModel.State) {
        val configs by screenModel.configs.observeAsState()
        val selectedOption by screenModel.selectedConfig.observeAsState()

        Column(
            modifier = modifier
                .selectableGroup()
                .verticalScroll(rememberScrollState())
        ) {
            configs?.forEach { text ->
                ConfigListItem(
                    configName = text,
                    selectedOption = selectedOption ?: configs!![0],
                    onOptionSelected = { option -> screenModel.selectedConfigChanged(option) },
                    enabled = vpnState is MainScreenModel.State.Disabled
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ConfigListItem(configName: String, selectedOption: String, onOptionSelected: (String) -> Unit, enabled: Boolean) {
        val navigator = LocalNavigator.current

        Row(
            modifier = Modifier
                .combinedClickable(
                    onClick = { if (enabled) onOptionSelected(configName) },
                    onLongClick = { navigator?.push(DetailsScreen(configName)) }
                )
                .padding(all = 8.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = (configName == selectedOption),
                onClick = { if (enabled) onOptionSelected(configName) },
                enabled = enabled
            )

            Text(text = configName)
        }
    }
}
