package com.example.myapplication.ui.editor

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.NotesApplication
import com.example.myapplication.data.local.entity.AttachmentEntity
import com.example.myapplication.data.local.entity.FolderEntity
import com.example.myapplication.data.local.entity.NoteEntity
import com.example.myapplication.data.local.entity.NoteVersionEntity
import com.example.myapplication.data.model.ChecklistItem
import com.example.myapplication.data.repository.NotesRepository
import com.example.myapplication.data.util.ChecklistCodec
import com.example.myapplication.data.util.TagCodec
import com.example.myapplication.ui.util.RichText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val note: NoteEntity? = null,
    val folders: List<FolderEntity> = emptyList(),
    val versions: List<NoteVersionEntity> = emptyList(),
    val attachments: List<AttachmentEntity> = emptyList(),
    val checklist: List<ChecklistItem> = emptyList(),
    val tags: List<String> = emptyList(),
    val title: String = "",
    val content: String = "",
    val color: Long = 0L,
    val saving: Boolean = false,
    val lastSavedAt: Long? = null,
    val ready: Boolean = false
)

class NoteEditorViewModel(
    private val noteId: Long,
    private val notesRepository: NotesRepository
) : ViewModel() {
    private val title = MutableStateFlow("")
    private val content = MutableStateFlow("")
    private val checklist = MutableStateFlow<List<ChecklistItem>>(emptyList())
    private val tags = MutableStateFlow<List<String>>(emptyList())
    private val color = MutableStateFlow(0L)
    private val saving = MutableStateFlow(false)
    private val lastSavedAt = MutableStateFlow<Long?>(null)
    private val ready = MutableStateFlow(false)
    private var autoSaveJob: Job? = null

    private val drafts = combine(title, content, checklist, tags, color) { t, c, cl, tg, col ->
        Draft(t, c, cl, tg, col)
    }

    private val meta = combine(
        notesRepository.observeNote(noteId).filterNotNull(),
        notesRepository.observeFolders(),
        notesRepository.observeVersions(noteId),
        notesRepository.observeAttachments(noteId),
        combine(saving, lastSavedAt, ready) { s, ls, r -> Triple(s, ls, r) }
    ) { note, folders, versions, attachments, saveMeta ->
        Meta(note, folders, versions, attachments, saveMeta.first, saveMeta.second, saveMeta.third)
    }

    val uiState: StateFlow<EditorUiState> = combine(meta, drafts) { m, d ->
        EditorUiState(
            note = m.note,
            folders = m.folders,
            versions = m.versions,
            attachments = m.attachments,
            checklist = d.checklist,
            tags = d.tags,
            title = d.title,
            content = d.content,
            color = d.color,
            saving = m.saving,
            lastSavedAt = m.lastSavedAt,
            ready = m.ready
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EditorUiState())

    init {
        viewModelScope.launch {
            val note = notesRepository.observeNote(noteId).filterNotNull().first()
            title.value = note.title
            content.value = note.content
            checklist.value = ChecklistCodec.decode(note.checklist)
            tags.value = TagCodec.decode(note.tags)
            color.value = note.color
            ready.value = true
        }
    }

    fun updateTitle(value: String) {
        title.value = value
        scheduleAutoSave()
    }

    fun updateContent(value: String) {
        content.value = value
        scheduleAutoSave()
    }

    fun applyFormat(start: Int, end: Int, marker: String) {
        content.update { RichText.wrapSelection(it, start, end, marker) }
        scheduleAutoSave()
    }

    fun setColor(value: Long) {
        color.value = value
        scheduleAutoSave()
    }

    fun setTags(raw: String) {
        tags.value = TagCodec.decode(raw)
        scheduleAutoSave()
    }

    fun addChecklistItem(text: String = "") {
        checklist.update { it + ChecklistItem(id = UUID.randomUUID().toString(), text = text) }
        scheduleAutoSave()
    }

    fun updateChecklistItem(id: String, text: String? = null, checked: Boolean? = null) {
        checklist.update { list ->
            list.map {
                if (it.id != id) it
                else it.copy(text = text ?: it.text, checked = checked ?: it.checked)
            }
        }
        scheduleAutoSave()
    }

    fun removeChecklistItem(id: String) {
        checklist.update { list -> list.filterNot { it.id == id } }
        scheduleAutoSave()
    }

    fun moveToFolder(folderId: Long?) {
        viewModelScope.launch {
            flushSave()
            val note = notesRepository.observeNote(noteId).first() ?: return@launch
            notesRepository.moveToFolder(note, folderId)
        }
    }

    fun togglePin() {
        viewModelScope.launch {
            val note = notesRepository.observeNote(noteId).first() ?: return@launch
            notesRepository.togglePin(note)
        }
    }

    fun archive(archive: Boolean) {
        viewModelScope.launch {
            flushSave(snapshot = true)
            val note = notesRepository.observeNote(noteId).first() ?: return@launch
            notesRepository.archive(note, archive)
        }
    }

    fun trash() {
        viewModelScope.launch {
            flushSave(snapshot = true)
            val note = notesRepository.observeNote(noteId).first() ?: return@launch
            notesRepository.moveToTrash(note)
        }
    }

    fun toggleShare(enable: Boolean) {
        viewModelScope.launch {
            flushSave()
            val note = notesRepository.observeNote(noteId).first() ?: return@launch
            if (enable) notesRepository.enableSharing(note) else notesRepository.disableSharing(note)
        }
    }

    fun restoreVersion(versionId: Long) {
        viewModelScope.launch {
            flushSave(snapshot = true)
            notesRepository.restoreVersion(noteId, versionId)
            val note = notesRepository.observeNote(noteId).filterNotNull().first()
            title.value = note.title
            content.value = note.content
            checklist.value = ChecklistCodec.decode(note.checklist)
            tags.value = TagCodec.decode(note.tags)
            color.value = note.color
        }
    }

    fun addAttachment(uri: Uri, fileName: String, mimeType: String) {
        viewModelScope.launch {
            notesRepository.addAttachment(noteId, uri, fileName, mimeType)
        }
    }

    fun removeAttachment(attachment: AttachmentEntity) {
        viewModelScope.launch { notesRepository.removeAttachment(attachment) }
    }

    fun saveNow(snapshot: Boolean = true) {
        viewModelScope.launch { flushSave(snapshot) }
    }

    private fun scheduleAutoSave() {
        if (!ready.value) return
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(650)
            flushSave(snapshot = false)
        }
    }

    private suspend fun flushSave(snapshot: Boolean = false) {
        if (!ready.value) return
        val base = notesRepository.observeNote(noteId).first() ?: return
        saving.value = true
        notesRepository.saveNote(
            base.copy(
                title = title.value,
                content = content.value,
                checklist = ChecklistCodec.encode(checklist.value),
                tags = TagCodec.encode(tags.value),
                color = color.value
            ),
            snapshotHistory = snapshot
        )
        lastSavedAt.value = System.currentTimeMillis()
        saving.value = false
    }

    companion object {
        fun factory(app: NotesApplication, noteId: Long) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return NoteEditorViewModel(noteId, app.notesRepository) as T
            }
        }
    }
}

private data class Draft(
    val title: String,
    val content: String,
    val checklist: List<ChecklistItem>,
    val tags: List<String>,
    val color: Long
)

private data class Meta(
    val note: NoteEntity,
    val folders: List<FolderEntity>,
    val versions: List<NoteVersionEntity>,
    val attachments: List<AttachmentEntity>,
    val saving: Boolean,
    val lastSavedAt: Long?,
    val ready: Boolean
)
