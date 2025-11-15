package com.cwru.budgetbot;

import java.util.*;

/**
 * Canonical merchant dictionary with alias matching that is
 * case-insensitive and punctuation-tolerant via Normalizer.
 *
 * Flags:
 *  - onCampus:       is this a campus location?
 *  - acceptsMealSwipe: can this location take meal swipes (often mobile)?
 *  - isDiningHall:   true for main dining halls that always consume swipes
 *
 * NOTE: The concrete merchants here are examples / placeholders.
 * You should customize this list to match actual campus + nearby options.
 */
public class MerchantLexicon {

    public static class MerchantEntry {
        private final String canonicalName;
        private final List<String> aliases;
        private final boolean onCampus;
        private final boolean acceptsMealSwipe;
        private final boolean isDiningHall;

        public MerchantEntry(String canonicalName, List<String> aliases,
                             boolean onCampus, boolean acceptsMealSwipe, boolean isDiningHall) {
            this.canonicalName = canonicalName;
            this.aliases = aliases;
            this.onCampus = onCampus;
            this.acceptsMealSwipe = acceptsMealSwipe;
            this.isDiningHall = isDiningHall;
        }

        public String canonicalName()     { return canonicalName; }
        public List<String> aliases()     { return aliases; }
        public boolean onCampus()         { return onCampus; }
        public boolean acceptsMealSwipe() { return acceptsMealSwipe; }
        public boolean isDiningHall()     { return isDiningHall; }
    }

    public static class Match {
        public final MerchantEntry entry;
        public final String aliasMatched;
        public Match(MerchantEntry entry, String aliasMatched) {
            this.entry = entry;
            this.aliasMatched = aliasMatched;
        }
    }

    private final List<MerchantEntry> entries = new ArrayList<>();

    public MerchantLexicon() {
        //
        // --- Dining halls (always swipes) ---
        //
        entries.add(new MerchantEntry(
                "Dining Hall",
                List.of("dining hall", "dining", "cafeteria", "dhall"),
                true, true, true
        ));

        // If your campus has multiple named dining halls, you can add them here:
        entries.add(new MerchantEntry(
                "North Dining Hall",
                List.of("north dining", "north dhall", "north cafeteria"),
                true, true, true
        ));

        entries.add(new MerchantEntry(
                "South Dining Hall",
                List.of("south dining", "south dhall", "south cafeteria"),
                true, true, true
        ));

        //
        // --- On-campus coffee / quick service ---
        //
        entries.add(new MerchantEntry(
                "Mitchell's Ice Cream",
                List.of("mitchell", "mitchells", "mitchell's", "mitchels"),
                true, false, false
        ));

        entries.add(new MerchantEntry(
                "Starbucks",
                List.of("starbucks", "stbx", "sbux"),
                true, false, false
        ));

        entries.add(new MerchantEntry(
                "Dunkin",
                List.of("dunkin", "dunkin donuts", "dd"),
                true, false, false
        ));

        entries.add(new MerchantEntry(
                "Einstein Bros Bagels",
                List.of("einstein", "einsteins", "einstein bros", "einsteins bagels"),
                true, false, false
        ));

        //
        // --- On-campus fast casual / restaurants (tweak swipes flag later) ---
        //
        entries.add(new MerchantEntry(
                "Campus Grill",
                List.of("campus grill", "grill"),
                true, false, false
        ));

        entries.add(new MerchantEntry(
                "Student Center Food Court",
                List.of("tink", "food court", "student center food"),
                true, false, false
        ));

        //
        // --- On-campus bookstore / retail ---
        //
        entries.add(new MerchantEntry(
                "Campus Bookstore",
                List.of("bookstore", "campus bookstore"),
                true, false, false
        ));

        //
        // --- Off-campus restaurants (fast casual) ---
        //
        entries.add(new MerchantEntry(
                "Chipotle",
                List.of("chipotle"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Panera Bread",
                List.of("panera", "panera bread"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Subway",
                List.of("subway"),
                false, false, false
        ));

        //
        // --- Groceries / pharmacies ---
        //
        entries.add(new MerchantEntry(
                "Giant Eagle",
                List.of("giant eagle", "giant-eagle"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Trader Joe's",
                List.of("trader joes", "trader joe's", "tj", "tjs"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Aldi",
                List.of("aldi"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Target",
                List.of("target", "super target"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Walmart",
                List.of("walmart", "wal-mart", "wally world"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "CVS",
                List.of("cvs"),
                false, false, false
        ));

        entries.add(new MerchantEntry(
                "Walgreens",
                List.of("walgreens", "wal greens"),
                false, false, false
        ));

        //
        // --- Online / misc ---
        //
        entries.add(new MerchantEntry(
                "Amazon",
                List.of("amazon", "amzn"),
                false, false, false
        ));
    }

    /** Return best alias match (longest alias wins) using normalized token containment. */
    public Optional<Match> find(String rawText) {
        String norm = Normalizer.normalize(rawText);
        Match best = null;
        int bestLen = -1;

        for (MerchantEntry e : entries) {
            for (String alias : e.aliases()) {
                String a = Normalizer.normalize(alias);
                if (containsToken(norm, a)) {
                    if (a.length() > bestLen) {
                        best = new Match(e, alias);
                        bestLen = a.length();
                    }
                }
            }
        }
        return Optional.ofNullable(best);
    }

    /** word-boundary-like containment on normalized strings; allows “giant eagle” vs “gianteagle” */
    private boolean containsToken(String hay, String needle) {
        String h = " " + hay + " ";
        String n = " " + needle + " ";
        if (h.contains(n)) return true;
        if (needle.contains(" ")) {
            String compactNeedle = needle.replace(" ", "");
            return hay.replace(" ", "").contains(compactNeedle);
        }
        return false;
    }
}

