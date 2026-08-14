# Graph Report - MyApplication  (2026-08-13)

## Corpus Check
- 36 files · ~15,359 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 324 nodes · 501 edges · 25 communities (22 shown, 3 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 24 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]

## God Nodes (most connected - your core abstractions)
1. `NotesRepository` - 27 edges
2. `NoteEditorViewModel` - 24 edges
3. `HomeViewModel` - 18 edges
4. `NoteEntity` - 16 edges
5. `Long` - 12 edges
6. `ActionSheetContent()` - 12 edges
7. `HomeScreen()` - 12 edges
8. `NoteDao` - 10 edges
9. `NotesApp()` - 10 edges
10. `Long` - 9 edges

## Surprising Connections (you probably didn't know these)
- `NotesApp()` --calls--> `Composable`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/NotesApp.kt → app/src/main/java/com/example/myapplication/ui/components/ActionSheets.kt
- `NotesApp()` --calls--> `LookAndFeelScreen()`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/NotesApp.kt → app/src/main/java/com/example/myapplication/ui/settings/LookAndFeelScreen.kt
- `SettingsScreen()` --calls--> `SettingsNavItem()`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/settings/SettingsScreen.kt → app/src/main/java/com/example/myapplication/ui/components/ActionSheets.kt
- `HomeScreen()` --calls--> `SheetQuickAction`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt → app/src/main/java/com/example/myapplication/ui/components/ActionSheets.kt
- `HomeScreen()` --calls--> `SheetListAction`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt → app/src/main/java/com/example/myapplication/ui/components/ActionSheets.kt

## Import Cycles
- None detected.

## Communities (25 total, 3 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.12
Nodes (13): AttachmentEntity, Boolean, Flow, FolderEntity, List, Long, NoteEntity, NoteSort (+5 more)

### Community 1 - "Community 1"
Cohesion: 0.06
Nodes (36): android, Boolean, FolderEntity, Int, Long, Modifier, NoteEntity, String (+28 more)

### Community 2 - "Community 2"
Cohesion: 0.13
Nodes (11): AttachmentEntity, Flow, FolderEntity, List, Long, NoteEntity, NoteVersionEntity, AttachmentDao (+3 more)

### Community 3 - "Community 3"
Cohesion: 0.11
Nodes (14): AttachmentEntity, Boolean, Int, Long, NotesApplication, StateFlow, String, Uri (+6 more)

### Community 4 - "Community 4"
Cohesion: 0.10
Nodes (12): Boolean, FolderEntity, Long, NoteEntity, NotesApplication, NoteSort, StateFlow, String (+4 more)

### Community 5 - "Community 5"
Cohesion: 0.17
Nodes (8): AttachmentDao, Context, FolderDao, get(), NoteDatabase, NoteDao, NoteVersionDao, RoomDatabase

### Community 6 - "Community 6"
Cohesion: 0.14
Nodes (9): AppThemeStyle, Boolean, Flow, NoteSort, String, ThemeMode, Keys, SettingsRepository (+1 more)

### Community 7 - "Community 7"
Cohesion: 0.12
Nodes (11): AppThemeStyle, Boolean, NotesApplication, NoteSort, StateFlow, String, ThemeMode, factory() (+3 more)

### Community 8 - "Community 8"
Cohesion: 0.27
Nodes (6): AnnotatedString, Color, Int, String, RichText, toComposeColorOrNull()

### Community 9 - "Community 9"
Cohesion: 0.36
Nodes (5): List, String, ChecklistItem, ChecklistCodec, TagCodec

### Community 10 - "Community 10"
Cohesion: 0.47
Nodes (4): Application, NotesApplication, NotesRepository, SettingsRepository

### Community 11 - "Community 11"
Cohesion: 0.29
Nodes (6): AppThemeStyle, ChecklistItem, NoteColors, NoteFilter, NoteSort, ThemeMode

### Community 12 - "Community 12"
Cohesion: 0.40
Nodes (4): AttachmentEntity, FolderEntity, NoteEntity, NoteVersionEntity

### Community 21 - "Community 21"
Cohesion: 0.18
Nodes (10): applicationId, artifactType, kind, type, baselineProfiles, elements, elementType, minSdkVersionForDexing (+2 more)

### Community 22 - "Community 22"
Cohesion: 0.40
Nodes (3): Bundle, ComponentActivity, MainActivity

### Community 23 - "Community 23"
Cohesion: 0.18
Nodes (22): AppThemeStyle, Boolean, List, Modifier, String, SettingsViewModel, ActionSheetContent(), ActionSheetHeader() (+14 more)

### Community 24 - "Community 24"
Cohesion: 0.43
Nodes (6): AppThemeStyle, Color, AppThemePalette, palette(), Surfaces, ThemePalettePreview

## Knowledge Gaps
- **80 isolated node(s):** `version`, `type`, `kind`, `applicationId`, `variantName` (+75 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `NoteEditorViewModel` connect `Community 3` to `Community 7`?**
  _High betweenness centrality (0.036) - this node is a cross-community bridge._
- **Why does `HomeViewModel` connect `Community 4` to `Community 7`?**
  _High betweenness centrality (0.031) - this node is a cross-community bridge._
- **Why does `NotesApp()` connect `Community 1` to `Community 22`, `Community 23`?**
  _High betweenness centrality (0.027) - this node is a cross-community bridge._
- **What connects `version`, `type`, `kind` to the rest of the system?**
  _80 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.12051282051282051 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.06342494714587738 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.1265597147950089 - nodes in this community are weakly interconnected._