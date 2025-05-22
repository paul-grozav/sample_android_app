package com.example.androidautobuildapk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testTapButton = findViewById<Button>(R.id.testTap)
        val outputTextView = findViewById<TextView>(R.id.outputText)

        testTapButton.setOnClickListener {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)

            if (isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "Starting OpenVPN profile...", Toast.LENGTH_SHORT).show()

                // Prepare the intent to start OpenVPN for Android profile
                val intent = Intent("de.blinkt.openvpn.api.START_PROFILE")
                intent.setClassName("de.blinkt.openvpn", "de.blinkt.openvpn.api.ExternalAPI")
                intent.putExtra("de.blinkt.openvpn.api.profileName", "MyVPN")

                try {
                    startService(intent)
                } catch (e: Exception) {
                    outputTextView.text = "Failed to start OpenVPN service:\n${e.message}"
                    return@setOnClickListener
                }

                // Optional: simulate tap after a short delay
                Handler(Looper.getMainLooper()).postDelayed({
                    prefs.edit().putBoolean("simulateTap", true).apply()
                }, 2000)

            } else {
                outputTextView.text = "Accessibility Service is not enabled."
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
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
