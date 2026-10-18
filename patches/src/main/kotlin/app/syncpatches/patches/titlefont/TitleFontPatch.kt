package app.syncpatches.patches.titlefont

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.BytecodePatchContext
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.syncpatches.patches.shared.Constants
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Replaces every hardcoded typeface manager (`Lt7/r0;`) call so that the affected
 * views follow the title font of the currently active post view mode
 * (card / small card / smaller card / compact / list / slide) instead of the
 * stock hardcoded Product Sans / Roboto.
 *
 * Each replacement swaps a single `invoke-static` instruction 1:1 with
 * `TitleFontFix.titleTypeface()`, which resolves the typeface dynamically at
 * runtime. The extension is merged into the app before this patch executes.
 */

private const val TYPEFACE_MANAGER_CLASS = "Lt7/r0;"

/**
 * smali for replacing `invoke-static {reg}, Lt7/r0;->d(I)Landroid/graphics/Typeface;`
 */
private const val TITLE_TYPEFACE_CALL =
    "invoke-static {}, ${Constants.TITLE_FONT_EXTENSION_CLASS}->titleTypeface()Landroid/graphics/Typeface;"

/**
 * smali for replacing a hardcoded font id const (SubredditHolder case).
 */
private const val TITLE_FONT_ID_CALL =
    "invoke-static {}, ${Constants.TITLE_FONT_EXTENSION_CLASS}->titleFontId()I"

/**
 * Replaces every call to `Lt7/r0;->[methodName](...)` in all methods of
 * [fingerprint]'s class with [replacement], and asserts that the expected
 * number of calls is found and replaced so that a mismatching app version
 * fails loudly instead of silently patching partially.
 */
private context(patchContext: BytecodePatchContext)
fun replaceTypefaceManagerCalls(
    fingerprint: Fingerprint,
    methodName: String,
    parameterTypes: List<String>,
    replacement: String,
    expectedCount: Int,
    expectedDescription: String,
) {
    val classDef = fingerprint.classDef

    var count = 0
    classDef.methods.forEach { method ->
        count += method.replaceTypefaceCalls(methodName, parameterTypes, replacement)
    }

    if (count != expectedCount) {
        throw PatchException(
            "Expected $expectedCount $expectedDescription in ${classDef.type} but found $count"
        )
    }
}

/**
 * Replaces every `invoke-static` to `[TYPEFACE_MANAGER_CLASS]->[methodName][parameterTypes]`
 * in [method] with [replacement] and returns the number of replacements.
 */
private fun MutableMethod.replaceTypefaceCalls(
    methodName: String,
    parameterTypes: List<String>,
    replacement: String,
): Int {
    val implementation = implementation ?: return 0
    var count = 0

    // Replacements are 1:1 single instruction swaps, so iterating by index is safe.
    for (index in implementation.instructions.indices) {
        val instruction = implementation.instructions[index]

        if (instruction.opcode != Opcode.INVOKE_STATIC) continue

        val reference = (instruction as ReferenceInstruction).reference as MethodReference

        if (reference.definingClass == TYPEFACE_MANAGER_CLASS &&
            reference.name == methodName &&
            reference.parameterTypes == parameterTypes
        ) {
            replaceInstruction(index, replacement)
            count++
        }
    }

    return count
}

@Suppress("unused")
val fixHardcodedTitleFontsPatch = bytecodePatch(
    name = "Fix hardcoded title fonts",
    description = "Replaces hardcoded fonts.",
    default = true,
) {
    compatibleWith(
        Constants.SYNC_FREE,
        Constants.SYNC_PRO,
        Constants.SYNC_DEV,
    )

    extendWith("extensions/titlefont.mpe")

    execute {
        // Every individual site. The counts must match Sync v23.06.30-13:39;
        // a mismatch fails the patch with a descriptive error.

        // Toolbar title.
        replaceTypefaceManagerCalls(
            MaterialYouToolbarFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (toolbar title)",
        )

        // Settings screens.
        replaceTypefaceManagerCalls(
            PreferenceWrapperFragmentFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (PreferenceWrapperFragment title)",
        )
        replaceTypefaceManagerCalls(
            RootPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (RootPreference title)",
        )
        replaceTypefaceManagerCalls(
            ColorPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (ColorPreference title)",
        )
        replaceTypefaceManagerCalls(
            SyncPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (SyncPreference title)",
        )
        replaceTypefaceManagerCalls(
            SyncListPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (SyncListPreference title)",
        )
        replaceTypefaceManagerCalls(
            SyncCheckBoxPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (SyncCheckBoxPreference title)",
        )
        replaceTypefaceManagerCalls(
            SyncRadioButtonPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (SyncRadioButtonPreference title)",
        )
        replaceTypefaceManagerCalls(
            CategoryHeaderPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (CategoryHeaderPreference title)",
        )
        replaceTypefaceManagerCalls(
            CategoryInfoHeaderPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (CategoryInfoHeaderPreference title)",
        )
        replaceTypefaceManagerCalls(
            PushPreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (PushPreference title)",
        )
        replaceTypefaceManagerCalls(
            MonetDarkColorOverridePreferenceFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (MonetDarkColorOverridePreference title)",
        )

        // Theme editor preview.
        replaceTypefaceManagerCalls(
            SingleThemePreferencePreviewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (theme preview title)",
        )

        // Filter / Bypass settings page headers.
        replaceTypefaceManagerCalls(
            FilterFragmentHeaderFingerprint,
            methodName = "c", parameterTypes = emptyList(), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (filter header title)",
        )
        replaceTypefaceManagerCalls(
            BypassFragmentHeaderFingerprint,
            methodName = "c", parameterTypes = emptyList(), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (bypass header title)",
        )

        // Drawer.
        replaceTypefaceManagerCalls(
            DrawerHeaderHolderFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 3, expectedDescription = "typeface manager calls (drawer header)",
        )

        // Views hardcoded to Product Sans and friends.
        replaceTypefaceManagerCalls(
            AltUrlViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (AltUrlView)",
        )
        replaceTypefaceManagerCalls(
            UrlViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (UrlView)",
        )
        replaceTypefaceManagerCalls(
            CommentChipFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (CommentChip)",
        )
        replaceTypefaceManagerCalls(
            VotingChipFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (VotingChip)",
        )
        replaceTypefaceManagerCalls(
            MonetSearchBoxViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (search box)",
        )
        replaceTypefaceManagerCalls(
            OnboardingRestoreButtonFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (onboarding button)",
        )
        replaceTypefaceManagerCalls(
            ProductSansTextViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 2, expectedDescription = "typeface manager calls (ProductSansTextView)",
        )
        replaceTypefaceManagerCalls(
            LinkProductSansTextViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (LinkProductSansTextView)",
        )
        replaceTypefaceManagerCalls(
            RobotoTextViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (RobotoTextView)",
        )
        replaceTypefaceManagerCalls(
            SecondaryRobotoTextViewFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (SecondaryRobotoTextView)",
        )
        replaceTypefaceManagerCalls(
            MaxButtonFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (MaxButton)",
        )
        replaceTypefaceManagerCalls(
            CommentHeaderHolderFingerprint,
            methodName = "d", parameterTypes = listOf("I"), replacement = TITLE_TYPEFACE_CALL,
            expectedCount = 1, expectedDescription = "typeface manager calls (comment section header)",
        )

        // Multi input fragment: only the title input (field `s0`) gets the
        // title font; URL and subreddit inputs keep their stock font for
        // readability. The title input is the first of three `Lt7/r0;->a()`
        // calls in the method.
        MultiInputFragmentFingerprint.method.let { method ->
            val implementation = method.implementation!!

            var count = 0
            for (index in implementation.instructions.indices) {
                val instruction = implementation.instructions[index]
                if (instruction.opcode != Opcode.INVOKE_STATIC) continue

                val reference =
                    (instruction as ReferenceInstruction).reference as MethodReference

                if (reference.definingClass == TYPEFACE_MANAGER_CLASS &&
                    reference.name == "a" &&
                    reference.parameterTypes.isEmpty()
                ) {
                    // Patch only the first occurrence: the title input field `s0`.
                    if (count == 0) {
                        method.replaceInstruction(index, TITLE_TYPEFACE_CALL)
                    }
                    count++
                }
            }

            if (count != 3) {
                throw PatchException(
                    "Expected 3 typeface manager calls in MultiInputFragment but found $count"
                )
            }
        }

        // SubredditHolder: the custom subreddit text view chooses a font
        // between `const/4 p3, 0x3` (current subreddit) and `const/16 p3, 0x9`
        // before a single `Lt7/r0;->d(I)` call. Replace the const with the
        // dynamic title font id of the current view mode.
        SubredditHolderFingerprint.method.let { method ->
            val implementation = method.implementation!!

            // The last parameter register of this method (p3), as a raw
            // register index. `parameters` does not include `this`, so for the
            // (virtual) method with parameters (I, String, String) it is
            // registerCount - 3 - 1 + 3 = registerCount - 1.
            val p3Register = implementation.registerCount - method.parameterTypes.size - 1 + 3

            val constIndices = mutableListOf<Int>()
            for (index in implementation.instructions.indices) {
                val instruction = implementation.instructions[index]
                if (instruction.opcode != Opcode.CONST_4) continue

                val register = (instruction as OneRegisterInstruction).registerA
                val literal = (instruction as NarrowLiteralInstruction).narrowLiteral

                // const/4 p3, 0x3
                if (register == p3Register && literal == 3) {
                    constIndices.add(index)
                }
            }

            if (constIndices.size != 1) {
                throw PatchException(
                    "Expected 1 'const/4 p3, 0x3' in SubredditHolder but found ${constIndices.size}"
                )
            }

            val index = constIndices.first()
            method.replaceInstruction(index, TITLE_FONT_ID_CALL)
            method.addInstructions(
                index + 1,
                "move-result p3",
            )
        }
    }
}
