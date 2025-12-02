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
                        ? "✅ YES: Using a meal swipe at " + merchant + " fits comfortably into your current usage this week."
                        : "✅ YES: " + capFirst(merchant) + " for " + amountPhrase + " fits within your current weekly budget pace.";

                String caution = isSwipe
                        ? "⚠️ CAUTION: A swipe at " + merchant + " might be okay, but you’re getting closer to your weekly swipe limit. Use it only if this meal matters more than a later one."
                        : "⚠️ CAUTION: " + amountPhrase + " at " + merchant +
                          " is doable, but it puts pressure on the rest of your week. If you go ahead, try to cut back slightly on other non-essentials.";

                String no = isSwipe
                        ? "⛔ NO: You’re too close to using up your swipes for the week. It’s safer to save this swipe for a more important meal."
                        : "⛔ NO: " + amountPhrase + " at " + merchant +
                          " would push you past a realistic weekly spending level. Choosing a cheaper option or waiting until next week is safer.";

                return new ResponseBundle(yes, caution, no);
            }

            case HOW_AM_I_DOING: {
                // More direct, no “if” hedging
                String yes = "✅ You’re on track with your budget this week. At your current pace, you can afford a small extra purchase without hurting your goals.";
                String caution = "⚠️ You’re getting close to the edge of your weekly budget. New non-essential spending is possible, but you should keep it small and selective.";
                String no = "⛔ You’ve effectively used up this week’s budget. It’s best to pause extra spending until the new week starts and let your budget reset.";

                return new ResponseBundle(yes, caution, no);
            }

            case RECS: {
                String yes = "✅ You’re in a comfortable spot this week. Here are good options:\n" +
                        " • Treats & coffee: " + joinList(TREATS_AND_COFFEE) + "\n" +
                        " • Regular meals out: " + joinList(MID_MEALS) + "\n" +
                        " • Groceries to stock up: " + joinList(MID_GROCERIES) + "\n" +
                        "Mixing a treat or two with normal meals and a grocery run keeps you on pace.";

                String caution = "⚠️ Your budget is tight but manageable. Safer choices right now are:\n" +
                        " • Most meals from: " + joinList(DINING_HALLS) + "\n" +
                        " • Occasional meals out: " + joinList(MID_MEALS) + "\n" +
                        " • Budget-friendly groceries: " + joinList(CHEAP_GROCERIES) + " or " + joinList(MID_GROCERIES) + "\n" +
                        "Lean on dining halls and groceries for most meals and treat restaurant trips as occasional.";

                String no = "⛔ Your budget is under real pressure this week. Best options are:\n" +
                        " • Primary meals: " + joinList(DINING_HALLS) + "\n" +
                        " • Groceries: " + joinList(CHEAP_GROCERIES) + "\n" +
                        "Stick to dining halls and basic groceries until your weekly spending resets, then reintroduce extras.";

                return new ResponseBundle(yes, caution, no);
            }

            default: {
                String yes = "✅ This looks manageable based on a typical student budget. For more precise advice, mention where you’re going and roughly how much you’ll spend.";
                String caution = "⚠️ I can give a clearer answer if you share the place and approximate cost, or ask how you’re doing on your budget this week.";
                String no = "⛔ I don’t have enough details to judge this. Tell me the location, an approximate price, or ask for recommendations based on your budget.";

                return new ResponseBundle(yes, caution, no);
            }
        }
    }

    /** Pick one of the three strings based on Decision. */
    public String pickResponse(PurchaseQuery q, Decision decision) {
        ResponseBundle bundle = draftResponses(q);
        switch (decision) {
            case YES:      return bundle.yes;
            case CAUTION:  return bundle.caution;
            case NO:       return bundle.no;
            default:       return bundle.caution;
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
}
