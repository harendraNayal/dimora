package com.hsnayal.dimora

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Switch
import androidx.compose.ui.Alignment
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Correct edge-to-edge with BLACK system bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            MaterialTheme {
                DimoraScreen(
                    onEnable = { enableDimmer() },
                    onDisable = { disableDimmer() },
                    onSliderChange = { slider ->
                        val opacity = 1f - slider
                        updateOpacity(opacity)
                    },
                    onRedModeChange = { enabled ->
                        updateMode(
                            if (enabled)
                                DimmerService.MODE_RED
                            else
                                DimmerService.MODE_NORMAL
                        )
                    }
                )
            }
        }
    }

    private fun updateMode(mode: String) {
        val intent = Intent(this, DimmerService::class.java).apply {
            action = DimmerService.ACTION_UPDATE_MODE
            putExtra(DimmerService.EXTRA_MODE, mode)
        }
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        applyImmersiveMode()
    }

    private fun applyImmersiveMode() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)

        // ✅ TRUE fullscreen
        controller.hide(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )

        // Swipe to reveal temporarily
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // ✅ White icons on black bar
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
    }

    private fun enableDimmer() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri()
                )
            )
        } else {
            startForegroundService(Intent(this, DimmerService::class.java))
        }
    }

    private fun disableDimmer() {
        stopService(Intent(this, DimmerService::class.java))
    }

    private fun updateOpacity(opacity: Float) {
        val intent = Intent(this, DimmerService::class.java).apply {
            action = DimmerService.ACTION_UPDATE_OPACITY
            putExtra(DimmerService.EXTRA_OPACITY, opacity.coerceIn(0f, 1f))
        }
        startService(intent)
    }
}

@Composable
fun DimoraScreen(
    onEnable: () -> Unit,
    onDisable: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onRedModeChange: (Boolean) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(1f) }
    var redMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Dimora",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = if (redMode)
                "🔴 Red OLED Night Mode"
            else
                "🌙 Normal Dim Mode"
        )

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onSliderChange(it)
            },
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)   // ✅ minimum touch target
                .padding(vertical = 12.dp) // ✅ increases touch area
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Red OLED Mode")
            Switch(
                checked = redMode,
                onCheckedChange = {
                    redMode = it
                    onRedModeChange(it)
                }
            )
        }

        Button(
            onClick = onEnable,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable Dimmer")
        }

        OutlinedButton(
            onClick = onDisable,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Disable Dimmer")
        }
    }
}


