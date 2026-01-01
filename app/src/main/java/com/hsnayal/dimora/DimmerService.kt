//package com.hsnayal.dimora
//
//import android.app.Service
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.PixelFormat
//import android.os.IBinder
//import android.view.View
//import android.view.WindowManager
//
//class DimmerService : Service() {
//
//    companion object {
//        const val ACTION_UPDATE_OPACITY = "ACTION_UPDATE_OPACITY"
//        const val ACTION_UPDATE_MODE = "ACTION_UPDATE_MODE"
//
//        const val EXTRA_OPACITY = "EXTRA_OPACITY"
//        const val EXTRA_MODE = "EXTRA_MODE"
//
//        const val MODE_NORMAL = "MODE_NORMAL"
//        const val MODE_RED = "MODE_RED"
//    }
//
//    private lateinit var windowManager: WindowManager
//    private var overlayView: View? = null
//
//    // 🔑 SINGLE SOURCE OF TRUTH
//    private var currentOpacity = 0f       // no dim by default
//    private var currentMode = MODE_NORMAL
//
//    override fun onCreate() {
//        super.onCreate()
//        startForeground(1, NotificationUtil.create(this))
//        showOverlay()
//        updateOverlay()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        intent?.let {
//
//            // Always read opacity if present
//            if (it.hasExtra(EXTRA_OPACITY)) {
//                currentOpacity =
//                    it.getFloatExtra(EXTRA_OPACITY, currentOpacity)
//            }
//
//            when (it.action) {
//                ACTION_UPDATE_MODE -> {
//                    currentMode =
//                        it.getStringExtra(EXTRA_MODE) ?: MODE_NORMAL
//                }
//            }
//            updateOverlay()
//        }
//        return START_STICKY
//    }
//
//    private fun showOverlay() {
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            PixelFormat.TRANSLUCENT
//        )
//
//        params.layoutInDisplayCutoutMode =
//            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//
//        overlayView = View(this)
//        windowManager.addView(overlayView, params)
//    }
//
//    private fun updateOverlay() {
//        val alpha = (currentOpacity * 255).toInt().coerceIn(0, 255)
//
//        val color = when (currentMode) {
//            MODE_RED -> Color.argb(alpha, 255, 0, 0) // 🔴 TRUE OLED RED
//            else -> Color.argb(alpha, 0, 0, 0)
//        }
//
//        overlayView?.setBackgroundColor(color)
//    }
//
//    override fun onDestroy() {
//        overlayView?.let { windowManager.removeView(it) }
//        overlayView = null
//        super.onDestroy()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}

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
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {

            ACTION_UPDATE_OPACITY -> {
                currentOpacity =
                    intent.getFloatExtra(EXTRA_OPACITY, currentOpacity)
                updateOverlay()
            }

            ACTION_UPDATE_MODE -> {
                currentMode =
                    intent.getStringExtra(EXTRA_MODE) ?: MODE_NORMAL
                updateOverlay()
            }
        }

        return START_STICKY
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES

        overlayView = View(this)
        windowManager.addView(overlayView, params)

        updateOverlay()
    }

    private fun updateOverlay() {
        val alpha = (currentOpacity * 255).toInt()

        val color = when (currentMode) {
            MODE_RED -> Color.argb(alpha, 255, 0, 0) // 🔴 TRUE OLED RED
            else -> Color.argb(alpha, 0, 0, 0)      // Normal dim
        }

        overlayView?.setBackgroundColor(color)
    }

    override fun onDestroy() {
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

//package com.hsnayal.dimora
//
//import android.app.Service
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.PixelFormat
//import android.os.IBinder
//import android.view.View
//import android.view.WindowManager
//
//class DimmerService : Service() {
//
//    companion object {
//        const val ACTION_UPDATE_OPACITY = "ACTION_UPDATE_OPACITY"
//        const val EXTRA_OPACITY = "EXTRA_OPACITY"
//    }
//
//    private lateinit var windowManager: WindowManager
//    private var overlayView: View? = null
//    private var currentOpacity = 0.4f
//
//    override fun onCreate() {
//        super.onCreate()
//        startForeground(1, NotificationUtil.create(this))
//        showOverlay()
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        if (intent?.action == ACTION_UPDATE_OPACITY) {
//            val opacity = intent.getFloatExtra(EXTRA_OPACITY, currentOpacity)
//            updateOpacity(opacity)
//        }
//
//        return START_STICKY
//    }
//
//    private fun showOverlay() {
//
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//
//        val params = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
//                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            PixelFormat.TRANSLUCENT
//        )
//
//        params.layoutInDisplayCutoutMode =
//            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//
//        overlayView = View(this).apply {
//            setBackgroundColor(
//                Color.argb((currentOpacity * 255).toInt(), 0, 0, 0)
//            )
//        }
//
//        windowManager.addView(overlayView, params)
//    }
//
//
//    private fun updateOpacity(opacity: Float) {
//        currentOpacity = opacity
//        overlayView?.setBackgroundColor(
//            Color.argb((opacity * 255).toInt(), 0, 0, 0)
//        )
//    }
//
//    override fun onDestroy() {
//        overlayView?.let { windowManager.removeView(it) }
//        overlayView = null
//        super.onDestroy()
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}