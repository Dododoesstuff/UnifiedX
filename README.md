# UnifiedX Music Player 🎵

Unified cross-platform streaming (Spotify & YouTube), offline caching, real-time synchronized lyrics, in-car Android Auto / CarPlay integration, and home screen widgets.

## 📱 How to Download & Install Stable `.apk` on Your Phone

### Option A: Direct Download from GitHub Releases (Recommended)
Whenever you sync or push commits to GitHub:
1. Navigate to the **Releases** tab on the right side of your GitHub repository.
2. Select the latest release (e.g. **`UnifiedX Latest Continuous Build`** or version tag).
3. Under **Assets**, tap **`UnifiedX-Release.apk`** to download and install directly on your Android phone.

### Option B: Download via GitHub Actions Artifacts
1. Go to the **Actions** tab on your GitHub repository.
2. Select the latest **"Build and Release APK"** workflow run.
3. Scroll down to the **Artifacts** section at the bottom.
4. Download **`UnifiedX-Android-APKs`** to get both the signed release and debug APK files.

### Option C: Local Gradle Build
If you clone the repository locally:
```bash
./gradlew assembleRelease
```
The installable release APK will be located at:
`app/build/outputs/apk/release/app-release.apk`

---

## ✨ Features
- **Unified Hybrid Playback**: Seamless audio playback across Spotify, YouTube Music, and local storage.
- **Synced Lyrics Engine**: Auto-synchronized scrolling lyrics with tap-to-seek timestamp alignment.
- **Android Auto & Automotive OS**: Complete head-unit and steering wheel media browsing & controls.
- **Home Screen Widget**: Quick-access widget with album art rendering, live track metadata, and playback controls.
- **Room Database Sync**: Local caching of playlists, favorites, and user streaming preferences.
