package com.example.androidautobuildapk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testTapButton = findViewById<Button>(R.id.testTap)

        testTapButton.setOnClickListener {
            // Set a flag so the accessibility service will simulate the tap
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.edit().putBoolean("simulateTap", true).apply()

            if (isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "Tap will be simulated", Toast.LENGTH_SHORT).show()
            } else {
                // Ask the user to enable the accessibility service
                Toast.makeText(this, "Enable Accessibility Service", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        }
    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out android.accessibilityservice.AccessibilityService>
    ): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        for (enabledService in colonSplitter) {
            val componentName = ComponentName.unflattenFromString(enabledService)
            if (componentName != null && componentName == expectedComponentName) {
                return true
            }
        }
        return false
    }
}
