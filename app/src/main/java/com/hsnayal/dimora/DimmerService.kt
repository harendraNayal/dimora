package com.hsnayal.dimora

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.View
import android.view.WindowManager

class DimmerService : Service() {

    companion object {
        const val ACTION_UPDATE_OPACITY = "ACTION_UPDATE_OPACITY"
        const val ACTION_UPDATE_MODE = "ACTION_UPDATE_MODE"
        const val ACTION_STOP_DIMMER = "ACTION_STOP_DIMMER"

        const val EXTRA_OPACITY = "EXTRA_OPACITY"
        const val EXTRA_MODE = "EXTRA_MODE"

        const val MODE_NORMAL = "MODE_NORMAL"
        const val MODE_RED = "MODE_RED"
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    private var currentOpacity = 0f // no dim by default
    private var currentMode = MODE_NORMAL

    override fun onCreate() {
        super.onCreate()
        startForeground(1, NotificationUtil.create(this))
        showOverlay()
        updateOverlay()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            if (it.action == ACTION_STOP_DIMMER) {
                stopSelf() // 🔥 Disable dimmer
                return START_NOT_STICKY
            }

            if (it.hasExtra(EXTRA_OPACITY)) {
                currentOpacity = it.getFloatExtra(EXTRA_OPACITY, currentOpacity)
            }

            if (it.action == ACTION_UPDATE_MODE) {
                currentMode = it.getStringExtra(EXTRA_MODE) ?: MODE_NORMAL
            }

            updateOverlay()
        }

        return START_STICKY
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        overlayView = View(this)
        windowManager.addView(overlayView, params)

        updateOverlay()
    }

    private fun updateOverlay() {
        // Convert opacity (0f–1f) → alpha (0–255)
        val alpha = (currentOpacity * 255)
            .toInt()
            .coerceIn(0, 255)

        // Decide overlay color based on mode
        val color = when (currentMode) {
            MODE_RED -> Color.argb(alpha, 255, 0, 0) // 🔴 True OLED red
            else -> Color.argb(alpha, 0, 0, 0)      // 🌑 Normal dim (black)
        }

        // Apply to existing overlay view
        overlayView?.setBackgroundColor(color)
    }

//    private fun updateOverlay() {
//        val alpha = (currentOpacity * 255).toInt()
//
//        val color = when (currentMode) {
//            MODE_RED -> Color.argb(alpha, 255, 0, 0) // 🔴 TRUE OLED RED
//            else -> Color.argb(alpha, 0, 0, 0)      // Normal dim
//        }
//
//        overlayView?.setBackgroundColor(color)
//    }

    override fun onDestroy() {
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}