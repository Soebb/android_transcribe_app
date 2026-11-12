package dev.notune.transcribe;

import java.util.Locale;

/**
 * Resolves subtitle source language for the English/Persian-only translator.
 * A fixed supported model language takes precedence, followed by ML Kit
 * language identification and Persian-script detection. Unsupported or
 * uncertain languages are left untranslated.
 */
public final class SourceLanguageResolver {
    private SourceLanguageResolver() {}

    public static final String UNDETERMINED = "und";

    /** Resolves a supported source code from the selected model language. */
    public static String resolve(String text, String modelLanguageTag) {
        String fromModel = primaryCode(modelLanguageTag);
        if (fromModel != null) return fromModel;
        return detectFromText(text);
    }

    /** Returns only English or Persian model-language codes. */
    public static String primaryCode(String tag) {
        return supportedCode(SubtitleTranslationTargets.primaryTag(tag));
    }

    /** Returns only English or Persian from an ML Kit language-ID result. */
    public static String identifiedCode(String identifiedTag) {
        if (identifiedTag == null) return null;
        String tag = identifiedTag.trim();
        if (tag.isEmpty() || tag.equalsIgnoreCase(UNDETERMINED)
                || tag.toLowerCase(Locale.ROOT).endsWith("-latn")) return null;
        return supportedCode(SubtitleTranslationTargets.primaryTag(tag));
    }

    /**
     * Prefers ML Kit identification, then Persian-script detection, then the
     * last confidently detected supported session language.
     */
    public static String pickSource(String identifiedTag, String text, String sessionLanguage) {
        String identified = identifiedCode(identifiedTag);
        if (identified != null) return identified;
        String heuristic = detectFromText(text);
        if (heuristic != null) return heuristic;
        return supportedCode(sessionLanguage);
    }

    private static String supportedCode(String code) {
        if ("en".equals(code) || "fa".equals(code)) return code;
        return null;
    }

    /**
     * Detects Persian from its Arabic-derived script. English is identified
     * through ML Kit; plain Latin text alone is not enough to distinguish it
     * reliably from other languages.
     */
    public static String detectFromText(String text) {
        if (text == null || text.isEmpty()) return null;
        for (int i = 0; i < text.length();) {
            int cp = text.codePointAt(i);
            i += Character.charCount(cp);
            if ((cp >= 0x0600 && cp <= 0x06FF)
                    || (cp >= 0x0750 && cp <= 0x077F)
                    || (cp >= 0x08A0 && cp <= 0x08FF)) {
                return "fa";
            }
        }
        return null;
    }
}
