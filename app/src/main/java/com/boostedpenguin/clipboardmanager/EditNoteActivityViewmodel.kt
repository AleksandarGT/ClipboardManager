package com.boostedpenguin.clipboardmanager

import androidx.lifecycle.*
import com.boostedpenguin.clipboardmanager.room.Note
import com.boostedpenguin.clipboardmanager.room.NoteRepository
import kotlinx.coroutines.launch

class EditNoteActivityViewModel(private val repo: NoteRepository) : ViewModel() {

    lateinit var currentNote: Note

    fun update(content: String) = viewModelScope.launch {
        currentNote.content = content
        repo.update(currentNote)
    }

    fun deleteCurrent() = viewModelScope.launch {
        repo.delete(currentNote)
    }

    fun updateFavorite() = viewModelScope.launch{
        currentNote.favorite = !currentNote.favorite
        repo.update(currentNote)
    }
}

class EditNoteActivityViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditNoteActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditNoteActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}