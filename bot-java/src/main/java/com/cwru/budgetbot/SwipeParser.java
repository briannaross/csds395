package com.cwru.budgetbot;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Detects if the text refers to swipes; optional count if stated (e.g., "2 swipes"). */
public class SwipeParser {
    private static final Pattern SWIPE_ANY = Pattern.compile(
            "\\b(meal\\s*)?swipes?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern SWIPE_COUNT = Pattern.compile(
            "\\b(\\d+)\\s*(meal\\s*)?swipes?\\b|x\\s*(\\d+)\\s*swipes?\\b", Pattern.CASE_INSENSITIVE);

    public boolean mentionsSwipe(String text) {
        return SWIPE_ANY.matcher(text).find();
    }

    public Optional<Integer> swipeCount(String text) {
        Matcher m = SWIPE_COUNT.matcher(text);
        if (m.find()) {
            String g = m.group(1) != null ? m.group(1) : m.group(3);
            try { return Optional.of(Integer.parseInt(g)); } catch (NumberFormatException ignore) {}
        }
        return Optional.empty();
    }
}

