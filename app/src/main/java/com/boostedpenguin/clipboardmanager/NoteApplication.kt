package com.boostedpenguin.clipboardmanager

import android.app.Application
import com.boostedpenguin.clipboardmanager.room.NoteDatabase
import com.boostedpenguin.clipboardmanager.room.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NoteApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { NoteDatabase.getInstance(this, applicationScope) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}