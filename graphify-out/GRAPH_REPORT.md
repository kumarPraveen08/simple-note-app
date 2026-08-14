# Graph Report - MyApplication  (2026-08-14)

## Corpus Check
- 38 files · ~155,501 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 341 nodes · 520 edges · 28 communities (24 shown, 4 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 27 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ddd431e6`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

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
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]

## God Nodes (most connected - your core abstractions)
1. `NotesRepository` - 27 edges
2. `NoteEditorViewModel` - 24 edges
3. `HomeViewModel` - 18 edges
4. `NoteEntity` - 16 edges
5. `ActionSheetContent()` - 13 edges
6. `Long` - 12 edges
7. `HomeScreen()` - 12 edges
8. `NoteDao` - 10 edges
9. `NotesApp()` - 10 edges
10. `Long` - 9 edges

## Surprising Connections (you probably didn't know these)
- `NotesApp()` --calls--> `NoteEditorScreen()`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/NotesApp.kt → app/src/main/java/com/example/myapplication/ui/editor/NoteEditorScreen.kt
- `NotesApp()` --calls--> `HomeScreen()`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/NotesApp.kt → app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt
- `NotesApp()` --calls--> `NotesExpressiveTheme()`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/NotesApp.kt → app/src/main/java/com/example/myapplication/ui/theme/Theme.kt
- `HomeScreen()` --calls--> `SheetQuickAction`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt → app/src/main/java/com/example/myapplication/ui/components/ActionSheets.kt
- `HomeScreen()` --calls--> `SheetListAction`  [INFERRED]
  app/src/main/java/com/example/myapplication/ui/home/HomeScreen.kt → app/src/main/java/com/example/myapplication/ui/components/ActionSheets.kt

## Import Cycles
- None detected.

## Communities (28 total, 4 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.12
Nodes (13): AttachmentEntity, Boolean, Flow, FolderEntity, List, Long, NoteEntity, NoteSort (+5 more)

### Community 1 - "Community 1"
Cohesion: 0.10
Nodes (26): android, Boolean, FolderEntity, Int, Long, Modifier, NoteEntity, String (+18 more)

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

### Community 17 - "Community 17"
Cohesion: 0.25
Nodes (7): Android Studio, Build guide, Command line, Cut a GitHub release, Release APKs, Sign with your own key (optional, recommended), What you need

### Community 18 - "Community 18"
Cohesion: 0.50
Nodes (3): About, Features, Screenshots

### Community 21 - "Community 21"
Cohesion: 0.18
Nodes (10): applicationId, artifactType, kind, type, baselineProfiles, elements, elementType, minSdkVersionForDexing (+2 more)

### Community 23 - "Community 23"
Cohesion: 0.10
Nodes (31): AppThemeStyle, Boolean, List, Modifier, String, Unit, HomeViewModel, String (+23 more)

### Community 24 - "Community 24"
Cohesion: 0.43
Nodes (6): AppThemeStyle, Color, AppThemePalette, palette(), Surfaces, ThemePalettePreview

### Community 26 - "Community 26"
Cohesion: 0.40
Nodes (4): AppThemeStyle, Boolean, ThemeMode, NotesExpressiveTheme()

### Community 27 - "Community 27"
Cohesion: 0.40
Nodes (3): Bundle, ComponentActivity, MainActivity

## Knowledge Gaps
- **91 isolated node(s):** `String`, `version`, `type`, `kind`, `applicationId` (+86 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **4 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `NoteEditorViewModel` connect `Community 3` to `Community 7`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Why does `HomeViewModel` connect `Community 4` to `Community 7`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **Why does `NotesApp()` connect `Community 23` to `Community 1`, `Community 26`, `Community 27`?**
  _High betweenness centrality (0.025) - this node is a cross-community bridge._
- **Are the 2 inferred relationships involving `ActionSheetContent()` (e.g. with `FoldersScreen()` and `HomeScreen()`) actually correct?**
  _`ActionSheetContent()` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `String`, `version`, `type` to the rest of the system?**
  _91 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.12051282051282051 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.10098522167487685 - nodes in this community are weakly interconnected._