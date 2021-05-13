package com.boostedpenguin.clipboardmanager

import androidx.lifecycle.*
import com.boostedpenguin.clipboardmanager.room.Note
import com.boostedpenguin.clipboardmanager.room.NoteRepository
import kotlinx.coroutines.launch


class EditNoteActivityViewModel(private val repo: NoteRepository) : ViewModel() {


    val currentNote: MutableLiveData<Note> by lazy {
        MutableLiveData<Note>()
    }

    fun update(content: String) = viewModelScope.launch {
        currentNote.value?.content = content
        repo.update(currentNote.value!!)
    }

    fun deleteCurrent() = viewModelScope.launch {
        repo.delete(currentNote.value!!)
    }

    fun updateFavorite() = viewModelScope.launch{
        currentNote.value?.favorite = currentNote.value?.favorite!!.not()
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