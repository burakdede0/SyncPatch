# SyncPatch

Morphe-compatible patch bundle for **Sync for Reddit** with quality-of-life fixes:

- **Predictive back gesture**: enables Android's native predictive back animation.
- **Exit on single back press**: closes the app with a single back press instead
  of asking you a second time.
- **Fix hardcoded title fonts**: titles follow the app's own font settings.

> Works with [Morphe](https://morphe.software) Manager or the Morphe CLI.
> Target app: Sync for Reddit / Sync Pro / Sync Dev **v23.06.30-13:39** (versionCode 23033).

## Usage

1. Add the bundle with [this link](https://morphe.software/add-source?github=burakdede0/SyncPatch)
   in Morphe Manager.
2. Select **Sync for Reddit**, apply, and install the patched app.

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
- **HK Morphe Patches** ([humzakh/HK-Morphe-Patches](https://github.com/humzakh/HK-Morphe-Patches)) -
  the manifest handling of the *Predictive back gesture* patch is derived from
  their GPLv3 "Predictive back gesture (Reddit Sync)" patch, with a deliberate
  change so that predictive back also covers app exit. Check out the project for
  many more Sync for Reddit patches.
- **patcheddit** ([wchill/patcheddit](https://github.com/wchill/patcheddit)) - another actively
  maintained patch bundle for Sync for Reddit with fixes for Reddit's API/APK
  changes. Worth a look if you want more patches for the app.
- **Sync for Reddit** is an app by Laurence Dawson; the patches modify the app at
  build time on the user's own device and are not affiliated with its developer.

## License

Licensed under [GPLv3](LICENSE), like the Morphe Patcher this bundle is built on -
see also the notices above. Use at your own risk.
