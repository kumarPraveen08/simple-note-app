# Notes

Local-first Android notes app. Jetpack Compose UI, Room on device, no backend.

## What it does

- Notes with title, body, tags, color, checklists, and file attachments
- Folders, pin, archive, trash (restore or delete)
- Version history with restore
- Search plus sort (date or title; pinned stay on top)
- Theme: system / light / dark, optional Material You, eight palettes
- Local profile (name, email, bio)

## Stack

| | |
|---|---|
| Language | Kotlin 2.4 |
| UI | Compose + Material 3 + Navigation |
| Data | Room (`notes.db`) + DataStore |
| Images | Coil |
| minSdk / targetSdk | 24 / 36 |

## Run

Android Studio: open this folder, sync Gradle, run the `app` module.

CLI:

```bash
./gradlew :app:installDebug
```

Needs JDK 11+.

## Layout

```
app/src/main/java/com/example/myapplication/
  data/          Room entities, DAOs, repositories
  ui/home/       note list, filters
  ui/editor/     note editor + autosave
  ui/folders/    folder management
  ui/settings/   settings, look & feel, profile
  ui/theme/      palettes
```

`NotesApplication` owns `NotesRepository` and `SettingsRepository`. Screens talk to those through ViewModels.
