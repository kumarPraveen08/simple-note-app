package com.example.myapplication.data.repository

import android.content.Context
import android.net.Uri
import com.example.myapplication.data.local.NoteDatabase
import com.example.myapplication.data.local.entity.AttachmentEntity
import com.example.myapplication.data.local.entity.FolderEntity
import com.example.myapplication.data.local.entity.NoteEntity
import com.example.myapplication.data.local.entity.NoteVersionEntity
import com.example.myapplication.data.model.NoteSort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

class NotesRepository(private val context: Context) {
    private val db = NoteDatabase.get(context)
    private val notes = db.noteDao()
    private val folders = db.folderDao()
    private val versions = db.noteVersionDao()
    private val attachments = db.attachmentDao()

    fun observeFolders(): Flow<List<FolderEntity>> = folders.observeFolders()

    fun observeActiveNotes(
        query: String,
        sort: NoteSort,
        folderId: Long?,
        pinnedOnly: Boolean
    ): Flow<List<NoteEntity>> = notes.observeActiveNotes().map { list ->
        list.asSequence()
            .filter { folderId == null || it.folderId == folderId }
            .filter { !pinnedOnly || it.isPinned }
            .filter { matchesQuery(it, query) }
            .sortedWith(sortComparator(sort))
            .toList()
    }

    fun observeArchived(query: String): Flow<List<NoteEntity>> =
        notes.observeArchived().map { list -> list.filter { matchesQuery(it, query) } }

    fun observeTrash(query: String): Flow<List<NoteEntity>> =
        notes.observeTrash().map { list -> list.filter { matchesQuery(it, query) } }

    fun observeNote(id: Long): Flow<NoteEntity?> = notes.observeById(id)

    fun observeVersions(noteId: Long): Flow<List<NoteVersionEntity>> =
        versions.observeVersions(noteId)

    fun observeAttachments(noteId: Long): Flow<List<AttachmentEntity>> =
        attachments.observeForNote(noteId)

    suspend fun createFolder(name: String): Long =
        folders.insert(FolderEntity(name = name.trim()))

    suspend fun renameFolder(folder: FolderEntity, name: String) {
        folders.update(folder.copy(name = name.trim()))
    }

    suspend fun deleteFolder(folder: FolderEntity) {
        folders.delete(folder)
    }

    suspend fun createNote(folderId: Long? = null): Long {
        val now = System.currentTimeMillis()
        return notes.insert(
            NoteEntity(
                title = "",
                content = "",
                folderId = folderId,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun saveNote(
        note: NoteEntity,
        snapshotHistory: Boolean = false
    ) {
        val updated = note.copy(updatedAt = System.currentTimeMillis())
        if (snapshotHistory) {
            versions.insert(
                NoteVersionEntity(
                    noteId = updated.id,
                    title = updated.title,
                    content = updated.content,
                    checklist = updated.checklist
                )
            )
            versions.trimOldVersions(updated.id)
        }
        notes.update(updated)
    }

    suspend fun togglePin(note: NoteEntity) {
        notes.update(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
    }

    suspend fun archive(note: NoteEntity, archive: Boolean) {
        notes.update(
            note.copy(
                isArchived = archive,
                isTrashed = false,
                trashedAt = null,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun moveToTrash(note: NoteEntity) {
        notes.update(
            note.copy(
                isTrashed = true,
                isArchived = false,
                trashedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun restoreFromTrash(note: NoteEntity) {
        notes.update(
            note.copy(
                isTrashed = false,
                trashedAt = null,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun permanentlyDelete(note: NoteEntity) {
        notes.delete(note)
    }

    suspend fun emptyTrash() {
        notes.emptyTrash()
    }

    suspend fun moveToFolder(note: NoteEntity, folderId: Long?) {
        notes.update(note.copy(folderId = folderId, updatedAt = System.currentTimeMillis()))
    }

    suspend fun restoreVersion(noteId: Long, versionId: Long) {
        val version = versions.getById(versionId) ?: return
        val note = notes.getById(noteId) ?: return
        saveNote(
            note.copy(
                title = version.title,
                content = version.content,
                checklist = version.checklist
            ),
            snapshotHistory = true
        )
    }

    suspend fun enableSharing(note: NoteEntity): NoteEntity {
        val token = note.shareToken ?: UUID.randomUUID().toString().take(8)
        val updated = note.copy(
            isShared = true,
            shareToken = token,
            updatedAt = System.currentTimeMillis()
        )
        notes.update(updated)
        return updated
    }

    suspend fun disableSharing(note: NoteEntity) {
        notes.update(
            note.copy(
                isShared = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addAttachment(noteId: Long, sourceUri: Uri, fileName: String, mimeType: String): Long {
        val dir = File(context.filesDir, "attachments/$noteId").apply { mkdirs() }
        val dest = File(dir, "${System.currentTimeMillis()}_$fileName")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Unable to read attachment")
        return attachments.insert(
            AttachmentEntity(
                noteId = noteId,
                uri = dest.toURI().toString(),
                fileName = fileName,
                mimeType = mimeType
            )
        )
    }

    suspend fun removeAttachment(attachment: AttachmentEntity) {
        runCatching {
            val file = File(Uri.parse(attachment.uri).path ?: return@runCatching)
            if (file.exists()) file.delete()
        }
        attachments.delete(attachment)
    }

    private fun matchesQuery(note: NoteEntity, query: String): Boolean {
        if (query.isBlank()) return true
        val q = query.trim()
        return note.title.contains(q, ignoreCase = true) ||
            note.content.contains(q, ignoreCase = true) ||
            note.tags.contains(q, ignoreCase = true)
    }

    private fun sortComparator(sort: NoteSort): Comparator<NoteEntity> = when (sort) {
        NoteSort.DATE_NEWEST -> compareByDescending<NoteEntity> { it.isPinned }
            .thenByDescending { it.updatedAt }
        NoteSort.DATE_OLDEST -> compareByDescending<NoteEntity> { it.isPinned }
            .thenBy { it.updatedAt }
        NoteSort.TITLE_AZ -> compareByDescending<NoteEntity> { it.isPinned }
            .thenBy { it.title.lowercase() }
        NoteSort.TITLE_ZA -> compareByDescending<NoteEntity> { it.isPinned }
            .thenByDescending { it.title.lowercase() }
    }
}
