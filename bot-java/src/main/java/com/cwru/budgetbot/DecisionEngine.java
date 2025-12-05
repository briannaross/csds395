package com.cwru.budgetbot;

import org.springframework.stereotype.Service;

/**
 * Decision engine that takes a parsed PurchaseQuery and a BudgetSnapshot
 * and returns a high-level decision that the responder can turn into text.
 *
 * It considers:
 *  - personal weekly budget
 *  - CaseCash pacing (approx per-week budget)
 *  - meal swipes remaining this week
 */
@Service
public class DecisionEngine {

    public Decision decide(PurchaseQuery q, BudgetSnapshot snap) {
        if (q == null || snap == null) {
            return Decision.CAUTION;
        }

        switch (q.getIntent()) {
            case HOW_AM_I_DOING:
                return decideHowAmIDoing(snap);

            case CAN_I_BUY:
                return decideCanIBuy(q, snap);

            case RECS:
                return decideRecs(snap);

            default:
                return Decision.CAUTION;
        }
    }

    /**
     * Overall “how am I doing”:
     *  - primarily based on personal weekly budget
     *  - adjusted if CaseCash or swipes are clearly overused
     */
    private Decision decideHowAmIDoing(BudgetSnapshot snap) {
        double personalBudget = safePositive(snap.getWeeklyBudgetPersonal());
        double personalSpent  = safeNonNegative(snap.getSpentThisWeekPersonal());

        double personalRatio;
        if (personalBudget <= 0) {
            personalRatio = 1.0; // treat as neutral/borderline
        } else {
            personalRatio = personalSpent / personalBudget;
        }

        // CaseCash “pacing”
        double ccWeeklyBudget = safePositive(snap.getCaseCashWeeklyBudgetApprox());
        double ccSpentWeek    = safeNonNegative(snap.getCaseCashSpentThisWeek());
        double ccRatio = (ccWeeklyBudget > 0) ? ccSpentWeek / ccWeeklyBudget : 1.0;

        // Meal swipe usage
        int swipesTotal = snap.getMealSwipesWeeklyTotal();
        int swipesUsed  = snap.getMealSwipesUsedThisWeek();
        double swipeRatio = (swipesTotal > 0) ? (double) swipesUsed / swipesTotal : 0.5;

        // Combine into a rough “worst-case” ratio
        double combined = Math.max(personalRatio, Math.max(ccRatio, swipeRatio));

        if (combined < 0.75) {
            return Decision.YES;       // comfortably on track
        } else if (combined <= 1.05) {
            return Decision.CAUTION;   // borderline
        } else {
            return Decision.NO;        // clearly overpacing
        }
    }

    /**
     * Question like “can I buy X for $Y?”.
     * We branch on source type:
     *  - PERSONAL uses personal weekly budget
     *  - CASE_CASH uses approximate weekly CaseCash budget
     *  - MEAL_SWIPE uses remaining swipes this week
     */
    private Decision decideCanIBuy(PurchaseQuery q, BudgetSnapshot snap) {
        Double amtObj = q.getAmount();
        double amount = (amtObj == null || amtObj <= 0) ? 0.0 : amtObj;

        switch (q.getSource()) {
            case CASE_CASH:
                return decideCaseCashPurchase(amount, snap);

            case MEAL_SWIPE:
                return decideSwipePurchase(snap);

            case PERSONAL:
            default:
                return decidePersonalPurchase(amount, snap);
        }
    }

    private Decision decidePersonalPurchase(double amount, BudgetSnapshot snap) {
        if (amount <= 0.0) return Decision.CAUTION;

        double weeklyBudget   = safePositive(snap.getWeeklyBudgetPersonal());
        double spent          = safeNonNegative(snap.getSpentThisWeekPersonal());
        double remainingBefore = weeklyBudget - spent;

        if (weeklyBudget <= 0) return Decision.CAUTION;

        double remainingAfter  = remainingBefore - amount;
        double minComfortBuffer = weeklyBudget * 0.20;

        if (remainingAfter >= minComfortBuffer) {
            return Decision.YES;
        } else if (remainingAfter >= 0) {
            return Decision.CAUTION;
        } else {
            return Decision.NO;
        }
    }

    private Decision decideCaseCashPurchase(double amount, BudgetSnapshot snap) {
        if (amount <= 0.0) return Decision.CAUTION;

        double ccWeeklyBudget = safePositive(snap.getCaseCashWeeklyBudgetApprox());
        double ccSpentWeek    = safeNonNegative(snap.getCaseCashSpentThisWeek());
        double remainingBefore = ccWeeklyBudget - ccSpentWeek;

        if (ccWeeklyBudget <= 0) return Decision.CAUTION;

        double remainingAfter   = remainingBefore - amount;
        double minComfortBuffer = ccWeeklyBudget * 0.20;

        if (remainingAfter >= minComfortBuffer) {
            return Decision.YES;
        } else if (remainingAfter >= 0) {
            return Decision.CAUTION;
        } else {
            return Decision.NO;
        }
    }

    private Decision decideSwipePurchase(BudgetSnapshot snap) {
        int total = snap.getMealSwipesWeeklyTotal();
        int used  = snap.getMealSwipesUsedThisWeek();

        if (total <= 0) return Decision.CAUTION;

        int remaining = total - used;

        if (remaining > total * 0.5) {
            return Decision.YES;       // more than half of swipes left
        } else if (remaining > 0) {
            return Decision.CAUTION;   // low but not zero
        } else {
            return Decision.NO;        // no swipes left on paper
        }
    }

    /**
     * Recommendations: if combined pacing is good, suggest more freedom.
     * If pacing is tight or bad, favor cheaper / on-campus options.
     */
    private Decision decideRecs(BudgetSnapshot snap) {
        // Use same combined heuristic as HOW_AM_I_DOING
        double personalBudget = safePositive(snap.getWeeklyBudgetPersonal());
        double personalSpent  = safeNonNegative(snap.getSpentThisWeekPersonal());
        double personalRatio  = (personalBudget > 0) ? personalSpent / personalBudget : 1.0;

        double ccWeeklyBudget = safePositive(snap.getCaseCashWeeklyBudgetApprox());
        double ccSpentWeek    = safeNonNegative(snap.getCaseCashSpentThisWeek());
        double ccRatio        = (ccWeeklyBudget > 0) ? ccSpentWeek / ccWeeklyBudget : 1.0;

        int swipesTotal = snap.getMealSwipesWeeklyTotal();
        int swipesUsed  = snap.getMealSwipesUsedThisWeek();
        double swipeRatio = (swipesTotal > 0) ? (double) swipesUsed / swipesTotal : 0.5;

        double combined = Math.max(personalRatio, Math.max(ccRatio, swipeRatio));

        if (combined < 0.7) {
            return Decision.YES;
        } else if (combined <= 1.0) {
            return Decision.CAUTION;
        } else {
            return Decision.NO;
        }
    }

    private double safePositive(double v) {
        return Double.isFinite(v) && v > 0 ? v : 0.0;
    }

    private double safeNonNegative(double v) {
        return Double.isFinite(v) && v >= 0 ? v : 0.0;
    }
}
