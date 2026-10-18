# SyncPatch

Morphe-compatible patch bundle for **Sync for Reddit** that fixes **hardcoded fonts** so
they follow the app's own per-view-mode **title font settings** instead of the stock
hardcoded Product Sans / Roboto.

> Works with [Morphe](https://morphe.software) Manager or the Morphe CLI.
> Target app: Sync for Reddit / Sync Pro / Sync Dev **v23.06.30-13:39** (versionCode 23033).

## What the patch does

Sync has a per-view-mode title font setting (`*_font_title` for `list`, `compact`,
`smaller_card`, `small_card`, `card` and `slide`), but many of its views are hardcoded
to Product Sans or Roboto and ignore those settings.

**"Fix hardcoded title fonts"** reroutes every hardcoded call to the app's typeface
manager through an extension that dynamically resolves the title font of the view mode
the user is currently in:

```
mode 0 (List)          -> list_font_title
mode 1 (Compact)       -> compact_font_title
mode 2 (Smaller cards) -> smaller_card_font_title
mode 3 (Small cards)   -> small_card_font_title
mode 4 (Cards)         -> card_font_title
otherwise (Slides)     -> slide_font_title
```

If anything is unavailable (e.g. settings singletons not yet initialized), the extension
falls back to Product Sans, Sync's stock title font — the same look as before patching.

### Covered views

| Area | Views |
|---|---|
| Main UI | Toolbar title, search box, URL views (`UrlView`, `AltUrlView`), voting chip, comment chip |
| Lists | Drawer header (username & stats), subreddit section headers, comment section header |
| Settings | Preference row titles (Sync/Color/CheckBox/RadioButton/List/Root styles), category headers, push preference, Monet dark color override |
| Other screens | Theme editor preview, filter / bypass page headers, multi-input sheet title, onboarding button, `MaxButton` |
| Common | `ProductSansTextView`, `LinkProductSansTextView`, `RobotoTextView`, `SecondaryRobotoTextView` |

### Intentionally not touched

| Site | Reason |
|---|---|
| Dialog message bodies (`AbstractAlertDialogBottomSheet`) | Body text, not a title |
| Monospace code spans (`nc/d`), JSON export (`ca/a`) | Code font is intentionally monospaced |
| `BaseTextView` | Already driven by the app's font settings |
| Preference summaries | Kept as Roboto on purpose |
| Multi-input sheet URL / subreddit inputs | Kept a fixed font for URL readability |

## Usage

1. Get `patches.mpp` from the [releases](https://github.com/burakdede0/SyncPatch/releases) page.
2. In Morphe Manager / CLI, select **Sync for Reddit v23.06.30-13:39** and the patch
   **Fix hardcoded title fonts** (enabled by default).
3. Apply, install, and pick your title fonts per view mode in Sync's settings as usual.
   All views listed above now follow them.

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
gradle :patches:compileKotlin :validator:classes

# assemble patches.mpp (kotlin-stdlib is bundled and dexed, as Morphe expects)
./build_mpp.sh
```

Optional environment variables for both scripts: `ANDROID_SDK_DIR`, and for
`build_mpp.sh` additionally `STDLIB_VERSION`, `VERSION`, `JAVA_HOME`.

### Validation

This repo includes a validator that applies the patch bundle to `sync.apk` in memory
and reports success/failure of every fingerprint and replacement. It never writes a
patched APK, and optionally writes the patched dex files to disk for inspection
(`-Dvalidate.output=...`):

```sh
gradle :validator:run
```

Place an unpatched `sync.apk` (v23.06.30-13:39) in the repository root first; see
`validator/build.gradle.kts` for the file names used.

## Repository layout

```
build_ext.sh              compiles + dexes the runtime extension -> extensions/titlefont.mpe
build_mpp.sh              assembles patches.mpp (classes, kotlin-stdlib, dexing, manifest)
patches/                  the patch source (Kotlin; fingerprints + replacement logic)
patches/src/main/resources/extensions/titlefont.mpe
                          the runtime extension (DEX) merged into the app [generated]
extension/                Java source of the extension + compile-time stubs
validator/                local validation driver (applies the patch in memory, no output)
libs/                     locally built morhe-patcher jar (GPLv3, unmodified)
LICENSE / NOTICE          licensing information
```

## License

Licensed under [GPLv3](LICENSE), like the Morphe Patcher this bundle is built on.
The patch targets Sync for Reddit by Laurence Dawson; this project is not affiliated
with the app's developer. Use at your own risk.
