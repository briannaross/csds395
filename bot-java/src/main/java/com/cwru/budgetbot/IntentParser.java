package com.cwru.budgetbot;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class IntentParser {
    private final MerchantLexicon lexicon = new MerchantLexicon();
    private final MoneyParser money = new MoneyParser();
    private final SwipeParser swipe = new SwipeParser();

    // "can I buy", "should I get", "is it okay if I spend"
    private static final Pattern CAN_I_BUY =
            Pattern.compile("(?:\\bcan|\\bshould).*(?:buy|get|spend)|\\bis it ok(?:ay)? if\\b",
                    Pattern.CASE_INSENSITIVE);

    // "how am I doing on my budget?"
    private static final Pattern HOW_AM_I_DOING =
            Pattern.compile("\\bhow\\b.*\\b(budget|spend|doing|pace|pacing)\\b",
                    Pattern.CASE_INSENSITIVE);

    // RECS: “where should I eat?”, “any food recommendations?”, “where can I get groceries?”
    private static final Pattern RECS =
            Pattern.compile(
                    "(where should i (eat|go))"
                            + "|(any (food|lunch|dinner|grocery|groceries) recommendations?)"
                            + "|(what (do you )?recommend)"
                            + "|(recommend (someplace|somewhere|a place))"
                            + "|(suggest (a place|somewhere))"
                            + "|(where can i (get|buy) (food|groceries|grocer?y))",
                    Pattern.CASE_INSENSITIVE
            );

    public PurchaseQuery parse(String input) {
        if (input == null || input.isBlank()) {
            return new PurchaseQuery(
                    IntentType.UNKNOWN,
                    "Unknown",
                    null,
                    SourceType.UNKNOWN,
                    "",
                    false
            );
        }

        String text = input.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        // 1) intent
        IntentType intent = detectIntent(lower);

        // 2) merchant (canonical) via alias matching with normalization
        Optional<MerchantLexicon.Match> match = lexicon.find(text);
        String merchant = match.map(m -> m.entry.canonicalName()).orElse("Unknown");
        boolean isDiningHall = match.map(m -> m.entry.isDiningHall()).orElse(false);

        // 3) amount
        Double amount = money.extractAmount(text);
        boolean hasAmount = amount != null && amount > 0;

        // 4) source inference
        SourceType source = SourceType.UNKNOWN;

        boolean mentionsSwipe = swipe.mentionsSwipe(text);

        // Dining hall logic: if they explicitly say "swipe" → MEAL_SWIPE
        // If they give a dollar amount → treat as money (CaseCash by default)
        // Else → default to MEAL_SWIPE
        if (isDiningHall) {
            if (mentionsSwipe && !hasAmount) {
                source = SourceType.MEAL_SWIPE;
            } else if (hasAmount) {
                source = SourceType.CASE_CASH;   // dining hall but paying with money
            } else {
                source = SourceType.MEAL_SWIPE;  // generic "dining hall" question
            }
        } else if (mentionsSwipe) {
            source = SourceType.MEAL_SWIPE;
        }

        // If we still haven't decided and it's clearly on-campus, prefer CaseCash
        if (source != SourceType.MEAL_SWIPE) {
            if (lower.contains("case cash") || lower.contains("casecash")) {
                source = SourceType.CASE_CASH;
            } else if (match.map(m -> m.entry.onCampus()).orElse(false)) {
                // e.g., Starbucks on campus → default CaseCash
                source = SourceType.CASE_CASH;
            } else if (lower.contains("personal") || lower.contains("credit")
                    || lower.contains("debit") || lower.contains("my card")) {
                source = SourceType.PERSONAL;
            }
        }

        // 5) cheap preference flag (for cheap recs)
        boolean cheapPreference = detectCheapPreference(lower);

        // 6) upgrade UNKNOWN intent to CAN_I_BUY if it still looks like a purchase
        if (intent == IntentType.UNKNOWN && (match.isPresent() || hasAmount || mentionsSwipe)) {
            intent = IntentType.CAN_I_BUY;
        }

        return new PurchaseQuery(intent, merchant, amount, source, text, cheapPreference);
    }

    private IntentType detectIntent(String lower) {
        // RECS first so "where can I get groceries?" isn't treated as CAN_I_BUY
        if (RECS.matcher(lower).find()) return IntentType.RECS;
        if (HOW_AM_I_DOING.matcher(lower).find()) return IntentType.HOW_AM_I_DOING;
        if (CAN_I_BUY.matcher(lower).find()) return IntentType.CAN_I_BUY;

        // heuristic fallback: generic food questions
        if (looksLikeFoodRecs(lower)) return IntentType.RECS;

        return IntentType.UNKNOWN;
    }

    private boolean looksLikeFoodRecs(String lower) {
        boolean hasFoodWord =
                lower.contains("food")
                        || lower.contains("lunch")
                        || lower.contains("dinner")
                        || lower.contains("groceries")
                        || lower.contains("grocery")
                        || lower.contains("snack")
                        || lower.contains("something to eat");

        boolean hasLocationish =
                lower.contains("somewhere")
                        || lower.contains("some place")
                        || lower.contains("someplace")
                        || lower.contains("place to eat")
                        || lower.contains("place to get");

        boolean hasBudgetWord =
                lower.contains("cheap")
                        || lower.contains("affordable")
                        || lower.contains("on a budget")
                        || lower.contains("not too expensive")
                        || lower.contains("broke");

        return hasFoodWord && (hasLocationish || hasBudgetWord);
    }

    /** Detects when the user is explicitly asking for cheap/affordable options. */
    private boolean detectCheapPreference(String lower) {
        return lower.contains("cheap")
                || lower.contains("affordable")
                || lower.contains("on a budget")
                || lower.contains("broke")
                || lower.contains("not too expensive")
                || lower.contains("low cost")
                || lower.contains("budget friendly")
                || lower.contains("inexpensive");
    }
}
