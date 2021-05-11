package com.boostedpenguin.clipboardmanager.room

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note);

    @Delete
    suspend fun delete(note: Note);

    @Query("DELETE FROM notes_table")
    suspend fun deleteAll();

    @Query("DELETE FROM notes_table WHERE id IN (:notes)")
    suspend fun deleteNotes(notes: List<Int>)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM notes_table")
    fun getAll(): Flow<List<Note>>

    @Update
    suspend fun updateNotes(notes: List<Note>)

    @Query("SELECT * FROM notes_table WHERE id=:id ")
    suspend fun getNote(id: Int): Note
}