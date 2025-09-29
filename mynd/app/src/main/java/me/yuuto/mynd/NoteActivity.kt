package me.yuuto.mynd

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.yuuto.mynd.notes.Note
import me.yuuto.mynd.notes.NoteDatabase

private var noteId: Int = -1
private var isNewNote: Boolean = true
private var note: Note? = null

class NoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_activity)

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val lockEnabled = prefs.getBoolean("lock_notes", false)

        if (lockEnabled && noteId != -1) {
            showBiometricPrompt()
        } else {
            loadNoteContent()
        }
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    loadNoteContent()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Zugriff verweigert", Toast.LENGTH_SHORT)
                        .show()
                    finish() // NoteActivity schließen
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        "Fingerabdruck nicht erkannt",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Notiz entsperren")
            .setSubtitle("Bestätige mit Fingerabdruck")
            .setNegativeButtonText("Abbrechen")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun loadNoteContent() {
        val title = findViewById<EditText>(R.id.noteHeading)
        val content = findViewById<EditText>(R.id.noteContent)
        noteId = intent?.getIntExtra("note_id", -1) ?: -1

        if (noteId != -1) {
            isNewNote = false
            val noteDao = NoteDatabase.Companion.getDatabase(this).noteDao()
            CoroutineScope(Dispatchers.IO).launch {
                val loadedNote = noteDao.getNoteById(noteId)
                note = loadedNote
                withContext(Dispatchers.Main) {
                    title.setText(loadedNote?.title ?: "")
                    content.setText(loadedNote?.content ?: "")
                }
            }
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            Log.d("Notes", "Speichern-Button gedrückt")

            if (isNewNote) saveNote(this, title.text.toString(), content.text.toString())
            else updateNote(this, note!!,title.text.toString(), content.text.toString())

            finish()
        }
    }

    fun saveNote(context: Context, title: String, content: String) {
        val noteDao = NoteDatabase.Companion.getDatabase(context).noteDao()
        val newNote = Note(title = title, content = content)

        CoroutineScope(Dispatchers.IO).launch {
            noteDao.insert(newNote)
        }
    }

    fun updateNote(context: Context, note: Note, newTitle: String, newContent: String) {
        note.title = newTitle
        note.content = newContent
        note.lastEdited = System.currentTimeMillis()

        val noteDao = NoteDatabase.Companion.getDatabase(context).noteDao()
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.update(note)
        }
    }

}
