package com.example.myapplication.ui.settings

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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
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
import com.example.myapplication.data.model.NoteSort
import com.example.myapplication.ui.components.SettingsNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onOpenLookAndFeel: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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
                    "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
                SettingsNavItem(
                    icon = Icons.Default.Person,
                    title = "Profile",
                    subtitle = settings.displayName.ifBlank { "Display name, email, bio" },
                    onClick = onOpenProfile
                )
                SettingsNavItem(
                    icon = Icons.Default.Palette,
                    title = "Look & feel",
                    subtitle = "Dark theme, dynamic color, palettes",
                    onClick = onOpenLookAndFeel
                )
                SettingsNavItem(
                    icon = Icons.AutoMirrored.Filled.Sort,
                    title = "Default sort",
                    subtitle = when (settings.sort) {
                        NoteSort.DATE_NEWEST -> "Date · newest"
                        NoteSort.DATE_OLDEST -> "Date · oldest"
                        NoteSort.TITLE_AZ -> "Title · A–Z"
                        NoteSort.TITLE_ZA -> "Title · Z–A"
                    },
                    onClick = {
                        val next = when (settings.sort) {
                            NoteSort.DATE_NEWEST -> NoteSort.DATE_OLDEST
                            NoteSort.DATE_OLDEST -> NoteSort.TITLE_AZ
                            NoteSort.TITLE_AZ -> NoteSort.TITLE_ZA
                            NoteSort.TITLE_ZA -> NoteSort.DATE_NEWEST
                        }
                        viewModel.setSort(next)
                    }
                )
                SettingsNavItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "Offline notes stored on this device",
                    onClick = {}
                )
            }
        }
    }
}
