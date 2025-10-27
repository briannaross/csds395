package com.cwru.budgetbot;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        IntentParser parser = new IntentParser();
        BudgetBotResponder responder = new BudgetBotResponder();

        List<String> samples = List.of(
            "I want to get ice cream at Mitchell's today",
            "can I buy starbucks for $7?",
            "is it ok if I spend 12 dollars at the dining hall?",
            "should I use a swipe at dining?",
            "grabbing chipotle for about 9 bucks on my personal card",
            "how am I doing on my budget this week?",
            "explain your recommendation about coffee",
            "mitchells for 6?",
            "on campus lunch swipe?",
            "case cash coffee at stbx",
            "i want to get food somewhere cheap"
        );

        for (String s : samples) {
            PurchaseQuery q = parser.parse(s);
            BudgetBotResponder.ResponseBundle r = responder.draftResponses(q);

            System.out.println("USER: " + s);
            System.out.println(r.yes);
            System.out.println(r.caution);
            System.out.println(r.no);
            System.out.println();
        }
    }
}
