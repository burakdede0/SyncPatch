# SyncPatch

Morphe-compatible patch bundle for **Sync for Reddit** with quality-of-life fixes:

- **Predictive back gesture** - at the main screen, back is handed to the
  system so a swipe plays the native predictive transition into the launcher
  (Android 14+), and a full swipe exits the app on the first gesture, with no
  custom animations drawn by the patch. Inner states (drawer, panels, tap bar
  screens, home feed switches) keep app-handled back. Also sets the predictive
  back manifest flag (image viewer activities keep their own back behavior).
  Users with Sync's "posts home" setting on keep app-handled back everywhere,
  because feed switching has no observable event to track.
- **Exit on single back press** - Sync shows "Press BACK again to exit" and only
  closes on the second BACK press / swipe. The confirmation has no setting to turn it
  off (the `exit_preference` preference is not exposed in the UI and always defaults
  to on), so this patch makes the usual BACK handler finish the app on the first
  press, like every other app. Drawer-close-on-back still works as before.
- **Fix hardcoded title fonts** - fixes views that are hardcoded to Product Sans or
  Roboto so they follow the app's own per-view-mode **title font settings**.

> Works with [Morphe](https://morphe.software) Manager or the Morphe CLI.
> Target app: Sync for Reddit / Sync Pro / Sync Dev **v23.06.30-13:39** (versionCode 23033).

## Usage

1. Get `patches.mpp` from the [releases](https://github.com/burakdede0/SyncPatch/releases) page.
2. In Morphe Manager / CLI, select **Sync for Reddit v23.06.30-13:39** and the patches
   you want (all are enabled by default):
  - **Predictive back gesture**
  - **Exit on single back press**
  - **Fix hardcoded title fonts**
3. Apply and install; the affected views follow Sync's own settings (title fonts are
   picked per view mode in Sync's settings).

The bundle declares compatibility for all three Sync package variants:

| App | Package name |
|---|---|
| Sync for Reddit | `com.laurencedawson.reddit_sync` |
| Sync Pro | `com.laurencedawson.reddit_sync.pro` |
| Sync Dev | `com.laurencedawson.reddit_sync.dev` |

## Building from source

Requirements: JDK 17, Gradle, Android SDK (build-tools + any recent platform).

```sh
# compile the extension runtime (extension/src -> titlefont.mpe)
./build_ext.sh

# compile the patch bundle classes (also downloads dependencies)
gradle :patches:compileKotlin

# assemble patches.mpp (kotlin-stdlib is bundled and dexed, as Morphe expects)
./build_mpp.sh
```

Optional environment variables for both scripts: `ANDROID_SDK_DIR`, and for
`build_mpp.sh` additionally `STDLIB_VERSION`, `VERSION`, `JAVA_HOME`.

## Notices & credits

- **Not related to Reddit, Inc.** This project is a third-party community patch
  bundle. "Reddit" is a trademark of Reddit, Inc.; the term is used only to
  identify what the target app interacts with. SyncPatch is not affiliated with,
  endorsed by, sponsored by, or officially connected to Reddit, Inc. in any way.
- **HK Morphe Patches** ([github.com/humzakh/HK-Morphe-Patches](https://github.com/humzakh/HK-Morphe-Patches)) -
  the manifest handling of the *Predictive back gesture* patch is derived from
  their GPLv3 "Predictive back gesture (Reddit Sync)" patch, with a deliberate
  change so that predictive back also covers app exit. Check out the project for
  many more Sync for Reddit patches.
- **Patcheddit** ([patcheddit.com](https://patcheddit.com)) - another actively
  maintained patch bundle for Sync for Reddit with fixes for Reddit's API/APK
  changes. Worth a look if you want more patches for the app.
- **Sync for Reddit** is an app by Laurence Dawson; the patches modify the app at
  build time on the user's own device and are not affiliated with its developer.

## License

Licensed under [GPLv3](LICENSE), like the Morphe Patcher this bundle is built on -
see also the notices above. Use at your own risk.
