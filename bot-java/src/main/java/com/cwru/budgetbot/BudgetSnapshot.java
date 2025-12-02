package com.cwru.budgetbot;

public class BudgetSnapshot {

    private final Double weeklyBudgetPersonal;
    private final Double spentThisWeekPersonal;

    private final Double caseCashTotalSemester;
    private final Double caseCashSpentThisWeek;

    private final Integer mealSwipesWeeklyTotal;
    private final Integer mealSwipesUsedThisWeek;

    public BudgetSnapshot(Double weeklyBudgetPersonal,
                          Double spentThisWeekPersonal,
                          Double caseCashTotalSemester,
                          Double caseCashSpentThisWeek,
                          Integer mealSwipesWeeklyTotal,
                          Integer mealSwipesUsedThisWeek) {
        this.weeklyBudgetPersonal = weeklyBudgetPersonal;
        this.spentThisWeekPersonal = spentThisWeekPersonal;
        this.caseCashTotalSemester = caseCashTotalSemester;
        this.caseCashSpentThisWeek = caseCashSpentThisWeek;
        this.mealSwipesWeeklyTotal = mealSwipesWeeklyTotal;
        this.mealSwipesUsedThisWeek = mealSwipesUsedThisWeek;
    }

    public static BudgetSnapshot fromRequest(AssistantRequest req) {
        return new BudgetSnapshot(
                req.getWeeklyBudgetPersonal(),
                req.getSpentThisWeekPersonal(),
                req.getCaseCashTotalSemester(),
                req.getCaseCashSpentThisWeek(),
                req.getMealSwipesWeeklyTotal(),
                req.getMealSwipesUsedThisWeek()
        );
    }

    public Double getWeeklyBudgetPersonal()         { return weeklyBudgetPersonal; }
    public Double getSpentThisWeekPersonal()        { return spentThisWeekPersonal; }
    public Double getCaseCashTotalSemester()        { return caseCashTotalSemester; }
    public Double getCaseCashSpentThisWeek()        { return caseCashSpentThisWeek; }
    public Integer getMealSwipesWeeklyTotal()       { return mealSwipesWeeklyTotal; }
    public Integer getMealSwipesUsedThisWeek()      { return mealSwipesUsedThisWeek; }
}
