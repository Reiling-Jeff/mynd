package me.yuuto.mynd.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var content: String,
    val createdAt: Long = System.currentTimeMillis(),
    var lastEdited: Long = System.currentTimeMillis()
)
