package com.cwru.budgetbot;

/**
 * Snapshot of a student's budget state for one week.
 * Includes:
 *  - personal weekly budget + spent
 *  - CaseCash totals and this week's CaseCash spending
 *  - meal swipe totals and swipes used this week
 */
public class BudgetSnapshot {

    private final double weeklyBudgetPersonal;
    private final double spentThisWeekPersonal;

    private final double caseCashTotalSemester;
    private final double caseCashSpentThisWeek;

    private final int mealSwipesWeeklyTotal;
    private final int mealSwipesUsedThisWeek;

    public BudgetSnapshot(double weeklyBudgetPersonal,
                          double spentThisWeekPersonal,
                          double caseCashTotalSemester,
                          double caseCashSpentThisWeek,
                          int mealSwipesWeeklyTotal,
                          int mealSwipesUsedThisWeek) {
        this.weeklyBudgetPersonal   = weeklyBudgetPersonal;
        this.spentThisWeekPersonal  = spentThisWeekPersonal;
        this.caseCashTotalSemester  = caseCashTotalSemester;
        this.caseCashSpentThisWeek  = caseCashSpentThisWeek;
        this.mealSwipesWeeklyTotal  = mealSwipesWeeklyTotal;
        this.mealSwipesUsedThisWeek = mealSwipesUsedThisWeek;
    }

    /** Demo snapshot so controller / Main always have safe, non-null data. */
    public static BudgetSnapshot demo() {
        // Example numbers: tune these if you want different behavior.
        return new BudgetSnapshot(
                80.0,   // weeklyBudgetPersonal
                35.0,   // spentThisWeekPersonal
                300.0,  // caseCashTotalSemester
                40.0,   // caseCashSpentThisWeek
                17,     // mealSwipesWeeklyTotal
                6       // mealSwipesUsedThisWeek
        );
    }

    // --- PERSONAL ---

    public double getWeeklyBudgetPersonal() {
        return weeklyBudgetPersonal;
    }

    public double getSpentThisWeekPersonal() {
        return spentThisWeekPersonal;
    }

    // Convenience methods used by older code:
    public double getWeeklyBudget() {
        return weeklyBudgetPersonal;
    }

    public double getSpentThisWeek() {
        return spentThisWeekPersonal;
    }

    // --- CASECASH ---

    public double getCaseCashTotalSemester() {
        return caseCashTotalSemester;
    }

    public double getCaseCashSpentThisWeek() {
        return caseCashSpentThisWeek;
    }

    // Rough “per week” CaseCash budget, assuming ~15-week semester.
    public double getCaseCashWeeklyBudgetApprox() {
        if (caseCashTotalSemester <= 0) return 0.0;
        return caseCashTotalSemester / 15.0;
    }

    // --- MEAL SWIPES ---

    public int getMealSwipesWeeklyTotal() {
        return mealSwipesWeeklyTotal;
    }

    public int getMealSwipesUsedThisWeek() {
        return mealSwipesUsedThisWeek;
    }
}
