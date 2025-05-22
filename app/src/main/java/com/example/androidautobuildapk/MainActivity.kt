package com.example.androidautobuildapk

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.*
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
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            prefs.edit().putBoolean("simulateTap", true).apply()

            if (isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
                Toast.makeText(this, "Launching OpenVPN...", Toast.LENGTH_SHORT).show()

                // 1. Launch OpenVPN for Android using its API intent
                val intent = Intent()
                intent.component = ComponentName("de.blinkt.openvpn", "de.blinkt.openvpn.api.StartProfile")
                intent.putExtra("de.blinkt.openvpn.api.profileName", "MyVPN") // name of your VPN profile
                intent.action = Intent.ACTION_MAIN
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "OpenVPN is not installed", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 2. Wait 2 seconds, then trigger simulateTap via the AccessibilityService
                Handler(Looper.getMainLooper()).postDelayed({
                    prefs.edit().putBoolean("simulateTap", true).apply()
                }, 2000)

            } else {
                Toast.makeText(this, "Please enable the Accessibility Service", Toast.LENGTH_LONG).show()
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
