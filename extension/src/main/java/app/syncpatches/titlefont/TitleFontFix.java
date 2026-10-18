package app.syncpatches.titlefont;

import android.graphics.Typeface;
import com.laurencedawson.reddit_sync.singleton.SettingsSingleton;
import t7.r0;

/**
 * Runtime extension for the "Fix hardcoded title fonts" patch.
 *
 * Resolves the title typeface of the post view mode currently in use,
 * instead of the hardcoded Product Sans / Roboto used by the stock app.
 *
 * Falls back to Product Sans (font id 11) on any failure.
 */
public final class TitleFontFix {

    private TitleFontFix() {
    }

    /**
     * Returns the title typeface of the currently active post view mode.
     */
    public static Typeface titleTypeface() {
        try {
            return r0.d(titleFontId());
        } catch (Throwable t) {
            // Product Sans was the default title font before patching.
            return r0.d(FONT_PRODUCT_SANS);
        }
    }

    /**
     * Returns the font id of the title font of the currently active post view mode.
     */
    public static int titleFontId() {
        try {
            // Mode values:
            // 0 = List, 1 = Compact, 2 = Smaller cards, 3 = Small cards, 4 = Cards, 5 = Slides.
            switch (com.laurencedawson.reddit_sync.singleton.c.a().e()) {
                case 0:
                    return getSetting(SETTING_LIST);
                case 1:
                    return getSetting(SETTING_COMPACT);
                case 2:
                    return getSetting(SETTING_SMALLER_CARD);
                case 3:
                    return getSetting(SETTING_SMALL_CARD);
                case 4:
                    return getSetting(SETTING_CARD);
                default:
                    return getSetting(SETTING_SLIDE);
            }
        } catch (Throwable t) {
            return FONT_PRODUCT_SANS;
        }
    }

    private static int getSetting(String key) {
        // SettingsSingleton.d().e(key) resolves the `int` field of the current
        // settings object with the given name and throws on unknown keys.
        return SettingsSingleton.d().e(key);
    }

    // Font id used by the stock app for titles.
    private static final int FONT_PRODUCT_SANS = 11;

    private static final String SETTING_LIST = "list_font_title";
    private static final String SETTING_COMPACT = "compact_font_title";
    private static final String SETTING_SMALLER_CARD = "smaller_card_font_title";
    private static final String SETTING_SMALL_CARD = "small_card_font_title";
    private static final String SETTING_CARD = "card_font_title";
    private static final String SETTING_SLIDE = "slide_font_title";
}
