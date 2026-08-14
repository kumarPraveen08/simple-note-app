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

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Release APKs

One command builds a **universal** APK (all ABIs) plus smaller APKs per CPU:

```bash
./gradlew :app:assembleRelease
```

| File | Use this on |
|---|---|
| `app-universal-release.apk` | Any device (safest pick) |
| `app-arm64-v8a-release.apk` | Most phones (64-bit ARM) |
| `app-armeabi-v7a-release.apk` | Older 32-bit ARM phones |
| `app-x86_64-release.apk` | Emulators / some Chromebooks |

They land in `app/build/outputs/apk/release/`.

### Sign with your own key (optional, recommended)

Without a keystore, release APKs are signed with the debug key. Fine for trying locally. For GitHub releases, make a key and keep it private.

```bash
keytool -genkeypair -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias notes
```

Put this in `keystore.properties` at the repo root (gitignored):

```
storeFile=keystore.jks
storePassword=your-store-password
keyAlias=notes
keyPassword=your-key-password
```

Then run `./gradlew :app:assembleRelease` again.

### Cut a GitHub release

Do this **after** the commits or PR are on `main`. Version lives in `app/build.gradle.kts`: `versionName` (what people see) and `versionCode` (must always go up).

| Kind | `versionName` | Tag | Example |
|---|---|---|---|
| Patch (bugfix) | `1.0.0` → `1.0.1` | `v1.0.1` | typo, crash |
| Minor (feature) | `1.0.1` → `1.1.0` | `v1.1.0` | new screen |
| Major (breaking) | `1.1.0` → `2.0.0` | `v2.0.0` | data format change |
| Alpha | `1.1.0-alpha.1` | `v1.1.0-alpha.1` | early test |
| RC | `1.1.0-rc.1` | `v1.1.0-rc.1` | almost stable |

Alpha / beta / rc are marked **pre-release** and are not “Latest”. Bump `versionCode` by 1 every time, including pre-releases.

1. Merge the PR (or push the commits) to `main`.
2. On `main`, edit `versionName` and `versionCode`.
3. Commit, e.g. `release: 1.0.1`.
4. Push `main`, then tag **the same** version and push the tag:

```bash
git push origin main
git tag v1.0.1
git push origin v1.0.1
```

5. Wait for the **Release** workflow. It creates **Notes 1.0.1** and attaches the APKs. GitHub fills notes from merged PRs.
6. Open the release → **Edit** to write a real description (what changed, who should install universal vs arm64). Save.

Tag must match `versionName` (`v` + name). Don’t tag an old commit.

To sign CI builds with your key, add these repo secrets:

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | `base64 -i keystore.jks` (macOS) or `base64 -w0 keystore.jks` (Linux) |
| `KEYSTORE_PASSWORD` | store password |
| `KEY_ALIAS` | `notes` |
| `KEY_PASSWORD` | key password |

Bump `versionName` / `versionCode` in `app/build.gradle.kts` before tagging.
