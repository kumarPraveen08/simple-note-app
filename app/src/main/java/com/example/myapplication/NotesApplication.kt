package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.repository.NotesRepository
import com.example.myapplication.data.repository.SettingsRepository

class NotesApplication : Application() {
    lateinit var notesRepository: NotesRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        notesRepository = NotesRepository(this)
        settingsRepository = SettingsRepository(this)
    }
}
