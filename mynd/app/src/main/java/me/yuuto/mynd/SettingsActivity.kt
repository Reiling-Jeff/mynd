package me.yuuto.mynd

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var lockNotesSwitch: Switch
    private lateinit var lockNotesPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        lockNotesSwitch = findViewById(R.id.lockNotesSwitch)
        lockNotesPassword = findViewById(R.id.notesPassword)

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val isLocked = prefs.getBoolean("lock_notes", false)
        lockNotesSwitch.isChecked = isLocked


        if (isLocked) {
            val password = prefs.getString("notes_password", "")
            lockNotesPassword.setText(password)
        }

        val biometricManager = BiometricManager.from(this)
        val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            lockNotesSwitch.isEnabled = false
            Toast.makeText(this, "Fingerabdruck nicht verfügbar", Toast.LENGTH_SHORT).show()
        }

        lockNotesSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (lockNotesPassword.text.isBlank()) {
                    Toast.makeText(this, "Bitte zuerst ein Passwort eingeben", Toast.LENGTH_SHORT).show()
                    lockNotesSwitch.isChecked = false
                } else {
                    showBiometricPrompt(isLocking = true)
                }
            } else {
                showBiometricPrompt(isLocking = false)
            }
        }
    }

    private fun showBiometricPrompt(isLocking: Boolean) {
        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (isLocking) {
                        Toast.makeText(applicationContext, "Notizen gesperrt", Toast.LENGTH_SHORT).show()
                        saveLockState(true)
                        savePassword()
                    } else {
                        Toast.makeText(applicationContext, "Notizen entsperrt", Toast.LENGTH_SHORT).show()
                        saveLockState(false)
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // If user cancels via negative button or back press.
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON||
                        errorCode == BiometricPrompt.ERROR_CANCELED) {
                        Toast.makeText(applicationContext, "Authentifizierung abgebrochen", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // For other errors (lockout, timeout, etc.), if unlocking, show password dialog.
                    showPasswordDialogForUnlock()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Fingerabdruck nicht erkannt. Bitte erneut versuchen.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(if (isLocking) "Notizen sperren" else "Notizen entsperren")
            .setSubtitle("Bestätige mit Fingerabdruck")
            .setNegativeButtonText("Abbrechen")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showPasswordDialogForUnlock() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Passwort eingeben")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Bestätigen") { dialog, _ ->
            val enteredPassword = input.text.toString()
            val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
            val savedPassword = prefs.getString("notes_password", null)

            if (enteredPassword == savedPassword) {
                Toast.makeText(this, "Notizen entsperrt", Toast.LENGTH_SHORT).show()
                saveLockState(false)
            } else {
                Toast.makeText(this, "Falsches Passwort", Toast.LENGTH_SHORT).show()
                lockNotesSwitch.isChecked = true
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            lockNotesSwitch.isChecked = true
            dialog.cancel()
        }

        builder.show()
    }

    private fun saveLockState(locked: Boolean) {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        prefs.edit { putBoolean("lock_notes", locked) }
    }

    private fun savePassword() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        prefs.edit { putString("notes_password", lockNotesPassword.text.toString()) }
    }
}
