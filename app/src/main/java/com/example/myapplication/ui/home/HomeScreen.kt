package com.example.myapplication.ui.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.automirrored.filled.DriveFileMove
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberContainedSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.local.entity.NoteEntity
import com.example.myapplication.data.model.NoteFilter
import com.example.myapplication.data.model.NoteSort
import com.example.myapplication.ui.HomeViewModel
import com.example.myapplication.ui.components.ActionSheetContent
import com.example.myapplication.ui.components.EmptyState
import com.example.myapplication.ui.components.FolderCard
import com.example.myapplication.ui.components.NoteCard
import com.example.myapplication.ui.components.SheetListAction
import com.example.myapplication.ui.components.SheetQuickAction
import com.example.myapplication.ui.util.RichText
import com.example.myapplication.ui.util.toComposeColorOrNull
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private data class FilterOption(
    val label: String,
    val filter: NoteFilter,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenNote: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFolders: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showSortMenu by remember { mutableStateOf(false) }
    var actionNote by remember { mutableStateOf<NoteEntity?>(null) }
    var moveNote by remember { mutableStateOf<NoteEntity?>(null) }
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)
    val listState = rememberLazyListState()
    val fabFocusRequester = remember { FocusRequester() }

    val textFieldState = rememberTextFieldState()
    val searchBarState = rememberContainedSearchBarState()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val appBarWithSearchColors = SearchBarDefaults.appBarWithSearchColors(
        searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
    )

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collectLatest(viewModel::setQuery)
    }

    val fabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset < 48
        }
    }

    LaunchedEffect(fabVisible) {
        if (!fabVisible) fabMenuExpanded = false
    }

    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

    val title = when (state.filter) {
        NoteFilter.ALL -> "Notes"
        NoteFilter.PINNED -> "Pinned"
        NoteFilter.ARCHIVE -> "Archive"
        NoteFilter.TRASH -> "Trash"
        NoteFilter.FOLDER -> state.folders.firstOrNull { it.id == state.selectedFolderId }?.name
            ?: "Folder"
    }

    val showHomeFilters =
        state.filter == NoteFilter.ALL || state.filter == NoteFilter.PINNED

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
            onSearch = { scope.launch { searchBarState.animateToCollapsed() } },
            placeholder = { Text("Search notes, tags…") },
            leadingIcon = {
                if (searchBarState.currentValue == SearchBarValue.Expanded) {
                    IconButton(onClick = { scope.launch { searchBarState.animateToCollapsed() } }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = {
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(onClick = { textFieldState.clearText() }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                colors = appBarWithSearchColors,
                inputField = inputField,
                navigationIcon = {
                    if (state.filter != NoteFilter.ALL && state.filter != NoteFilter.PINNED) {
                        IconButton(onClick = { viewModel.setFilter(NoteFilter.ALL) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            NoteSort.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (sort) {
                                                NoteSort.DATE_NEWEST -> "Date · newest"
                                                NoteSort.DATE_OLDEST -> "Date · oldest"
                                                NoteSort.TITLE_AZ -> "Title · A–Z"
                                                NoteSort.TITLE_ZA -> "Title · Z–A"
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.setSort(sort)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
            ExpandedFullScreenContainedSearchBar(
                state = searchBarState,
                inputField = inputField,
                colors = appBarWithSearchColors.searchBarColors
            ) {
                if (state.notes.isEmpty()) {
                    EmptyState(face = "(¬_¬)", message = "No matches")
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = {
                                    textFieldState.setTextAndPlaceCursorAtEnd(note.title)
                                    scope.launch { searchBarState.animateToCollapsed() }
                                    onOpenNote(note.id)
                                },
                                onMoreClick = { actionNote = note }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButtonMenu(
                expanded = fabMenuExpanded,
                button = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            if (fabMenuExpanded) {
                                TooltipAnchorPosition.Start
                            } else {
                                TooltipAnchorPosition.Above
                            }
                        ),
                        tooltip = { PlainTooltip { Text("Menu") } },
                        state = rememberTooltipState()
                    ) {
                        ToggleFloatingActionButton(
                            modifier = Modifier
                                .semantics {
                                    traversalIndex = -1f
                                    stateDescription =
                                        if (fabMenuExpanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = fabVisible || fabMenuExpanded,
                                    alignment = Alignment.BottomEnd
                                )
                                .focusRequester(fabFocusRequester),
                            checked = fabMenuExpanded,
                            onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                        ) {
                            val imageVector by remember {
                                derivedStateOf {
                                    if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                                }
                            }
                            Icon(
                                painter = rememberVectorPainter(imageVector),
                                contentDescription = null,
                                modifier = Modifier.animateIcon({ checkedProgress })
                            )
                        }
                    }
                }
            ) {
                FloatingActionButtonMenuItem(
                    onClick = {
                        fabMenuExpanded = false
                        viewModel.createNote(onOpenNote)
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("New note") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        fabMenuExpanded = false
                        onOpenFolders()
                    },
                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    text = { Text("Folders") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        fabMenuExpanded = false
                        viewModel.setFilter(NoteFilter.ARCHIVE)
                    },
                    icon = { Icon(Icons.Default.Archive, contentDescription = null) },
                    text = { Text("Archive") }
                )
                FloatingActionButtonMenuItem(
                    onClick = {
                        fabMenuExpanded = false
                        viewModel.setFilter(NoteFilter.TRASH)
                    },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    text = { Text("Trash") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 840.dp)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                if (showHomeFilters) {
                    val filterOptions = listOf(
                        FilterOption("All", NoteFilter.ALL, Icons.AutoMirrored.Outlined.Notes),
                        FilterOption("Pinned", NoteFilter.PINNED, Icons.Default.PushPin)
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            ButtonGroupDefaults.ConnectedSpaceBetween
                        ),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        filterOptions.forEachIndexed { index, option ->
                            val selected = state.filter == option.filter
                            ToggleButton(
                                checked = selected,
                                onCheckedChange = { viewModel.setFilter(option.filter) },
                                shapes = when (index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    else -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                },
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    checkedContainerColor = MaterialTheme.colorScheme.onSurface,
                                    checkedContentColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.semantics { role = Role.RadioButton }
                            ) {
                                Icon(option.icon, contentDescription = null)
                                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                                Text(option.label)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (state.filter == NoteFilter.TRASH && state.notes.isNotEmpty()) {
                    TextButton(
                        onClick = { viewModel.emptyTrash() },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("Empty trash")
                    }
                }
                if (state.notes.isEmpty()) {
                    EmptyState(
                        face = when (state.filter) {
                            NoteFilter.TRASH -> "(╥﹏╥)"
                            NoteFilter.ARCHIVE -> "(´-﹏-`)"
                            NoteFilter.PINNED -> "(￣ヘ￣)"
                            NoteFilter.FOLDER -> "(・_・;)"
                            else -> "(¬_¬)"
                        },
                        message = when (state.filter) {
                            NoteFilter.TRASH -> "Nothing in trash"
                            NoteFilter.ARCHIVE -> "Nothing archived"
                            NoteFilter.PINNED -> "No pinned notes"
                            NoteFilter.FOLDER -> "No notes in this folder"
                            else -> "No notes yet"
                        },
                        stateModifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.notes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onOpenNote(note.id) },
                                onMoreClick = { actionNote = note }
                            )
                        }
                    }
                }
            }
        }
    }

    moveNote?.let { note ->
        ModalBottomSheet(
            onDismissRequest = { moveNote = null },
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.padding(bottom = 28.dp)) {
                Text(
                    "Move to folder",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Text(
                    note.title.ifBlank { "Untitled" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                if (state.folders.isEmpty()) {
                    Text(
                        "No folders yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(120.dp),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.heightIn(max = 360.dp)
                    ) {
                        gridItems(state.folders, key = { it.id }) { folder ->
                            FolderCard(
                                folder = folder,
                                noteCount = 0,
                                onClick = {
                                    viewModel.moveToFolder(note, folder.id)
                                    moveNote = null
                                }
                            )
                        }
                    }
                }
                TextButton(
                    onClick = {
                        viewModel.moveToFolder(note, null)
                        moveNote = null
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Remove from folder")
                }
            }
        }
    }

    actionNote?.let { note ->
        val noteColor = note.color.toComposeColorOrNull()
        val updatedLabel = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
            .format(Date(note.updatedAt))
        ModalBottomSheet(
            onDismissRequest = { actionNote = null },
            sheetState = sheetState,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            when (state.filter) {
                NoteFilter.TRASH -> {
                    ActionSheetContent(
                        title = note.title.ifBlank { "Untitled" },
                        subtitle = "In trash · $updatedLabel",
                        leading = { NoteSheetThumbnail(noteColor) },
                        quickActions = listOf(
                            SheetQuickAction(Icons.Default.RestoreFromTrash, "Restore") {
                                viewModel.restore(note)
                                actionNote = null
                            },
                            SheetQuickAction(Icons.Default.DeleteForever, "Delete") {
                                viewModel.deleteForever(note)
                                actionNote = null
                            },
                            SheetQuickAction(Icons.Default.Share, "Share") {
                                shareNote(context, note)
                                actionNote = null
                            }
                        ),
                        listActions = listOf(
                            SheetListAction(
                                Icons.Default.RestoreFromTrash,
                                "Restore note",
                                "Move back to your notes list",
                                onClick = {
                                    viewModel.restore(note)
                                    actionNote = null
                                }
                            ),
                            SheetListAction(
                                Icons.Default.DeleteForever,
                                "Delete forever",
                                "Permanently remove this note",
                                onClick = {
                                    viewModel.deleteForever(note)
                                    actionNote = null
                                }
                            )
                        )
                    )
                }
                NoteFilter.ARCHIVE -> {
                    ActionSheetContent(
                        title = note.title.ifBlank { "Untitled" },
                        subtitle = "Archived · $updatedLabel",
                        leading = { NoteSheetThumbnail(noteColor) },
                        quickActions = listOf(
                            SheetQuickAction(Icons.Default.Archive, "Unarchive") {
                                viewModel.archive(note, false)
                                actionNote = null
                            },
                            SheetQuickAction(Icons.Default.Delete, "Trash") {
                                viewModel.trash(note)
                                actionNote = null
                            },
                            SheetQuickAction(Icons.Default.Share, "Share") {
                                shareNote(context, note)
                                actionNote = null
                            }
                        ),
                        listActions = listOf(
                            SheetListAction(
                                Icons.Default.Archive,
                                "Unarchive",
                                "Return this note to the main list",
                                onClick = {
                                    viewModel.archive(note, false)
                                    actionNote = null
                                }
                            ),
                            SheetListAction(
                                Icons.Default.Delete,
                                "Move to trash",
                                "Remove from archive",
                                onClick = {
                                    viewModel.trash(note)
                                    actionNote = null
                                }
                            )
                        )
                    )
                }
                else -> {
                    ActionSheetContent(
                        title = note.title.ifBlank { "Untitled" },
                        subtitle = updatedLabel,
                        leading = { NoteSheetThumbnail(noteColor) },
                        trailing = {
                            IconButton(onClick = {
                                viewModel.togglePin(note)
                                actionNote = null
                            }) {
                                Icon(
                                    Icons.Default.PushPin,
                                    contentDescription = if (note.isPinned) "Unpin" else "Pin",
                                    tint = if (note.isPinned) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        },
                        quickActions = listOf(
                            SheetQuickAction(
                                Icons.Default.PushPin,
                                if (note.isPinned) "Unpin" else "Pin"
                            ) {
                                viewModel.togglePin(note)
                                actionNote = null
                            },
                            SheetQuickAction(Icons.Default.Archive, "Archive") {
                                viewModel.archive(note, true)
                                actionNote = null
                            },
                            SheetQuickAction(Icons.Default.Share, "Share") {
                                shareNote(context, note)
                                actionNote = null
                            }
                        ),
                        listActions = listOf(
                            SheetListAction(
                                Icons.AutoMirrored.Filled.DriveFileMove,
                                "Move",
                                "Choose a folder",
                                onClick = {
                                    actionNote = null
                                    moveNote = note
                                }
                            ),
                            SheetListAction(
                                Icons.Default.Delete,
                                "Move to trash",
                                "You can restore it later",
                                onClick = {
                                    viewModel.trash(note)
                                    actionNote = null
                                }
                            )
                        )
                    )
                }
            }
        }
    }

}

@Composable
private fun NoteSheetThumbnail(noteColor: Color?) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = MaterialTheme.shapes.small,
        color = noteColor ?: MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.AutoMirrored.Outlined.Notes,
                contentDescription = null,
                tint = if (noteColor == null) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    Color.Black.copy(alpha = 0.55f)
                }
            )
        }
    }
}

private fun shareNote(context: android.content.Context, note: NoteEntity) {
    val share = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "${note.title}\n\n${RichText.plainPreview(note.content, 4000)}"
        )
    }
    context.startActivity(Intent.createChooser(share, "Share note"))
}
