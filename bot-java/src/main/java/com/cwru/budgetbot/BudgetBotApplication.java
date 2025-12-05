package com.cwru.budgetbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BudgetBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetBotApplication.class, args);
    }

    public static class ResponseBundle {
        public final String yes;
        public final String caution;
        public final String no;
        public ResponseBundle(String yes, String caution, String no) {
            this.yes = yes; this.caution = caution; this.no = no;
        }
    }

    // Simple "buckets" for recommendation lists.
    // These names are chosen to match your MerchantLexicon entries.
    private static final String[] DINING_HALLS = {
            "Dining Hall", "North Dining Hall", "South Dining Hall"
    };
    private static final String[] CHEAP_GROCERIES = {
            "Aldi", "Walmart"
    };
    private static final String[] MID_GROCERIES = {
            "Trader Joe's", "Giant Eagle", "Target"
    };
    private static final String[] MID_MEALS = {
            "Chipotle", "Subway", "Panera Bread", "Campus Grill", "Student Center Food Court"
    };
    private static final String[] TREATS_AND_COFFEE = {
            "Mitchell's Ice Cream", "Starbucks", "Dunkin", "Einstein Bros Bagels"
    };

    public ResponseBundle draftResponses(PurchaseQuery q) {
        final String merchant = StringUtil.orUnknown(q.getMerchant(), "this place");
        final String amountPhrase = amountPhrase(q);
        final String sourcePhrase = sourcePhrase(q.getSource());
        final boolean isSwipe = q.getSource() == SourceType.MEAL_SWIPE
                || "Dining Hall".equalsIgnoreCase(q.getMerchant())
                || "North Dining Hall".equalsIgnoreCase(q.getMerchant())
                || "South Dining Hall".equalsIgnoreCase(q.getMerchant());

        switch (q.getIntent()) {
            case CAN_I_BUY: {
                String yes = isSwipe
                        ? "✅ YES: Using a meal swipe at " + merchant + " looks fine. " +
                        "Just keep an eye on your weekly swipe limits so you don't run out before the week resets."
                        : "✅ YES: " + capFirst(merchant) + " for " + amountPhrase + " looks okay. " +
                        "Paying with " + sourcePhrase + " should be fine if your overall weekly spending pace is reasonable.";

                String caution = isSwipe
                        ? "⚠️ CAUTION: A swipe at " + merchant + " might be okay, but it depends on how many swipes " +
                        "you've already used this week. If you're close to the cap, consider saving this swipe for later."
                        : "⚠️ CAUTION: Buying at " + merchant + " for " + amountPhrase +
                        " could push you over your pacing target. If you go ahead, try balancing it by skipping a small treat later.";

                String no = isSwipe
                        ? "⛔ NO: I'd skip a swipe at " + merchant + " right now. " +
                        "If you're running low on weekly or daily swipes, saving this one can help you avoid running out early."
                        : "⛔ NO: I'd pass on " + merchant + " for " + amountPhrase + " today. " +
                        "Given typical budgeting guidelines, this risks eating into your buffer. Consider a cheaper option or wait a bit.";

                return new ResponseBundle(yes, caution, no);
            }

            case HOW_AM_I_DOING: {
                return new ResponseBundle(
                        "✅ If your weekly spending is below your target and you still have a buffer, you're likely on pace. " +
                                "You can probably afford a small treat without throwing things off.",
                        "⚠️ You might be close to your target. It's worth reviewing this week's non-essential purchases and trimming one or two.",
                        "⛔ You're probably over your safe pace. Pausing extras for a few days can help you reset before the next week starts."
                );
            }

            case RECS: {
                // YES: user is doing well → more freedom: mix of treats, mid meals, and decent groceries
                String yes = "✅ If your budget is in good shape, here are some options:\n" +
                        " • Treats & coffee: " + joinList(TREATS_AND_COFFEE) + "\n" +
                        " • Regular meals out: " + joinList(MID_MEALS) + "\n" +
                        " • Groceries to stock up: " + joinList(MID_GROCERIES) + "\n" +
                        "You can mix a small number of treats with normal meals and a grocery run to stay on track.";

                // CAUTION: user is borderline → mostly mid/cheap: dining hall, mid meals, cheaper groceries
                String caution = "⚠️ If your budget is a bit tight, focus on lower-cost choices:\n" +
                        " • Most meals from: " + joinList(DINING_HALLS) + "\n" +
                        " • Occasional meals out: " + joinList(MID_MEALS) + "\n" +
                        " • Budget-conscious groceries: " + joinList(CHEAP_GROCERIES) + " or " + joinList(MID_GROCERIES) + "\n" +
                        "Try using dining halls or groceries for most meals and save restaurants for once in a while.";

                // NO: user doing poorly → very frugal: dining halls + cheapest groceries
                String no = "⛔ If your budget is in a rough spot, I'd stick to the cheapest options for now:\n" +
                        " • Primary meals: " + joinList(DINING_HALLS) + "\n" +
                        " • Groceries: " + joinList(CHEAP_GROCERIES) + "\n" +
                        "Focus on dining halls and basic groceries until your spending pace improves; then you can reintroduce treats and restaurants.";

                return new ResponseBundle(yes, caution, no);
            }

            default: {
                return new ResponseBundle(
                        "✅ If this is a purchase or food question, it may be fine depending on amount and source. Try asking more specifically with the place and cost.",
                        "⚠️ I can give you better advice if you share a merchant and approximate cost, or ask for recommendations directly.",
                        "⛔ I don't have enough details to judge this decision. Tell me where you're going and roughly how much you plan to spend."
                );
            }
        }
    }

    private String amountPhrase(PurchaseQuery q) {
        if (q.getSource() == SourceType.MEAL_SWIPE
                || "Dining Hall".equalsIgnoreCase(q.getMerchant())
                || "North Dining Hall".equalsIgnoreCase(q.getMerchant())
                || "South Dining Hall".equalsIgnoreCase(q.getMerchant())) {
            return "a swipe";
        }
        return StringUtil.money(q.getAmount());
    }

    private String sourcePhrase(SourceType s) {
        switch (s) {
            case CASE_CASH:  return "CaseCash";
            case PERSONAL:   return "your personal card";
            case MEAL_SWIPE: return "a meal swipe";
            default:         return "your usual payment";
        }
    }

    private String capFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String joinList(String[] items) {
        if (items == null || items.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            sb.append(items[i]);
            if (i < items.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
