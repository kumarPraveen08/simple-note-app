package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.NotesApplication
import com.example.myapplication.ui.editor.NoteEditorScreen
import com.example.myapplication.ui.editor.NoteEditorViewModel
import com.example.myapplication.ui.folders.FoldersScreen
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.navigation.Routes
import com.example.myapplication.ui.settings.LookAndFeelScreen
import com.example.myapplication.ui.settings.ProfileScreen
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.settings.SettingsViewModel
import com.example.myapplication.ui.theme.NotesExpressiveTheme

@Composable
fun NotesApp() {
    val app = LocalContext.current.applicationContext as NotesApplication
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(app))
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

    NotesExpressiveTheme(
        themeMode = settings.themeMode,
        dynamicColor = settings.dynamicColor,
        themeStyle = settings.themeStyle
    ) {
        val navController = rememberNavController()
        val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(app))

        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onOpenNote = { id -> navController.navigate(Routes.editor(id)) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenFolders = { navController.navigate(Routes.FOLDERS) }
                )
            }
            composable(Routes.FOLDERS) {
                FoldersScreen(
                    viewModel = homeViewModel,
                    onBack = { navController.popBackStack() },
                    onOpenFolder = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.EDITOR,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { entry ->
                val noteId = entry.arguments?.getLong("noteId") ?: return@composable
                val editorViewModel: NoteEditorViewModel = viewModel(
                    factory = NoteEditorViewModel.factory(app, noteId)
                )
                NoteEditorScreen(
                    viewModel = editorViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() },
                    onOpenLookAndFeel = { navController.navigate(Routes.LOOK_AND_FEEL) },
                    onOpenProfile = { navController.navigate(Routes.PROFILE) }
                )
            }
            composable(Routes.LOOK_AND_FEEL) {
                LookAndFeelScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
