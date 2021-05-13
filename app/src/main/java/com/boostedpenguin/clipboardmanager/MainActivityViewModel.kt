package com.boostedpenguin.clipboardmanager

import androidx.lifecycle.*
import com.boostedpenguin.clipboardmanager.room.Note
import com.boostedpenguin.clipboardmanager.room.NoteRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: NoteRepository) : ViewModel() {
    val allNotes: LiveData<List<Note>> = repo.allNotes.asLiveData()

    val isItemSelected = MutableLiveData<Boolean>(false);

    val selectedCheckboxes = MutableLiveData<MutableList<Int>>();

    val selectedNote = MutableLiveData<MutableList<Note>>()

    init {
        selectedCheckboxes.value = ArrayList()
        selectedNote.value = ArrayList()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun addPosition(position: Int, note: Note) {
        selectedCheckboxes.value?.add(position)
        selectedCheckboxes.notifyObserver()

        selectedNote.value?.add(note)
        selectedNote.notifyObserver()
    }

    fun removePosition(position: Int, note: Note) {
        selectedCheckboxes.value?.remove(position)
        selectedCheckboxes.notifyObserver()

        selectedNote.value!!.removeAll {
            it.id == note.id
        }
        selectedNote.notifyObserver()
    }

    fun clearPositions() {
        selectedCheckboxes.value?.clear()
        selectedCheckboxes.notifyObserver()

        selectedNote.value!!.clear()
        selectedNote.notifyObserver()
    }


    fun favoriteNotes() = viewModelScope.launch {
        selectedNote.value!!.forEach {
            it.favorite = !it.favorite
        }
        repo.updateNotes(selectedNote.value!!)
    }

    fun deleteNotes() = viewModelScope.launch {
        repo.deleteNotes(selectedNote.value!!.map { it.id }.toList())
        selectedNote.value!!.clear()
    }
}

class MainActivityViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}