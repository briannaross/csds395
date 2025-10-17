package com.cwru.budgetbot;

import java.util.*;

/**
 * Canonical merchant dictionary with alias matching that is
 * case-insensitive and punctuation-tolerant via Normalizer.
 *
 * Flags (onCampus, acceptsMealSwipe, isDiningHall) are included
 * for future logic; for now we only use them to infer MEAL_SWIPE
 * and default to CASE_CASH for on-campus purchases.
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

        public String canonicalName() { return canonicalName; }
        public List<String> aliases() { return aliases; }
        public boolean onCampus() { return onCampus; }
        public boolean acceptsMealSwipe() { return acceptsMealSwipe; }
        public boolean isDiningHall() { return isDiningHall; }
    }

    public static class Match {
        public final MerchantEntry entry;
        public final String aliasMatched;
        public Match(MerchantEntry entry, String aliasMatched) {
            this.entry = entry; this.aliasMatched = aliasMatched;
        }
    }

    private final List<MerchantEntry> entries = new ArrayList<>();

    public MerchantLexicon() {
        // Dining hall — always swipes
        entries.add(new MerchantEntry(
                "Dining Hall",
                List.of("dining hall", "dining", "cafeteria", "dhall"),
                true, true, true
        ));

        // On-campus spots (sample — adjust as needed)
        entries.add(new MerchantEntry(
                "Mitchell's Ice Cream",
                List.of("mitchell", "mitchells", "mitchell's", "mitchels"),
                true, false, false
        ));
        entries.add(new MerchantEntry(
                "Starbucks",
                List.of("starbucks", "stbx"),
                true, false, false
        ));
        entries.add(new MerchantEntry(
                "Dunkin",
                List.of("dunkin", "dunkin donuts", "dd"),
                true, false, false
        ));

        // Off-campus examples
        entries.add(new MerchantEntry(
                "Chipotle",
                List.of("chipotle"),
                false, false, false
        ));
        entries.add(new MerchantEntry(
                "Campus Bookstore",
                List.of("bookstore", "campus bookstore"),
                true, false, false
        ));
        entries.add(new MerchantEntry(
                "Giant Eagle",
                List.of("giant eagle", "giant-eagle"),
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
