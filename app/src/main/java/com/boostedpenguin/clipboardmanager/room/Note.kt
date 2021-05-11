package com.boostedpenguin.clipboardmanager.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "notes_table")
data class Note(
    var content: String
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var date: Date = Calendar.getInstance().time
    var favorite: Boolean = false
}