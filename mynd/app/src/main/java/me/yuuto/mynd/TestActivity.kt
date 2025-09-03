package me.yuuto.mynd

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var noteId: Int = -1
private var isNewNote: Boolean = true
private var note: Note? = null

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)

        val title = findViewById<EditText>(R.id.noteHeading)
        val content = findViewById<EditText>(R.id.noteContent)
        noteId = intent?.getIntExtra("note_id", -1) ?: -1

        if (noteId != -1) {
            isNewNote = false;
            val noteDao = NoteDatabase.getDatabase(this).noteDao()
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
            // Hier kommt der Code, der beim Klick ausgeführt werden soll
            Log.d("Notes", "Speichern-Button gedrückt")

            if (isNewNote) saveNote(this, title.text.toString(), content.text.toString())
            else updateNote(this, note!!,title.text.toString(), content.text.toString())

            finish()
        }
    }

    fun saveNote(context: Context, title: String, content: String) {
        val noteDao = NoteDatabase.getDatabase(context).noteDao()
        val newNote = Note(title = title, content = content)

        CoroutineScope(Dispatchers.IO).launch {
            noteDao.insert(newNote)
        }
    }

    fun updateNote(context: Context, note: Note, newTitle: String, newContent: String) {
        note.title = newTitle
        note.content = newContent
        note.lastEdited = System.currentTimeMillis()

        val noteDao = NoteDatabase.getDatabase(context).noteDao()
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.update(note)
        }
    }

}
