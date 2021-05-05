package com.boostedpenguin.clipboardmanager.room

import androidx.annotation.WorkerThread

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes = noteDao.getAll()

    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    @WorkerThread
    suspend fun deleteNotes(notes: List<Int>) {
        noteDao.deleteNotes(notes)
    }

    @WorkerThread
    suspend fun deleteAll() {
        noteDao.deleteAll()
    }
}