package com.cwru.budgetbot;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class IntentParser {
    private final MerchantLexicon lexicon = new MerchantLexicon();
    private final MoneyParser money = new MoneyParser();
    private final SwipeParser swipe = new SwipeParser();

    private static final Pattern CAN_I_BUY =
            Pattern.compile("(?:\\bcan|\\bshould).*(?:buy|get|spend)|\\bis it ok(?:ay)? if\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern HOW_AM_I_DOING =
            Pattern.compile("\\bhow\\b.*\\b(budget|spend|doing|pace|pacing)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPLAIN_RECS =
            Pattern.compile("\\bexplain\\b|\\bwhy\\b.*\\brecommend", Pattern.CASE_INSENSITIVE);

    public PurchaseQuery parse(String input) {
        if (input == null || input.isBlank()) {
            return new PurchaseQuery(IntentType.UNKNOWN, "Unknown", null, SourceType.UNKNOWN, "");
        }
        String text = input.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        // 1) intent
        IntentType intent = detectIntent(lower);

        // 2) merchant (canonical) via alias matching with normalization
        Optional<MerchantLexicon.Match> match = lexicon.find(text);
        String merchant = match.map(m -> m.entry.canonicalName()).orElse("Unknown");

        // 3) amount
        Double amount = money.extractAmount(text);

        // 4) source inference (MEAL_SWIPE, CASE_CASH, PERSONAL, UNKNOWN)
        SourceType source = SourceType.UNKNOWN;

        // (a) Explicit swipe mention → MEAL_SWIPE
        boolean mentionsSwipe = swipe.mentionsSwipe(text);
        if (mentionsSwipe) {
            source = SourceType.MEAL_SWIPE;
        }

        // (b) Dining Hall always uses swipes
        if (source != SourceType.MEAL_SWIPE && match.map(m -> m.entry.isDiningHall()).orElse(false)) {
            source = SourceType.MEAL_SWIPE;
        }

        // (c) Explicit hints for money sources if not already MEAL_SWIPE
        if (source != SourceType.MEAL_SWIPE) {
            if (lower.contains("case cash") || lower.contains("casecash") || lower.contains("on campus")) {
                source = SourceType.CASE_CASH;
            } else if (lower.contains("personal") || lower.contains("credit") || lower.contains("debit") || lower.contains("my card")) {
                source = SourceType.PERSONAL;
            } else if (match.map(m -> m.entry.onCampus()).orElse(false)) {
                // sensible default for on-campus when nothing else is stated
                source = SourceType.CASE_CASH;
            }
        }

        // 5) upgrade UNKNOWN intent to CAN_I_BUY if it looks like a purchase
        if (intent == IntentType.UNKNOWN && (match.isPresent() || amount != null || mentionsSwipe)) {
            intent = IntentType.CAN_I_BUY;
        }

        return new PurchaseQuery(intent, merchant, amount, source, text);
    }

    private IntentType detectIntent(String lower) {
        if (CAN_I_BUY.matcher(lower).find()) return IntentType.CAN_I_BUY;
        if (HOW_AM_I_DOING.matcher(lower).find()) return IntentType.HOW_AM_I_DOING;
        if (EXPLAIN_RECS.matcher(lower).find()) return IntentType.EXPLAIN_RECS;
        return IntentType.UNKNOWN;
    }
}
