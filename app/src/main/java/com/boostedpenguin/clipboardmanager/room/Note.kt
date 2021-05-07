package com.boostedpenguin.clipboardmanager.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes_table")
data class Note(
    var content: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var date: Date = Calendar.getInstance().time
}