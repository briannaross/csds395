package com.cwru.budgetbot;

import java.util.List;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        IntentParser parser = new IntentParser();

        List<String> samples = List.of(
                "I want to get ice cream at Mitchell's today",
                "can I buy mitchells for $6.75?",
                "Should I get MITCHELL icecream for 7 bucks?",
                "thinking of grabbing something at mitchels later",
                "is it ok if I spend 12 dollars at the dining hall?",
                "should I use a swipe at dining?",
                "2 meal swipes at dining",
                "can i get lunch at dhall",
                "one swipe at the cafeteria please",
                "grabbing a coffee from starbucks on campus",
                "case cash coffee at stbx",
                "is it ok if I get dunkin for 4 dollars?",
                "should I get breakfast at dunkin donuts today?",
                "can I buy from the campus bookstore?",
                "grabbing chipotle for about 9 bucks on my personal card",
                "is it okay if I get food from giant eagle?",
                "can i spend $25 at amazon?",
                "might go to chipotle tonight with my debit card",
                "how am I doing on my budget this week?",
                "explain your recommendation about coffee",
                "why do you recommend cutting back on starbucks?",
                "is it okay if I buy food off campus?",
                "thinking of getting dinner somewhere, should I?",
                "can I get a coffee for four dollars?",
                "can I buy ice cream for 7?",
                "can I spend 10 dollars on lunch?",
                "grabbing coffee (about $5)",
                "might grab dunkin for 4 bucks",
                "CAN I USE A SWIPE AT DUNKIN?",
                "can i get food at dining hall with my swipe?",
                "is it ok to use meal swipes today?",
                "using a swipe at starbucks",
                "thinking of using 2 swipes this week",
                "i want to get food somewhere cheap",
                "any recommendations for lunch?",
                "should i spend something today?",
                "might grab dunkin for bucks"
        );

        for (String s : samples) {
            PurchaseQuery q = parser.parse(s);
            System.out.println(s);
            System.out.println(" → " + q);
            System.out.println();
        }
    }
}