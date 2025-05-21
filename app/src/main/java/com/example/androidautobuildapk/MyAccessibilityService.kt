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

    override fun onInterrupt() {
        // Not used
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Toast.makeText(this, "Accessibility Service Connected", Toast.LENGTH_SHORT).show()

        // Delay a bit to let the system settle
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            if (prefs.getBoolean("simulateTap", false)) {
                simulateTap(540f, 960f) // Tap near the center
                Toast.makeText(this, "Simulating tap!", Toast.LENGTH_SHORT).show()
                prefs.edit().putBoolean("simulateTap", false).apply()
            }
        }, 1000)
    }

    private fun simulateTap(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply { moveTo(x, y) }
            val stroke = GestureDescription.StrokeDescription(path, 0, 100)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            dispatchGesture(gesture, null, null)
        } else {
            Toast.makeText(this, "Your Android version does not support gestures", Toast.LENGTH_SHORT).show()
        }
    }
}
