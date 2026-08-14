# Build guide

## What you need

- [Android Studio](https://developer.android.com/studio) (latest stable)
- JDK 11 or newer (Android Studio’s JDK is fine)
- An Android phone or emulator on API 24+

The project uses Gradle 9.6.1, Kotlin 2.4, and AGP 9.3. `minSdk` is 24, `targetSdk` is 36.

## Android Studio

1. Clone the repo and open this folder in Android Studio.
2. Let Gradle sync finish.
3. Pick a device or emulator.
4. Run the `app` configuration.

## Command line

From the project root:

```bash
# debug APK
./gradlew :app:assembleDebug

# install on a connected device
./gradlew :app:installDebug
```

Debug APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

Release (unsigned; minify is off):

```bash
./gradlew :app:assembleRelease
```

On Windows, use `gradlew.bat` instead of `./gradlew`.
