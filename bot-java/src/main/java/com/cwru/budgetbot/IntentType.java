package com.cwru.budgetbot;

public enum IntentType {
    CAN_I_BUY,
    HOW_AM_I_DOING,
    RECS,
    UNKNOWN;

    // Map external / Wit.ai intent names to this enum.
    public static IntentType fromExternal(String name) {
        if (name == null) return UNKNOWN;
        String n = name.trim().toUpperCase();

        switch (n) {
            case "CAN_I_BUY":
            case "CAN_I_BUY_INTENT":
                return CAN_I_BUY;

            case "HOW_AM_I_DOING":
            case "HOW_AM_I_DOING_INTENT":
            case "BUDGET_STATUS":
                return HOW_AM_I_DOING;

            case "RECS":
            case "RECOMMENDATION":
            case "FOOD_RECOMMENDATION":
                return RECS;

            default:
                return UNKNOWN;
        }
    }
}
