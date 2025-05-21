package com.example.androidautobuildapk

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.SharedPreferences
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    private lateinit var prefs: SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Check for trigger and simulate tap
        if (prefs.getBoolean("simulateTap", false)) {
            simulateTap(500f, 800f) // example coordinates
            prefs.edit().putBoolean("simulateTap", false).apply()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    private fun simulateTap(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply {
                moveTo(x, y)
            }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
                .build()

            dispatchGesture(gesture, null, null)
        }
    }
}
