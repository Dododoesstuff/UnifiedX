# CrossBeat Music Player 🎵

Unified cross-platform streaming, local playback, and real-time synchronized lyrics.

## 📱 How to Download & Install APK on Your Phone

### Option A: Direct Download via GitHub Actions (Automated CI/CD)
Whenever you push changes or trigger the workflow:
1. Go to the **Actions** tab on your GitHub repository.
2. Select the latest **"Build and Release APK"** workflow run.
3. Scroll down to the **Artifacts** section at the bottom of the page.
4. Click **`CrossBeat-Debug-APK`** to download the zip containing the `.apk` directly to your phone or computer.
5. Extract and open the `.apk` file to install on your Android device.

### Option B: Local Gradle Build
If you clone the repo to your computer with Android Studio:
```bash
./gradlew assembleDebug
```
The output APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## ✨ Features
- **Unified Hybrid Playback**: Seamless playback between Spotify, YouTube Music, and local files.
- **Synced Lyrics API**: Auto-synchronized scrolling lyrics with tap-to-seek and dynamic line highlighting.
- **Fluid Gestures**: Swipe down to minimize player, swipe left/right on artwork to skip tracks.
- **Baby Blue Theme**: Modern dark aesthetic with baby blue and midnight navy accents.
- **Offline Mode**: Local music indexing with full search and playlist management.
