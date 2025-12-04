package com.cwru.budgetbot;

public class DecisionEngine {

    private static final int SEMESTER_WEEKS = 15;

    public Decision decide(PurchaseQuery q, BudgetSnapshot snapshot) {
        if (snapshot == null) {
            return Decision.CAUTION;
        }

        SourceType src = q.getSource();
        if (src == null) {
            src = SourceType.UNKNOWN;
        }

        switch (src) {
            case MEAL_SWIPE:
                return decideForMealSwipe(snapshot);
            case CASE_CASH:
                return decideForCaseCash(q, snapshot);
            case PERSONAL:
            case UNKNOWN:
            default:
                return decideForPersonal(q, snapshot);
        }
    }

    private Decision decideForPersonal(PurchaseQuery q, BudgetSnapshot s) {
        Double weeklyBudget = s.getWeeklyBudgetPersonal();
        Double spent = s.getSpentThisWeekPersonal();

        if (weeklyBudget == null || weeklyBudget <= 0 || spent == null) {
            return Decision.CAUTION;
        }

        Double amt = q.getAmount();
        double ratioAfter;

        if (amt != null && amt > 0) {
            ratioAfter = (spent + amt) / weeklyBudget;
        } else {
            ratioAfter = spent / weeklyBudget;
        }

        if (ratioAfter < 0.6) {
            return Decision.YES;
        } else if (ratioAfter < 0.9) {
            return Decision.CAUTION;
        } else {
            return Decision.NO;
        }
    }

    private Decision decideForCaseCash(PurchaseQuery q, BudgetSnapshot s) {
        Double totalSemester = s.getCaseCashTotalSemester();
        Double spentThisWeek = s.getCaseCashSpentThisWeek();

        if (totalSemester == null || totalSemester <= 0 || spentThisWeek == null) {
            return Decision.CAUTION;
        }

        double idealPerWeek = totalSemester / SEMESTER_WEEKS;
        if (idealPerWeek <= 0) {
            return Decision.CAUTION;
        }

        Double amt = q.getAmount();
        double ratioCurrent = spentThisWeek / idealPerWeek;
        double ratioAfter = ratioCurrent;

        if (amt != null && amt > 0) {
            ratioAfter = (spentThisWeek + amt) / idealPerWeek;
        }

        // More forgiving thresholds for CaseCash:
        // < 0.85 of weekly pace → YES
        // 0.85–1.10 → CAUTION
        // > 1.10 → NO
        if (ratioAfter < 0.85) {
            return Decision.YES;
        } else if (ratioAfter < 1.10) {
            return Decision.CAUTION;
        } else {
            return Decision.NO;
        }
    }

    private Decision decideForMealSwipe(BudgetSnapshot s) {
        Integer total = s.getMealSwipesWeeklyTotal();
        Integer used = s.getMealSwipesUsedThisWeek();

        if (total == null || total <= 0 || used == null) {
            return Decision.CAUTION;
        }

        int remaining = total - used;
        double ratioUsed = used / (double) total;

        if (remaining <= 0) {
            return Decision.NO;      // out of swipes
        }

        if (ratioUsed < 0.6) {
            return Decision.YES;     // plenty left
        } else if (ratioUsed < 0.9) {
            return Decision.CAUTION; // getting tight
        } else {
            return Decision.NO;      // very few left
        }
    }
}
