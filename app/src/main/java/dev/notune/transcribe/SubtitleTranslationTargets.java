package dev.notune.transcribe;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Supported live-subtitle translation targets. */
public final class SubtitleTranslationTargets {
    /** Marker value meaning no translation; keep the original speech. */
    public static final String AUTO = "auto";

    /** Only English and Persian are offered as translation targets. */
    public static final List<String> TAGS = Arrays.asList(
            AUTO, "en-US", "fa-IR");

    private SubtitleTranslationTargets() {}

    /** Whether a stored tag is one of the supported values (auto included). */
    public static boolean isValid(String tag) {
        return tag != null && (tag.equals(AUTO) || TAGS.contains(tag));
    }

    /** Returns the ML Kit language code for English or Persian, or null. */
    public static String mlKitCode(String tag) {
        String primary = primaryTag(tag);
        if ("en".equals(primary) || "fa".equals(primary)) return primary;
        return null;
    }

    /** Primary subtag of a BCP-47 tag, lowercased; null for auto/blank. */
    public static String primaryTag(String tag) {
        if (tag == null) return null;
        String t = tag.trim();
        if (t.isEmpty() || t.equalsIgnoreCase(AUTO)) return null;
        int dash = t.indexOf('-');
        return (dash < 0 ? t : t.substring(0, dash)).toLowerCase(Locale.ROOT);
    }
}
