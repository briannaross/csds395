package com.cwru.budgetbot;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyParser {
    // $6.75, 6.75, $7, 7, "4 bucks", "12 dollars", "about 5"
    private static final Pattern AMOUNT = Pattern.compile(
            "(?:about|around|approx(?:\\.?)|~)?\\s*(?:\\$\\s*)?([0-9]+(?:\\.[0-9]{1,2})?)\\b|\\b([0-9]+)\\s*(?:bucks|dollars?)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public Double extractAmount(String text) {
        Matcher m = AMOUNT.matcher(text);
        if (m.find()) {
            String a = m.group(1) != null ? m.group(1) : m.group(2);
            try { return Double.parseDouble(a); } catch (NumberFormatException ignore) {}
        }
        // soft fallbacks by phrase (tune as needed)
        String lower = text.toLowerCase(Locale.ROOT);
        if (lower.contains("a coffee")) return 4.00;
        if (lower.contains("ice cream")) return 6.75;
        return null;
    }
}
