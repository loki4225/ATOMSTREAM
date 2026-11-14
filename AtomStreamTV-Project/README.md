# AtomStreamTV

A lightweight Android TV website launcher built with Kotlin and a native Android UI. Websites open in an embedded Android `WebView`.

## Project details

| Setting | Value |
|---|---|
| Application | AtomStreamTV |
| Package | `com.atomstream.tv` |
| Minimum Android | 6.0 / API 23 |
| Compile / target SDK | 35 |
| Language | Kotlin |
| JDK | 17 |
| Android Gradle Plugin | 8.7.3 |
| Gradle | 8.9 |
| Orientation | Landscape |

## Included websites

- Gotaku — <https://gotaku.to/>
- Cinejoy — <https://cinejoy.pk/>
- Movy — <https://www.movy.sx/>

These are starting shortcuts only. Availability and playback are determined by each site and may change.

## Build with GitHub (no Android Studio required)

1. Download and extract `AtomStreamTV-Complete-Project.zip`.
2. Create a new empty GitHub repository, for example `AtomStreamTV`.
3. In the extracted project folder, open the folder containing `settings.gradle.kts` and `app/`.
4. Upload the *contents* of that folder to the repository root. Do not upload the parent folder as an extra nested directory. The repository root must show `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `app/`, and `.github/`.
5. Commit the files to the `main` branch.
6. Open the repository's **Actions** tab. The workflow named **Android APK Build** starts automatically after the push. You can also select it and choose **Run workflow**.
7. Wait for the run to finish with a green check. Open the completed run and download the artifact named **AtomStreamTV-APK**.
8. Extract the downloaded artifact ZIP. It contains `app-debug.apk`.

The workflow uses hosted GitHub runners, Java 17, Android SDK 35 and Gradle 8.9. It builds with `gradle :app:assembleDebug`; no Gradle wrapper is required.

## Install on an Atom projector running Android 13

1. Download `app-debug.apk` from the GitHub Actions artifact and copy it to a USB drive, or transfer it to the projector using a file-sharing method.
2. On the projector, open **Settings → Security / Privacy** (the exact label varies by firmware) and allow installation from the file manager or browser you will use, if Android asks.
3. Open the APK in the projector's file manager and select **Install**.
4. Launch **AtomStreamTV** from the app list. If the launcher exposes an Android TV apps row, it may also appear there.
5. Use the remote's D-pad to move focus, OK/Enter to select, and Back to return.

Only install APKs built from a repository you trust. Revoke the file manager's “install unknown apps” permission afterward if you enabled it solely for this installation.

## Remote controls

- D-pad: move between controls and website tiles.
- OK / Enter: activate the focused control or open a website.
- Back: go back in website history; if no history remains, return to the home screen.
- Back during website fullscreen video: exit the fullscreen video first.
- Long-press a custom website tile: show the remove confirmation.

## Add and remove websites

Use **Add Website** on the home screen. Enter a display name and an HTTP or HTTPS URL. A missing scheme is converted to `https://`. Duplicate addresses are rejected. Custom entries are stored in app-private SharedPreferences and survive restarts. Default shortcuts cannot be deleted. Long-press a custom tile to remove it.

## Settings and data

- **Clear WebView cache** removes cached web resources but retains cookies and saved shortcuts.
- **Clear browsing data** clears cookies, web storage and cache; it does not delete the custom website list.
- The app does not provide a download manager or expose JavaScript interfaces to websites.

## Playback and compatibility limitations

Playback is not guaranteed for every site. It depends on the site's own policies and implementation, Android System WebView version, codecs supported by the device, cookies, network conditions, content security rules and any DRM requirements. Some websites intentionally block embedded browsers, require a supported external browser, or use DRM systems unavailable to the device's WebView. The app does not bypass DRM, paywalls, sign-in restrictions or other access controls.

The WebView is configured for JavaScript, DOM storage, cookies and third-party cookies, HTML5 media without an additional gesture where the site permits it, HTTP/HTTPS-only top-level navigation, Safe Browsing where supported, and no mixed content. File/content access is disabled and SSL certificate errors are cancelled. Some sites may still not work correctly in embedded WebView.

## Project structure

```text
AtomStreamTV/
├── .github/workflows/android.yml
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/atomstream/tv/
│       │   ├── AddSiteActivity.kt
│       │   ├── MainActivity.kt
│       │   ├── PlayerActivity.kt
│       │   ├── SettingsActivity.kt
│       │   ├── Site.kt
│       │   ├── SiteStore.kt
│       │   ├── Ui.kt
│       │   └── WebViewManager.kt
│       └── res/
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Local build (optional)

With JDK 17, Gradle 8.9 and Android SDK Platform 35 installed:

```bash
gradle :app:assembleDebug
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```
