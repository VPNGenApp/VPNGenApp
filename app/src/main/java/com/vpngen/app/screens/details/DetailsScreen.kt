package com.vpngen.app.screens.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

data class DetailsScreen(val configName: String) : Screen {

    @Composable
    override fun Content() {
        Text(text = configName)
    }
}
