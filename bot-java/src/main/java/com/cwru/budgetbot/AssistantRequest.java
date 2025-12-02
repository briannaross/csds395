package com.cwru.budgetbot;

public class AssistantRequest {

    private String question;

    // Personal / general budget (non-CaseCash)
    private Double weeklyBudgetPersonal;      // e.g. 80.0
    private Double spentThisWeekPersonal;     // e.g. 35.0

    // CaseCash – semester-based
    private Double caseCashTotalSemester;     // e.g. 300.0
    private Double caseCashSpentThisWeek;     // e.g. 15.0

    // Meal swipes – weekly
    private Integer mealSwipesWeeklyTotal;    // e.g. 17
    private Integer mealSwipesUsedThisWeek;   // e.g. 5

    public AssistantRequest() {}

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public Double getWeeklyBudgetPersonal() {
        return weeklyBudgetPersonal;
    }
    public void setWeeklyBudgetPersonal(Double weeklyBudgetPersonal) {
        this.weeklyBudgetPersonal = weeklyBudgetPersonal;
    }

    public Double getSpentThisWeekPersonal() {
        return spentThisWeekPersonal;
    }
    public void setSpentThisWeekPersonal(Double spentThisWeekPersonal) {
        this.spentThisWeekPersonal = spentThisWeekPersonal;
    }

    public Double getCaseCashTotalSemester() {
        return caseCashTotalSemester;
    }
    public void setCaseCashTotalSemester(Double caseCashTotalSemester) {
        this.caseCashTotalSemester = caseCashTotalSemester;
    }

    public Double getCaseCashSpentThisWeek() {
        return caseCashSpentThisWeek;
    }
    public void setCaseCashSpentThisWeek(Double caseCashSpentThisWeek) {
        this.caseCashSpentThisWeek = caseCashSpentThisWeek;
    }

    public Integer getMealSwipesWeeklyTotal() {
        return mealSwipesWeeklyTotal;
    }
    public void setMealSwipesWeeklyTotal(Integer mealSwipesWeeklyTotal) {
        this.mealSwipesWeeklyTotal = mealSwipesWeeklyTotal;
    }

    public Integer getMealSwipesUsedThisWeek() {
        return mealSwipesUsedThisWeek;
    }
    public void setMealSwipesUsedThisWeek(Integer mealSwipesUsedThisWeek) {
        this.mealSwipesUsedThisWeek = mealSwipesUsedThisWeek;
    }
}
