package com.boostedpenguin.clipboardmanager.room

import androidx.annotation.WorkerThread

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes = noteDao.getAll()

    @WorkerThread
    suspend fun getNote(id: Int) : Note {
        return noteDao.getNote(id)
    }

    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    @WorkerThread
    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    @WorkerThread
    suspend fun deleteNotes(notes: List<Int>) {
        noteDao.deleteNotes(notes)
    }

    @WorkerThread
    suspend fun deleteAll() {
        noteDao.deleteAll()
    }

    @WorkerThread
    suspend fun updateNotes(notes: List<Note>) {
        noteDao.updateNotes(notes)
    }
}