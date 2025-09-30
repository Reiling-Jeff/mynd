package me.yuuto.mynd

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.yuuto.mynd.notes.Note
import me.yuuto.mynd.notes.NoteAdapter
import me.yuuto.mynd.notes.NoteDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var rvNotesList: RecyclerView
    private lateinit var fab: FloatingActionButton
    private val notes = mutableListOf<Note>()
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var intent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        rvNotesList = findViewById(R.id.rvNotesList)
        fab = findViewById(R.id.fab)

        noteAdapter = NoteAdapter(notes) { note ->
            intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("note_id", note.id)
            startActivity(intent)
        }

        rvNotesList.layoutManager = LinearLayoutManager(this)
        rvNotesList.adapter = noteAdapter

        fab.setOnClickListener {
            intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        fab.setOnLongClickListener {
            intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        enableSwipeToDelete()

        loadNotes()
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadNotes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = NoteDatabase.getDatabase(applicationContext).noteDao()
                val allNotes = dao.getAllNotes()
                withContext(Dispatchers.Main) {
                    notes.clear()
                    notes.addAll(allNotes)
                    noteAdapter.notifyDataSetChanged()
                }
            } catch (_: Throwable) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "DB nicht verfügbar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enableSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val noteToDelete = notes[position]

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        NoteDatabase.getDatabase(applicationContext).noteDao().delete(noteToDelete)
                    } catch (_: Exception) {}
                }

                notes.removeAt(position)
                noteAdapter.notifyItemRemoved(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val density = itemView.context.resources.displayMetrics.density
                val cornerRadius = 16f * density // 16dp → px

                if (dX != 0f) {
                    val progress = (kotlin.math.abs(dX) / itemView.width).coerceIn(0f, 1f)

                    val alpha = (50 + progress * (255 - 50)).toInt()

                    val paint = android.graphics.Paint().apply {
                        color = Color.argb(alpha, 255, 0, 0)
                    }

                    val rect = android.graphics.RectF(
                        if (dX > 0) itemView.left.toFloat() else itemView.right + dX,
                        itemView.top.toFloat(),
                        if (dX > 0) itemView.left + dX else itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    c.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvNotesList)
    }
}
