package app.syncpatches.patches.titlefont

import app.morphe.patcher.Fingerprint

/**
 * Fingerprints for every site of a hardcoded `t7.r0` typeface manager call.
 *
 * The typeface manager `Lt7/r0;` (decompiles to `TypefaceManager`) provides:
 * - `d(I)Landroid/graphics/Typeface;` — typeface by font id (the patches replace these calls),
 * - `a()Landroid/graphics/Typeface;` and `c()Landroid/graphics/Typeface;` — hardwired
 *   combinations used by some views.
 *
 * Class fingerprints without a method name are intentional: every matching
 * `Lt7/r0;` call in the class is replaced, and the patch asserts the number of
 * replacements against the expected count, so a mismatched app version fails
 * loudly instead of silently skipping sites.
 */

// region Toolbar and navigation

object MaterialYouToolbarFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/core/MaterialYouToolbar;",
)

// endregion

// region Preference screens

object PreferenceWrapperFragmentFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/fragments/preferences/PreferenceWrapperFragment;",
)

object RootPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/custom/RootPreference;",
)

object ColorPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/ColorPreference;",
)

object SyncPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/SyncPreference;",
)

object SyncListPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/SyncListPreference;",
)

object SyncCheckBoxPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/SyncCheckBoxPreference;",
)

object SyncRadioButtonPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/SyncRadioButtonPreference;",
)

object CategoryHeaderPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/CategoryHeaderPreference;",
)

object CategoryInfoHeaderPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/CategoryInfoHeaderPreference;",
)

object PushPreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/PushPreference;",
)

object MonetDarkColorOverridePreferenceFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/preferences/defaults/monet/MonetDarkColorOverridePreference;",
)

// endregion

// region Other screens

/** Theme editor preview. */
object SingleThemePreferencePreviewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/preferences/SingleThemePreferencePreview;",
)

/** Filter and Bypass settings page headers. */
object FilterFragmentHeaderFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/fragments/preferences/filters/FilterFragment\$d;",
)

object BypassFragmentHeaderFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/fragments/preferences/bypass/BypassFragment\$d;",
)

// endregion

// region Drawer

object DrawerHeaderHolderFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/viewholders/drawer/DrawerHeaderHolder;",
)

/** Subreddit view holder, replaced with a different instruction pattern. */
object SubredditHolderFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/viewholders/sections/subreddits/SubredditHolder;",
    name = "o",
    parameters = listOf("I", "Ljava/lang/String;", "Ljava/lang/String;"),
)

// endregion

// region Views hardcoded to Product Sans and friends

object AltUrlViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/posts/cards/AltUrlView;",
)

object UrlViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/posts/simple/UrlView;",
)

object CommentChipFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/posts/simple/CommentChip;",
)

object VotingChipFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/posts/simple/VotingChip;",
)

object MonetSearchBoxViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/monet/MonetSearchBoxView;",
)

object OnboardingRestoreButtonFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/onboarding/OnboardingRestoreButton;",
)

object ProductSansTextViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/text/basic/ProductSansTextView;",
)

object LinkProductSansTextViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/text/basic/LinkProductSansTextView;",
)

object RobotoTextViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/text/basic/RobotoTextView;",
)

object SecondaryRobotoTextViewFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ui/views/text/basic/SecondaryRobotoTextView;",
)

object MaxButtonFingerprint : Fingerprint(
    definingClass = "Lcom/laurencedawson/reddit_sync/ads/max/MaxButton;",
)

/** Comment section header ("Load ... comments"). */
object CommentHeaderHolderFingerprint : Fingerprint(
    definingClass = "Lpb/a;",
)

// endregion

// region Input fragment

/**
 * Multi input bottom sheet. Only the first `r0.a()` call (title input,
 * field `s0`) is replaced, the URL and subreddit inputs keep their font.
 */
object MultiInputFragmentFingerprint : Fingerprint(
    definingClass = "Lma/n;",
    name = "T1",
    parameters = listOf(
        "Landroid/view/LayoutInflater;",
        "Landroid/view/ViewGroup;",
        "Landroid/os/Bundle;",
    ),
    returnType = "Landroid/view/View;",
)

// endregion
