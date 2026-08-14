package com.example.myapplication.ui.editor

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myapplication.data.model.NoteColors
import com.example.myapplication.ui.components.ColorSwatch
import com.example.myapplication.ui.components.SectionLabel
import com.example.myapplication.ui.util.RichText
import com.example.myapplication.ui.util.toComposeColorOrNull
import java.io.File
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    viewModel: NoteEditorViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    var showColors by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showTags by remember { mutableStateOf(false) }
    var tagsDraft by remember { mutableStateOf("") }
    var contentValue by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(state.ready) {
        if (state.ready) {
            contentValue = TextFieldValue(state.content, TextRange(state.content.length))
        }
    }

    LaunchedEffect(state.content) {
        if (contentValue.text != state.content) {
            contentValue = contentValue.copy(text = state.content)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.saveNow(snapshot = true) }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = uri.lastPathSegment?.substringAfterLast('/') ?: "attachment"
        val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
        viewModel.addAttachment(uri, name, mime)
    }

    val bg = state.color.toComposeColorOrNull() ?: MaterialTheme.colorScheme.surface
    val note = state.note

    Scaffold(
        containerColor = bg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (state.saving) "Saving…" else "Auto-saved",
                            style = MaterialTheme.typography.labelLarge
                        )
                        state.lastSavedAt?.let {
                            Text(
                                DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(it)),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveNow(snapshot = true)
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::togglePin) {
                        Icon(
                            if (note?.isPinned == true) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin"
                        )
                    }
                    IconButton(onClick = { showColors = true }) {
                        Icon(Icons.Default.Palette, contentDescription = "Color")
                    }
                    IconButton(onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, state.title)
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${state.title}\n\n${RichText.plainPreview(state.content, 8000)}"
                            )
                        }
                        context.startActivity(Intent.createChooser(share, "Share note"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    BoxMenu(menuOpen, { menuOpen = it }) {
                        DropdownMenuItem(
                            text = { Text("Tags") },
                            onClick = {
                                tagsDraft = state.tags.joinToString(", ")
                                showTags = true
                                menuOpen = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("History") },
                            onClick = {
                                showHistory = true
                                menuOpen = false
                            },
                            leadingIcon = { Icon(Icons.Default.History, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (note?.isShared == true) "Disable sharing" else "Enable sharing") },
                            onClick = {
                                viewModel.toggleShare(note?.isShared != true)
                                menuOpen = false
                            }
                        )
                        state.folders.forEach { folder ->
                            DropdownMenuItem(
                                text = { Text("Move to ${folder.name}") },
                                onClick = {
                                    viewModel.moveToFolder(folder.id)
                                    menuOpen = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Remove from folder") },
                            onClick = {
                                viewModel.moveToFolder(null)
                                menuOpen = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (note?.isArchived == true) "Unarchive" else "Archive") },
                            onClick = {
                                viewModel.archive(note?.isArchived != true)
                                menuOpen = false
                                onBack()
                            },
                            leadingIcon = { Icon(Icons.Default.Archive, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Move to trash") },
                            onClick = {
                                viewModel.trash()
                                menuOpen = false
                                onBack()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null) }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            BasicTextField(
                value = state.title,
                onValueChange = viewModel::updateTitle,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { inner ->
                    if (state.title.isEmpty()) {
                        Text(
                            "Title",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )
            Spacer(Modifier.height(8.dp))
            FormatToolbar(
                onBold = {
                    viewModel.applyFormat(
                        contentValue.selection.min,
                        contentValue.selection.max,
                        "**"
                    )
                },
                onItalic = {
                    viewModel.applyFormat(
                        contentValue.selection.min,
                        contentValue.selection.max,
                        "*"
                    )
                },
                onUnderline = {
                    viewModel.applyFormat(
                        contentValue.selection.min,
                        contentValue.selection.max,
                        "__"
                    )
                },
                onAttach = { picker.launch(arrayOf("*/*")) },
                onChecklist = { viewModel.addChecklistItem() }
            )
            Spacer(Modifier.height(12.dp))
            BasicTextField(
                value = contentValue.copy(text = state.content),
                onValueChange = {
                    contentValue = it
                    viewModel.updateContent(it.text)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                decorationBox = { inner ->
                    if (state.content.isEmpty()) {
                        Text(
                            "Start writing… Use **bold**, *italic*, __underline__.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )
            if (state.content.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                SectionLabel("Preview")
                Text(
                    text = RichText.toAnnotated(state.content),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Checklist")
            state.checklist.forEach { item ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                    ) {
                        Checkbox(
                            checked = item.checked,
                            onCheckedChange = {
                                viewModel.updateChecklistItem(item.id, checked = it)
                            }
                        )
                        OutlinedTextField(
                            value = item.text,
                            onValueChange = { viewModel.updateChecklistItem(item.id, text = it) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("To-do") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            )
                        )
                        IconButton(onClick = { viewModel.removeChecklistItem(item.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove item")
                        }
                    }
                }
            }
            TextButton(onClick = { viewModel.addChecklistItem() }) {
                Icon(Icons.Default.CheckBox, contentDescription = null)
                Text("  Add to-do")
            }
            if (state.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                SectionLabel("Tags")
                Text(state.tags.joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
            }
            if (note?.isShared == true) {
                Spacer(Modifier.height(8.dp))
                SectionLabel("Sharing")
                Text(
                    "Share code: ${note.shareToken}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (state.attachments.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                SectionLabel("Attachments")
                state.attachments.forEach { attachment ->
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(attachment.fileName, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    val file = File(Uri.parse(attachment.uri).path ?: return@IconButton)
                                    val shareUri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = attachment.mimeType
                                        putExtra(Intent.EXTRA_STREAM, shareUri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share file"))
                                }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share file")
                                }
                                IconButton(onClick = { viewModel.removeAttachment(attachment) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                            if (attachment.mimeType.startsWith("image/")) {
                                AsyncImage(
                                    model = Uri.parse(attachment.uri),
                                    contentDescription = attachment.fileName,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(48.dp))
        }
        }
    }

    if (showColors) {
        AlertDialog(
            onDismissRequest = { showColors = false },
            title = { Text("Note color") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    NoteColors.options.forEach { color ->
                        ColorSwatch(
                            color = color,
                            selected = state.color == color,
                            onClick = {
                                viewModel.setColor(color)
                                showColors = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColors = false }) { Text("Done") }
            }
        )
    }

    if (showTags) {
        AlertDialog(
            onDismissRequest = { showTags = false },
            title = { Text("Tags") },
            text = {
                OutlinedTextField(
                    value = tagsDraft,
                    onValueChange = { tagsDraft = it },
                    label = { Text("Comma-separated tags") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setTags(tagsDraft)
                    showTags = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showTags = false }) { Text("Cancel") }
            }
        )
    }

    if (showHistory) {
        AlertDialog(
            onDismissRequest = { showHistory = false },
            title = { Text("Version history") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    if (state.versions.isEmpty()) {
                        Text("No snapshots yet. Snapshots are created when you leave the editor.")
                    } else {
                        state.versions.forEach { version ->
                            TextButton(
                                onClick = {
                                    viewModel.restoreVersion(version.id)
                                    showHistory = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                        .format(Date(version.savedAt)) +
                                        " · ${version.title.ifBlank { "Untitled" }}"
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHistory = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun FormatToolbar(
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onUnderline: () -> Unit,
    onAttach: () -> Unit,
    onChecklist: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        FilledTonalIconButton(onClick = onBold) {
            Icon(Icons.Default.FormatBold, contentDescription = "Bold")
        }
        FilledTonalIconButton(onClick = onItalic) {
            Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
        }
        FilledTonalIconButton(onClick = onUnderline) {
            Icon(Icons.Default.FormatUnderlined, contentDescription = "Underline")
        }
        FilledTonalIconButton(onClick = onChecklist) {
            Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "Checklist")
        }
        FilledTonalIconButton(onClick = onAttach) {
            Icon(Icons.Default.AttachFile, contentDescription = "Attach")
        }
    }
}

@Composable
private fun BoxMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    IconButton(onClick = { onExpandedChange(true) }) {
        Icon(Icons.Default.MoreVert, contentDescription = "More")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }, content = content)
}
