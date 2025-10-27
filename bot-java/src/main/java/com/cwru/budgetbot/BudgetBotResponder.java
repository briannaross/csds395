package com.cwru.budgetbot;

public class BudgetBotResponder {

    public static class ResponseBundle {
        public final String yes;
        public final String caution;
        public final String no;
        public ResponseBundle(String yes, String caution, String no) {
            this.yes = yes; this.caution = caution; this.no = no;
        }
    }

    public ResponseBundle draftResponses(PurchaseQuery q) {
        final String intent = q.getIntent().name();
        final String merchant = StringUtil.orUnknown(q.getMerchant(), "this place");
        final String amountPhrase = amountPhrase(q);
        final String sourcePhrase = sourcePhrase(q.getSource());
        final boolean isSwipe = q.getSource() == SourceType.MEAL_SWIPE
                || "Dining Hall".equalsIgnoreCase(q.getMerchant());

        switch (q.getIntent()) {
            case CAN_I_BUY:
                // purchase-style replies
                String yes = isSwipe
                        ? "✅ YES: Using a meal swipe at " + merchant + " looks fine. " +
                          "Keep an eye on your weekly/daily swipe limits so you don’t run out before the weekend."
                        : "✅ YES: " + capFirst(merchant) + " for " + amountPhrase + " looks okay. " +
                          "Paying with " + sourcePhrase + " should keep you on track if your weekly spending pace is steady.";

                String caution = isSwipe
                        ? "⚠️ CAUTION: A swipe at " + merchant + " may be okay, but it depends on your " +
                          "remaining weekly and daily swipe limits. If you’re near the cap, consider saving this swipe for later in the week."
                        : "⚠️ CAUTION: Buying at " + merchant + " for " + amountPhrase + " could push you over your pacing target. " +
                          "If you go ahead, try to balance it by skipping a small purchase later (e.g., ~$4 coffee). Paying with " + sourcePhrase + ".";

                String no = isSwipe
                        ? "⛔ NO: I’d skip a swipe at " + merchant + " right now. " +
                          "If you’re low on weekly/daily swipe availability, you could run out before the week resets."
                        : "⛔ NO: I’d pass on " + merchant + " for " + amountPhrase + " today. " +
                          "Given typical pacing and buffer guidelines, this risks tightening your budget. Consider a cheaper alternative or wait a day.";

                return new ResponseBundle(yes, caution, no);

            case HOW_AM_I_DOING:
                return new ResponseBundle(
                        "✅ You’re likely on pace if your recent spending is below your weekly target. Keep a small buffer for the weekend.",
                        "⚠️ You might be close to your target—review this week’s discretionary items and trim one or two.",
                        "⛔ You’re probably over target right now. Pause new discretionary buys until next week to reset your pace."
                );

            case EXPLAIN_RECS:
                return new ResponseBundle(
                        "✅ Recommendation summary: maintain current habits that fit your weekly target and keep a small safety buffer.",
                        "⚠️ Recommendation caution: one or two categories (like treats or delivery) may need a small cut to stay on track.",
                        "⛔ Recommendation stop: current spending pattern likely exceeds your safe pace; pause extras temporarily."
                );

            default:
                // UNKNOWN
                return new ResponseBundle(
                        "✅ If this is a purchase question, it may be fine depending on amount and source. Ask me like: “can I buy Starbucks for $6 with CaseCash?”",
                        "⚠️ I can help if you share a merchant and amount. Example: “Dunkin for 4 bucks?”",
                        "⛔ I don’t have enough details. Tell me where and roughly how much."
                );
        }
    }

    private String amountPhrase(PurchaseQuery q) {
        if (q.getSource() == SourceType.MEAL_SWIPE || "Dining Hall".equalsIgnoreCase(q.getMerchant())) {
            return "a swipe"; // amount doesn’t matter for swipes
        }
        return StringUtil.money(q.getAmount());
    }

    private String sourcePhrase(SourceType s) {
        switch (s) {
            case CASE_CASH: return "CaseCash";
            case PERSONAL:  return "your personal card";
            case MEAL_SWIPE:return "a meal swipe";
            default:        return "your usual payment";
        }
    }

    private String capFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
