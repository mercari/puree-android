package com.mercari.puree;

import java.util.regex.Pattern;

import javax.annotation.Nullable;

public class TagPattern {

    private static final String SEPARATOR = "\\.";
    private static final String ALL_WILD_CARD = "**";
    private static final String WILD_CARD = "*";
    private static final Pattern VALID_PATTERN_ALPHANUMERIC = Pattern.compile("^[A-Za-z0-9]+");

    private String pattern;

    public TagPattern() {
        this.pattern = ALL_WILD_CARD;
    }

    private TagPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Patterns should follow the following pattern:
     * • No consecutive periods
     * • Accepts only alphanumeric, '*', '**', and '.'
     * @param pattern to be matched
     * @return whether pattern is valid
     */
    private static boolean isValidPattern(String pattern) {
        String[] patternElements = pattern.split(SEPARATOR);
        for (String patternPath : patternElements) {
            if (!VALID_PATTERN_ALPHANUMERIC.matcher(patternPath).matches() &&
                    !patternPath.equals(ALL_WILD_CARD) &&
                    !patternPath.equals(WILD_CARD)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Matches tag based on the pattern specified in TagPattern
     *
     * @param tag
     * @return whether tag matches pattern scheme
     */
    boolean match(String tag) {
        if (tag.equals(pattern)) {
            return true;
        }
        String[] patternElements = pattern.split(SEPARATOR);
        String[] tagElements = tag.split(SEPARATOR);
        int patternIndex = 0;
        int tagIndex = 0;
        boolean isAllWildCard = false;
        while (tagIndex < tagElements.length && patternIndex < patternElements.length) {
            if (patternElements[patternIndex].equals(ALL_WILD_CARD)) {
                isAllWildCard = true;
            } else if (!patternElements[patternIndex].equals(WILD_CARD)) {
                if (!patternElements[patternIndex].equals(tagElements[tagIndex])) {
                    if (isAllWildCard) {
                        tagIndex++;
                        continue;
                    }
                    return false;
                } else {
                    isAllWildCard = false;
                }
            }
            tagIndex++;
            patternIndex++;
        }
        // AllWildCard at the end
        if (isAllWildCard && patternIndex == patternElements.length) {
            return true;
        }
        if (tagIndex == tagElements.length) {
            return patternIndex == patternElements.length ||
                    patternElements[patternElements.length - 1].equals(ALL_WILD_CARD);
        }
        return false;
    }

    /**
     * @return A TagPattern that fits the expected structure, null otherwise
     */
    @Nullable
    static TagPattern fromString(String pattern) {
        if (!isValidPattern(pattern)) {
            return null;
        }
        return new TagPattern(pattern);
    }
}
