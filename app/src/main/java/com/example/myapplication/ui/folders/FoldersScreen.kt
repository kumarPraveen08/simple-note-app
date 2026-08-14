package com.example.myapplication.ui.folders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.local.entity.FolderEntity
import com.example.myapplication.data.model.NoteFilter
import com.example.myapplication.ui.HomeViewModel
import com.example.myapplication.ui.components.ActionSheetContent
import com.example.myapplication.ui.components.EmptyState
import com.example.myapplication.ui.components.FolderCard
import com.example.myapplication.ui.components.SheetListAction
import com.example.myapplication.ui.components.SheetQuickAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit,
    onOpenFolder: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showNewFolder by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }
    var actionFolder by remember { mutableStateOf<FolderEntity?>(null) }
    var renameFolder by remember { mutableStateOf<FolderEntity?>(null) }
    var deleteFolder by remember { mutableStateOf<FolderEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folders", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        folderName = ""
                        showNewFolder = true
                    }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "New folder")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.folders.isEmpty()) {
                EmptyState(face = "(・_・;)", message = "No folders yet")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(140.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.folders, key = { it.id }) { folder ->
                        FolderCard(
                            folder = folder,
                            noteCount = 0,
                            onClick = {
                                viewModel.setFilter(NoteFilter.FOLDER, folder.id)
                                onOpenFolder(folder.id)
                            },
                            onMoreClick = { actionFolder = folder }
                        )
                    }
                }
            }
        }
    }

    actionFolder?.let { folder ->
        ModalBottomSheet(
            onDismissRequest = { actionFolder = null },
            shape = MaterialTheme.shapes.extraLarge
        ) {
            ActionSheetContent(
                title = folder.name,
                subtitle = "Folder",
                leading = {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                quickActions = listOf(
                    SheetQuickAction(Icons.Default.Edit, "Rename") {
                        folderName = folder.name
                        actionFolder = null
                        renameFolder = folder
                    },
                    SheetQuickAction(Icons.Default.Delete, "Delete") {
                        actionFolder = null
                        deleteFolder = folder
                    }
                ),
                listActions = listOf(
                    SheetListAction(
                        Icons.Default.Edit,
                        "Rename folder",
                        "Change the folder name",
                        onClick = {
                            folderName = folder.name
                            actionFolder = null
                            renameFolder = folder
                        }
                    ),
                    SheetListAction(
                        Icons.Default.Delete,
                        "Delete folder",
                        "Notes stay in your list",
                        onClick = {
                            actionFolder = null
                            deleteFolder = folder
                        }
                    )
                )
            )
        }
    }

    if (showNewFolder) {
        FolderNameDialog(
            title = "New folder",
            confirmLabel = "Create",
            value = folderName,
            onValueChange = { folderName = it },
            onDismiss = { showNewFolder = false },
            onConfirm = {
                viewModel.createFolder(folderName)
                folderName = ""
                showNewFolder = false
            }
        )
    }

    renameFolder?.let { folder ->
        FolderNameDialog(
            title = "Rename folder",
            confirmLabel = "Save",
            value = folderName,
            onValueChange = { folderName = it },
            onDismiss = { renameFolder = null },
            onConfirm = {
                viewModel.renameFolder(folder, folderName)
                folderName = ""
                renameFolder = null
            }
        )
    }

    deleteFolder?.let { folder ->
        AlertDialog(
            onDismissRequest = { deleteFolder = null },
            title = { Text("Delete folder?") },
            text = {
                Text("“${folder.name}” will be removed. Notes inside it stay in your list.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFolder(folder)
                        deleteFolder = null
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleteFolder = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun FolderNameDialog(
    title: String,
    confirmLabel: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("Folder name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = value.isNotBlank()
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
