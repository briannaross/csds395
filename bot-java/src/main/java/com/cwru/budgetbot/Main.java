package com.cwru.budgetbot;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Wire parsers for local testing
        MerchantLexicon lexicon   = new MerchantLexicon();
        MoneyParser    money      = new MoneyParser();
        SwipeParser    swipe      = new SwipeParser();
        IntentParser   parser     = new IntentParser(lexicon, money, swipe);

        BudgetBotResponder responder   = new BudgetBotResponder();
        DecisionEngine     decisionEngine = new DecisionEngine();

        // ----- Sample budget snapshot for testing -----
        BudgetSnapshot snapshot = new BudgetSnapshot(
                80.0,   // weeklyBudgetPersonal
                35.0,   // spentThisWeekPersonal
                300.0,  // caseCashTotalSemester
                15.0,   // caseCashSpentThisWeek
                17,     // mealSwipesWeeklyTotal
                5       // mealSwipesUsedThisWeek
        );

        // ----- Sample questions -----
        List<String> samples = List.of(
                "Can I get Starbucks for $5?",
                "Is it ok to get Chipotle for 12 dollars?",
                "How am I doing on my budget this week?",
                "Where can I get groceries?",
                "I want to get food somewhere cheap"
        );

        for (String s : samples) {
            PurchaseQuery q = parser.parse(s);
            Decision decision = decisionEngine.decide(q, snapshot);
            String message = responder.pickResponse(q, decision);

            System.out.println("USER: " + s);
            System.out.println("Intent: " + q.getIntent());
            System.out.println("Merchant: " + q.getMerchant());
            System.out.println("Amount: " + q.getAmount());
            System.out.println("Source: " + q.getSource());
            System.out.println("Decision: " + decision);
            System.out.println("BOT: " + message);
            System.out.println("----");
        }
    }
}
