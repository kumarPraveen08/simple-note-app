package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.dao.AttachmentDao
import com.example.myapplication.data.local.dao.FolderDao
import com.example.myapplication.data.local.dao.NoteDao
import com.example.myapplication.data.local.dao.NoteVersionDao
import com.example.myapplication.data.local.entity.AttachmentEntity
import com.example.myapplication.data.local.entity.FolderEntity
import com.example.myapplication.data.local.entity.NoteEntity
import com.example.myapplication.data.local.entity.NoteVersionEntity

@Database(
    entities = [
        FolderEntity::class,
        NoteEntity::class,
        NoteVersionEntity::class,
        AttachmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun noteDao(): NoteDao
    abstract fun noteVersionDao(): NoteVersionDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        @Volatile
        private var instance: NoteDatabase? = null

        fun get(context: Context): NoteDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "notes.db"
                ).build().also { instance = it }
            }
        }
    }
}
