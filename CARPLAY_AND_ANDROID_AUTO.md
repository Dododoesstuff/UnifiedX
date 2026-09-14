# CarPlay & Android Auto Integration Architecture for UnifiedX

This project contains cross-platform in-car infotainment support for **Android Auto**, **Android Automotive OS**, and **Apple CarPlay**.

---

## 1. Android Auto & Android Automotive OS Support (Native Android)

### A. MediaBrowserServiceCompat (`AutoMediaBrowserService.kt`)
Android Auto and Android Automotive communicate with UnifiedX via the standard Media Browser architecture:
- **Service**: `com.example.player.AutoMediaBrowserService`
- **Actions**:
  - `android.media.browse.MediaBrowserService`
  - `android.intent.action.MEDIA_BUTTON`
- **Root Category Browsing**:
  - 🌐 *All Unified Tracks*
  - 🟢 *Spotify Hits*
  - 🔴 *YouTube Audio*
  - ❤️ *Liked Tracks*
  - 💾 *Downloaded / Offline*

### B. Automotive App Descriptor (`res/xml/automotive_app_desc.xml`)
Declares `<automotiveApp><uses name="media" /></automotiveApp>` metadata consumed by Google Automotive Services and Android Auto projection units.

### C. AndroidManifest.xml Integration
- `com.google.android.gms.car.application` metadata referencing `@xml/automotive_app_desc`
- `androidx.car.app` and `mediaPlayback` foreground service declarations.

---

## 2. Apple CarPlay Architecture (iOS Export / Cross-Platform Spec)

When compiling or exporting UnifiedX to iOS / Multiplatform, CarPlay audio playback relies on the following components:

### A. Entitlements (`CarPlay.entitlements`)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>com.apple.developer.carplay-audio</key>
    <true/>
</dict>
</plist>
```

### B. Info.plist Scene Manifest for CarPlay
```xml
<key>UIApplicationSceneManifest</key>
<dict>
    <key>UISceneConfigurations</key>
    <dict>
        <key>CPTemplateApplicationSceneSessionRoleApplication</key>
        <array>
            <dict>
                <key>UISceneClassName</key>
                <string>CPTemplateApplicationScene</string>
                <key>UISceneDelegateClassName</key>
                <string>CarPlaySceneDelegate</string>
            </dict>
        </array>
    </dict>
</dict>
```

### C. CarPlay Audio Architecture (`CarPlaySceneDelegate.swift`)
1. **`CPTemplateApplicationSceneDelegate`**: Manages the CarPlay template interface.
2. **`CPTabBarTemplate` / `CPListTemplate`**: Provides tabs for *Discover*, *Spotify*, *YouTube*, and *Downloaded*.
3. **`CPNowPlayingTemplate`**: Integrates with `MPRemoteCommandCenter` and `MPNowPlayingInfoCenter` for in-dash steering wheel controls and album artwork rendering.
