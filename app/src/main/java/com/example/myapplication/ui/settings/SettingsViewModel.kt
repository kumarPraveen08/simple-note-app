package com.example.myapplication.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myapplication.NotesApplication
import com.example.myapplication.data.model.AppThemeStyle
import com.example.myapplication.data.model.NoteSort
import com.example.myapplication.data.model.ThemeMode
import com.example.myapplication.data.repository.SettingsRepository
import com.example.myapplication.data.repository.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val settings: StateFlow<UserSettings> = settingsRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UserSettings()
    )

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setTheme(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColor(enabled) }
    }

    fun setThemeStyle(style: AppThemeStyle) {
        viewModelScope.launch { settingsRepository.setThemeStyle(style) }
    }

    fun setSort(sort: NoteSort) {
        viewModelScope.launch { settingsRepository.setSort(sort) }
    }

    fun updateProfile(displayName: String, email: String, bio: String) {
        viewModelScope.launch { settingsRepository.updateProfile(displayName, email, bio) }
    }

    companion object {
        fun factory(app: NotesApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return SettingsViewModel(app.settingsRepository) as T
            }
        }
    }
}
