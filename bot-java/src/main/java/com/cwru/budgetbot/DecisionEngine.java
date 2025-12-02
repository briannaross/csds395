package com.cwru.budgetbot;

public class DecisionEngine {

    // Assumed semester length in weeks
    private static final int SEMESTER_WEEKS = 15;

    public Decision decide(PurchaseQuery q, BudgetSnapshot snapshot) {
        if (snapshot == null) {
            return Decision.CAUTION;
        }

        switch (q.getSource()) {
            case MEAL_SWIPE:
                return decideForMealSwipe(snapshot);
            case CASE_CASH:
                return decideForCaseCash(snapshot);
            case PERSONAL:
            case UNKNOWN:
            default:
                return decideForPersonal(snapshot);
        }
    }

    private Decision decideForPersonal(BudgetSnapshot s) {
        Double weeklyBudget = s.getWeeklyBudgetPersonal();
        Double spent = s.getSpentThisWeekPersonal();

        if (weeklyBudget == null || weeklyBudget <= 0 || spent == null) {
            return Decision.CAUTION;
        }

        double ratio = spent / weeklyBudget;

        if (ratio < 0.6) {
            return Decision.YES;
        } else if (ratio < 0.9) {
            return Decision.CAUTION;
        } else {
            return Decision.NO;
        }
    }

    private Decision decideForCaseCash(BudgetSnapshot s) {
        Double totalSemester = s.getCaseCashTotalSemester();
        Double spentThisWeek = s.getCaseCashSpentThisWeek();

        if (totalSemester == null || totalSemester <= 0 || spentThisWeek == null) {
            return Decision.CAUTION;
        }

        double idealPerWeek = totalSemester / SEMESTER_WEEKS;
        if (idealPerWeek <= 0) {
            return Decision.CAUTION;
        }

        double ratio = spentThisWeek / idealPerWeek;

        // Same thresholds: <60% → YES, 60–90% → CAUTION, >90% → NO
        if (ratio < 0.6) {
            return Decision.YES;
        } else if (ratio < 0.9) {
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
            return Decision.YES;     // plenty of swipes left
        } else if (ratioUsed < 0.9) {
            return Decision.CAUTION; // getting tight
        } else {
            return Decision.NO;      // too few swipes left
        }
    }
}
