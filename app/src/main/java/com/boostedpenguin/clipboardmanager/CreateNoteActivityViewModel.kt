package com.boostedpenguin.clipboardmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.boostedpenguin.clipboardmanager.room.Note
import com.boostedpenguin.clipboardmanager.room.NoteRepository
import kotlinx.coroutines.launch

class CreateNoteActivityViewModel(private val repo: NoteRepository) : ViewModel() {
    fun insert(note: Note) = viewModelScope.launch {
        repo.insert(note)
    }
}

class CreateNoteActivityViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateNoteActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateNoteActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}