package com.cwru.budgetbot;

/**
 * Request body for /assistant/ask.
 * Must have a no-arg constructor + getters/setters
 * so Spring/Jackson can deserialize JSON into it.
 */
public class AssistantRequest {

    private String question;

    // All of these can be null in JSON; we default later if needed
    private Double weeklyBudgetPersonal;
    private Double spentThisWeekPersonal;

    private Double caseCashTotalSemester;
    private Double caseCashSpentThisWeek;

    private Integer mealSwipesWeeklyTotal;
    private Integer mealSwipesUsedThisWeek;

    public AssistantRequest() {
        // Required by Jackson
    }

    // ---- getters & setters ----

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
