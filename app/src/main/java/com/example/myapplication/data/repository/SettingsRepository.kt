package com.example.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.model.AppThemeStyle
import com.example.myapplication.data.model.NoteSort
import com.example.myapplication.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("settings")

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val themeStyle: AppThemeStyle = AppThemeStyle.PURPLE,
    val sort: NoteSort = NoteSort.DATE_NEWEST,
    val displayName: String = "Note taker",
    val email: String = "",
    val bio: String = ""
)

class SettingsRepository(private val context: Context) {
    private object Keys {
        val theme = stringPreferencesKey("theme")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
        val themeStyle = stringPreferencesKey("theme_style")
        val sort = stringPreferencesKey("sort")
        val displayName = stringPreferencesKey("display_name")
        val email = stringPreferencesKey("email")
        val bio = stringPreferencesKey("bio")
    }

    val settings: Flow<UserSettings> = context.settingsDataStore.data.map { prefs ->
        UserSettings(
            themeMode = prefs[Keys.theme]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            dynamicColor = prefs[Keys.dynamicColor] ?: false,
            themeStyle = prefs[Keys.themeStyle]
                ?.let { runCatching { AppThemeStyle.valueOf(it) }.getOrNull() }
                ?: AppThemeStyle.PURPLE,
            sort = prefs[Keys.sort]?.let { runCatching { NoteSort.valueOf(it) }.getOrNull() }
                ?: NoteSort.DATE_NEWEST,
            displayName = prefs[Keys.displayName] ?: "Note taker",
            email = prefs[Keys.email] ?: "",
            bio = prefs[Keys.bio] ?: ""
        )
    }

    suspend fun setTheme(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.theme] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.dynamicColor] = enabled }
    }

    suspend fun setThemeStyle(style: AppThemeStyle) {
        context.settingsDataStore.edit { it[Keys.themeStyle] = style.name }
    }

    suspend fun setSort(sort: NoteSort) {
        context.settingsDataStore.edit { it[Keys.sort] = sort.name }
    }

    suspend fun updateProfile(displayName: String, email: String, bio: String) {
        context.settingsDataStore.edit {
            it[Keys.displayName] = displayName.trim().ifBlank { "Note taker" }
            it[Keys.email] = email.trim()
            it[Keys.bio] = bio.trim()
        }
    }
}
