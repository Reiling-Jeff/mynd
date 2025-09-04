package me.yuuto.mynd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.widget.Switch
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

    private lateinit var lockNotesSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        lockNotesSwitch = findViewById(R.id.lockNotesSwitch)

        // Prüfen ob Biometrie verfügbar ist
        val biometricManager = BiometricManager.from(this)
        val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            lockNotesSwitch.isEnabled = false
            Toast.makeText(this, "Fingerabdruck nicht verfügbar", Toast.LENGTH_SHORT).show()
        }

        lockNotesSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showBiometricPrompt()
            } else {
                // Sperre deaktivieren
                Toast.makeText(this, "Notizen entsperrt", Toast.LENGTH_SHORT).show()
                saveLockState(false)
            }
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Notizen gesperrt", Toast.LENGTH_SHORT).show()
                    saveLockState(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Fehler: $errString", Toast.LENGTH_SHORT).show()
                    lockNotesSwitch.isChecked = false
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Fingerabdruck nicht erkannt", Toast.LENGTH_SHORT).show()
                    lockNotesSwitch.isChecked = false
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Notizen sperren")
            .setSubtitle("Bestätige mit Fingerabdruck")
            .setNegativeButtonText("Abbrechen")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun saveLockState(locked: Boolean) {
        // hier z. B. in SharedPreferences speichern
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        prefs.edit().putBoolean("lock_notes", locked).apply()
    }
}
