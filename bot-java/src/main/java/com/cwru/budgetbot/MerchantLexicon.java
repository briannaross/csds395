package com.cwru.budgetbot;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class MerchantLexicon {

    // ==================== Entry ====================

    public static final class Entry {
        private final String canonicalName;
        private final List<String> normalizedAliases;
        private final boolean onCampus;
        private final boolean diningHall;

        public Entry(String canonicalName,
                     Collection<String> aliases,
                     boolean onCampus,
                     boolean diningHall) {
            this.canonicalName = canonicalName;
            this.normalizedAliases = aliases.stream()
                    .map(Normalizer::normalize)
                    .collect(Collectors.toList());
            this.onCampus = onCampus;
            this.diningHall = diningHall;
        }

        /** Canonical merchant name (what we display / reason over). */
        public String canonicalName() {
            return canonicalName;
        }

        public boolean onCampus() {
            return onCampus;
        }

        public boolean isDiningHall() {
            return diningHall;
        }

        List<String> aliases() {
            return normalizedAliases;
        }
    }

    // ==================== Match ====================

    public static final class Match {
        public final Entry entry;

        public Match(Entry entry) {
            this.entry = entry;
        }

        /** Convenience for IntentParser: match.canonicalName() */
        public String canonicalName() {
            return entry.canonicalName();
        }

        public boolean onCampus() {
            return entry.onCampus();
        }

        public boolean isDiningHall() {
            return entry.isDiningHall();
        }
    }

    // ==================== Lexicon data ====================

    private final List<Entry> entries = new ArrayList<>();

    public MerchantLexicon() {
        // Dining halls
        add("Dining Hall", List.of("dining hall", "the dining hall", "dining"),
                true, true);
        add("North Dining Hall", List.of("north dining", "ndh", "north dh"),
                true, true);
        add("South Dining Hall", List.of("south dining", "sdh", "south dh"),
                true, true);

        // On-campus / Uptown food partners & common spots
        add("Mitchell's Ice Cream", List.of("mitchells", "mitchell's", "mitchells ice cream"),
                true, false);
        add("Panera Bread", List.of("panera", "panera bread"),
                true, false);
        add("Starbucks", List.of("starbucks", "sbux"),
                true, false);
        add("Dunkin", List.of("dunkin", "dunkin donuts", "dd"),
                true, false);
        add("Rascal House Pizza", List.of("rascal house", "rascal house pizza"),
                true, false);
        add("Potbelly", List.of("potbelly", "potbelly sandwiches", "potbelly sandwich"),
                true, false);
        add("Sittoo's Pita & Salads", List.of("sittoos", "sittoo's", "sittoos pita", "sittoo's pita"),
                true, false);
        add("Cilantro Taqueria", List.of("cilantro", "cilantro taqueria"),
                true, false);
        add("Bibibop", List.of("bibibop", "bibibap", "bibimbap place"),
                true, false);
        add("Kenko Sushi", List.of("kenko", "kenko sushi"),
                true, false);
        add("Sunset Kitchen", List.of("sunset kitchen"),
                true, false);
        add("Phusion Cafe", List.of("phusion", "phusion cafe"),
                true, false);
        add("Beyond Juicery & Eatery", List.of("beyond juicery", "beyond juice"),
                true, false);
        add("Falafel Cafe", List.of("falafel cafe"),
                true, false);
        add("Indian Flame", List.of("indian flame", "indian flame restaurant"),
                true, false);
        add("Buffalo Wild Wings", List.of("bww", "buffalo wild wings", "buffalo wings"),
                false, false);
        add("The Jolly Scholar", List.of("jolly scholar", "the jolly scholar"),
                true, false);

        // Markets and convenience
        add("Spartie Mart", List.of("spartie mart", "spartiemart", "spartimart"),
                true, false);
        add("Fairfax Market", List.of("fairfax market", "fairfax"),
                false, false);
        add("Dave's Market", List.of("daves", "dave's market", "daves market"),
                false, false);
        add("Aldi", List.of("aldi", "aldi's"),
                false, false);
        add("Grocery Outlet", List.of("grocery outlet"),
                false, false);
        add("Trader Joe's", List.of("trader joes", "trader joe's", "tj's", "tjs"),
                false, false);
        add("Giant Eagle", List.of("giant eagle"),
                false, false);
        add("Target", List.of("target"),
                false, false);
        add("Whole Foods", List.of("whole foods", "wholefoods"),
                false, false);

        // Generic chains
        add("Chipotle", List.of("chipotle", "chipotle mexican grill"),
                false, false);
        add("Subway", List.of("subway"),
                false, false);
        // Panera already added as on-campus; keep one entry only if you prefer
    }

    private void add(String canonicalName,
                     Collection<String> rawAliases,
                     boolean onCampus,
                     boolean diningHall) {
        List<String> aliases = new ArrayList<>(rawAliases);
        aliases.add(canonicalName);
        entries.add(new Entry(canonicalName, aliases, onCampus, diningHall));
    }

    public Optional<Match> find(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        String normalized = Normalizer.normalize(text);

        Entry best = null;
        for (Entry e : entries) {
            for (String alias : e.aliases()) {
                if (normalized.contains(alias)) {
                    best = e;
                    break;
                }
            }
        }
        return (best == null) ? Optional.empty() : Optional.of(new Match(best));
    }
}
