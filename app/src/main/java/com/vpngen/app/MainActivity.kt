package com.vpngen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.vpngen.app.screens.main.MainScreen
import com.vpngen.app.ui.theme.VPNGenAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VPNGenAppTheme {
                Navigator(MainScreen())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VPNGenAppTheme {

    }
}
