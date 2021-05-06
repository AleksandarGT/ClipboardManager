package com.boostedpenguin.clipboardmanager

import androidx.lifecycle.*
import com.boostedpenguin.clipboardmanager.room.Note
import com.boostedpenguin.clipboardmanager.room.NoteRepository
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: NoteRepository) : ViewModel() {
    val allNotes: LiveData<List<Note>> = repo.allNotes.asLiveData()

    val isItemSelected = MutableLiveData<Boolean>(false);

    val selectedCheckboxes = MutableLiveData<MutableList<Int>>();


    init {
        selectedCheckboxes.value = ArrayList()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun addPosition(position: Int) {
        selectedCheckboxes.value?.add(position)
        selectedCheckboxes.notifyObserver()
    }

    fun removePosition(position: Int) {
        selectedCheckboxes.value?.remove(position)
        selectedCheckboxes.notifyObserver()
    }

    fun clearPositions() {
        selectedCheckboxes.value?.clear()
        selectedCheckboxes.notifyObserver()
    }



    val selectedNote = mutableListOf<Note>()

    fun insert(note: Note) = viewModelScope.launch {
        repo.insert(note)
    }

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
    }

    fun deleteNotes() = viewModelScope.launch {
        repo.deleteNotes(selectedNote.map { it.id }.toList())
        selectedNote.clear()
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