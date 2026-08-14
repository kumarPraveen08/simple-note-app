package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.NotesApplication
import com.example.myapplication.data.local.entity.FolderEntity
import com.example.myapplication.data.local.entity.NoteEntity
import com.example.myapplication.data.model.NoteFilter
import com.example.myapplication.data.model.NoteSort
import com.example.myapplication.data.repository.NotesRepository
import com.example.myapplication.data.repository.SettingsRepository
import com.example.myapplication.data.repository.UserSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val notes: List<NoteEntity> = emptyList(),
    val folders: List<FolderEntity> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val query: String = "",
    val filter: NoteFilter = NoteFilter.ALL,
    val selectedFolderId: Long? = null,
    val sort: NoteSort = NoteSort.DATE_NEWEST
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow(NoteFilter.ALL)
    private val selectedFolderId = MutableStateFlow<Long?>(null)

    private val notesOnly = combine(
        query,
        filter,
        selectedFolderId,
        settingsRepository.settings
    ) { q, f, folderId, settings ->
        Triple(q, f, folderId to settings.sort)
    }.flatMapLatest { (q, f, folderAndSort) ->
        val (folderId, sort) = folderAndSort
        when (f) {
            NoteFilter.ARCHIVE -> notesRepository.observeArchived(q)
            NoteFilter.TRASH -> notesRepository.observeTrash(q)
            NoteFilter.PINNED -> notesRepository.observeActiveNotes(q, sort, null, true)
            NoteFilter.FOLDER -> notesRepository.observeActiveNotes(q, sort, folderId, false)
            NoteFilter.ALL -> notesRepository.observeActiveNotes(q, sort, null, false)
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        notesOnly,
        notesRepository.observeFolders(),
        settingsRepository.settings,
        combine(query, filter, selectedFolderId) { q, f, folderId -> Triple(q, f, folderId) }
    ) { notes, folders, settings, filters ->
        HomeUiState(
            notes = notes,
            folders = folders,
            settings = settings,
            query = filters.first,
            filter = filters.second,
            selectedFolderId = filters.third,
            sort = settings.sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun setQuery(value: String) {
        query.value = value
    }

    fun setFilter(value: NoteFilter, folderId: Long? = selectedFolderId.value) {
        filter.value = value
        selectedFolderId.value = folderId
    }

    fun setSort(sort: NoteSort) {
        viewModelScope.launch { settingsRepository.setSort(sort) }
    }

    fun createNote(onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val folderId = if (filter.value == NoteFilter.FOLDER) selectedFolderId.value else null
            onCreated(notesRepository.createNote(folderId))
        }
    }

    fun togglePin(note: NoteEntity) {
        viewModelScope.launch { notesRepository.togglePin(note) }
    }

    fun archive(note: NoteEntity, archive: Boolean = true) {
        viewModelScope.launch { notesRepository.archive(note, archive) }
    }

    fun trash(note: NoteEntity) {
        viewModelScope.launch { notesRepository.moveToTrash(note) }
    }

    fun restore(note: NoteEntity) {
        viewModelScope.launch { notesRepository.restoreFromTrash(note) }
    }

    fun deleteForever(note: NoteEntity) {
        viewModelScope.launch { notesRepository.permanentlyDelete(note) }
    }

    fun emptyTrash() {
        viewModelScope.launch { notesRepository.emptyTrash() }
    }

    fun moveToFolder(note: NoteEntity, folderId: Long?) {
        viewModelScope.launch { notesRepository.moveToFolder(note, folderId) }
    }

    fun createFolder(name: String) {
        viewModelScope.launch { notesRepository.createFolder(name) }
    }

    fun renameFolder(folder: FolderEntity, name: String) {
        viewModelScope.launch { notesRepository.renameFolder(folder, name) }
    }

    fun deleteFolder(folder: FolderEntity) {
        viewModelScope.launch {
            notesRepository.deleteFolder(folder)
            if (selectedFolderId.value == folder.id) {
                selectedFolderId.value = null
                if (filter.value == NoteFilter.FOLDER) filter.value = NoteFilter.ALL
            }
        }
    }

    companion object {
        fun factory(app: NotesApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return HomeViewModel(app.notesRepository, app.settingsRepository) as T
            }
        }
    }
}
