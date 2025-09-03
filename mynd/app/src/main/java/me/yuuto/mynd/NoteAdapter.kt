package me.yuuto.mynd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class NoteAdapter(
    private val notes: MutableList<Note>,
    private val onClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val subtitle: TextView = itemView.findViewById(R.id.tvNoteContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title.ifBlank { "(Ohne Titel)" }
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss")
        val date = Date(note.lastEdited)
        val time = simpleDateFormat.format(date)
        holder.subtitle.text = "Last edited: ${time}"
        holder.itemView.setOnClickListener { onClick(note) }
    }

    override fun getItemCount(): Int = notes.size

    fun removeAt(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }
}
