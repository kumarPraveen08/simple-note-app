package com.example.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.local.entity.AttachmentEntity
import com.example.myapplication.data.local.entity.FolderEntity
import com.example.myapplication.data.local.entity.NoteEntity
import com.example.myapplication.data.local.entity.NoteVersionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders ORDER BY name ASC")
    fun observeFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: FolderEntity): Long

    @Update
    suspend fun update(folder: FolderEntity)

    @Delete
    suspend fun delete(folder: FolderEntity)

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getById(id: Long): FolderEntity?
}

@Dao
interface NoteDao {
    @Query(
        """
        SELECT * FROM notes
        WHERE isTrashed = 0 AND isArchived = 0
        ORDER BY isPinned DESC, updatedAt DESC
        """
    )
    fun observeActiveNotes(): Flow<List<NoteEntity>>

    @Query(
        """
        SELECT * FROM notes WHERE isArchived = 1 AND isTrashed = 0
        ORDER BY updatedAt DESC
        """
    )
    fun observeArchived(): Flow<List<NoteEntity>>

    @Query(
        """
        SELECT * FROM notes WHERE isTrashed = 1
        ORDER BY trashedAt DESC
        """
    )
    fun observeTrash(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observeById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes WHERE isTrashed = 1")
    suspend fun emptyTrash()
}

@Dao
interface NoteVersionDao {
    @Query("SELECT * FROM note_versions WHERE noteId = :noteId ORDER BY savedAt DESC")
    fun observeVersions(noteId: Long): Flow<List<NoteVersionEntity>>

    @Insert
    suspend fun insert(version: NoteVersionEntity): Long

    @Query("SELECT * FROM note_versions WHERE id = :id")
    suspend fun getById(id: Long): NoteVersionEntity?

    @Query("DELETE FROM note_versions WHERE noteId = :noteId AND id NOT IN (SELECT id FROM note_versions WHERE noteId = :noteId ORDER BY savedAt DESC LIMIT 20)")
    suspend fun trimOldVersions(noteId: Long)
}

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE noteId = :noteId ORDER BY addedAt DESC")
    fun observeForNote(noteId: Long): Flow<List<AttachmentEntity>>

    @Insert
    suspend fun insert(attachment: AttachmentEntity): Long

    @Delete
    suspend fun delete(attachment: AttachmentEntity)

    @Query("SELECT * FROM attachments WHERE id = :id")
    suspend fun getById(id: Long): AttachmentEntity?
}
