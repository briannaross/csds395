package com.cwru.budgetbot;

import org.springframework.stereotype.Service;

@Service
public class SwipeParser {

    public SourceType detectSource(String lower,
                                   String merchant,
                                   Double amount) {

        // explicit mentions first
        if (lower.contains("casecash") || lower.contains("case cash")) {
            return SourceType.CASE_CASH;
        }
        if (lower.contains("meal swipe") ||
                lower.contains("swipe") && (lower.contains("dining") || lower.contains("meal"))) {
            return SourceType.MEAL_SWIPE;
        }

        // dining halls default to swipes
        if (merchant != null) {
            String m = merchant.toLowerCase();
            if (m.contains("dining hall") ||
                    m.contains("north dining") ||
                    m.contains("south dining")) {
                return SourceType.MEAL_SWIPE;
            }
        }

        // everything else → personal funds by default
        return SourceType.PERSONAL;
    }
}
