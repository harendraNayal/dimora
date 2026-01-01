package com.hsnayal.dimora

import DimoraScreen
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermissionIfNeeded()

        // ✅ Correct edge-to-edge with BLACK system bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        setContent {
            MaterialTheme {

                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current

                var showPermissionDialog by remember {
                    mutableStateOf(!Settings.canDrawOverlays(context))
                }

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            showPermissionDialog = !Settings.canDrawOverlays(context)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                if (showPermissionDialog) {
                    OverlayPermissionDialog(
                        onContinue = {
                            startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    "package:$packageName".toUri()
                                )
                            )
                        }
                    )
                }

                DimoraScreen(
                    onEnable = { enableDimmer() },
                    onDisable = { disableDimmer() },
                    onSliderChange = { slider ->
                        val opacity = 1f - slider
                        updateOpacity(opacity)
                    },
                    onRedModeChange = { enabled ->
                        updateMode(
                            if (enabled) DimmerService.MODE_RED
                            else DimmerService.MODE_NORMAL
                        )
                    },
                )
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
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


