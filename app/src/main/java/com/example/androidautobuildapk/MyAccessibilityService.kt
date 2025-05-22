package com.example.androidautobuildapk

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "Accessibility Service Connected", Toast.LENGTH_SHORT).show()
        checkForTapFlag()
    }

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        checkForTapFlag()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun checkForTapFlag() {
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            if (prefs.getBoolean("simulateTap", false)) {
                simulateTap(500f, 400f)
                Toast.makeText(this, "Atingere trimisă", Toast.LENGTH_SHORT).show()
                prefs.edit().putBoolean("simulateTap", false).apply()
            }
        }, 500)
    }

    private fun simulateTap(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply { moveTo(x, y) }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()
            dispatchGesture(gesture, null, null)
        }
    }
}
