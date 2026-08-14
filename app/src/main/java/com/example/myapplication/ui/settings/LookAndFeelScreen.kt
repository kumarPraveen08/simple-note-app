package com.example.myapplication.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.model.ThemeMode
import com.example.myapplication.ui.components.SettingsSwitchItem
import com.example.myapplication.ui.components.ThemePalettePicker
import com.example.myapplication.ui.components.ThemePreviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val systemDark = isSystemInDarkTheme()
    val darkEnabled = when (settings.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }
    val darkSubtitle = when (settings.themeMode) {
        ThemeMode.DARK -> "On"
        ThemeMode.LIGHT -> "Off"
        ThemeMode.SYSTEM -> if (systemDark) "System · On" else "System · Off"
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Look & feel",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                Spacer(Modifier.height(12.dp))
                ThemePreviewCard()
                Spacer(Modifier.height(20.dp))
                ThemePalettePicker(
                    selected = settings.themeStyle,
                    enabled = !settings.dynamicColor,
                    onSelect = viewModel::setThemeStyle
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    if (settings.dynamicColor) {
                        "Palette picker is disabled while dynamic color is on."
                    } else {
                        "Choose a color style for the app theme."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                SettingsSwitchItem(
                    icon = Icons.Default.Colorize,
                    title = "Dynamic color",
                    subtitle = "Apply colors from wallpaper to the app theme.",
                    checked = settings.dynamicColor,
                    onCheckedChange = viewModel::setDynamicColor
                )
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark theme",
                    subtitle = darkSubtitle,
                    checked = darkEnabled,
                    onCheckedChange = { enabled ->
                        viewModel.setTheme(if (enabled) ThemeMode.DARK else ThemeMode.LIGHT)
                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.BrightnessAuto,
                    title = "Follow system",
                    subtitle = if (settings.themeMode == ThemeMode.SYSTEM) "On" else "Off",
                    checked = settings.themeMode == ThemeMode.SYSTEM,
                    onCheckedChange = { follow ->
                        viewModel.setTheme(
                            if (follow) ThemeMode.SYSTEM
                            else if (systemDark) ThemeMode.DARK else ThemeMode.LIGHT
                        )
                    }
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
