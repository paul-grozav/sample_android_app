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
            prefs.edit().putBoolean("simulateTap", true).apply()

            if (isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "Launching OpenVPN...", Toast.LENGTH_SHORT).show()

                val intent = Intent()
                intent.component = ComponentName(
                    "de.blinkt.openvpn",
                    "de.blinkt.openvpn.api.StartProfile"
                )
                intent.putExtra("de.blinkt.openvpn.api.profileName", "MyVPN")
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)

                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    // Display exception message in the TextView
                    outputTextView.text = "Failed to launch OpenVPN:\n${e.message}"
                    return@setOnClickListener
                }

                // Wait 2 seconds, then trigger tap via AccessibilityService
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
