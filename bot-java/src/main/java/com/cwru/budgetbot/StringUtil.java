package com.cwru.budgetbot;

import java.text.DecimalFormat;

public final class StringUtil {
    private static final DecimalFormat MONEY = new DecimalFormat("$0.00");

    private StringUtil() {}

    public static String money(Double amount) {
        if (amount == null) return "an unspecified amount";
        return MONEY.format(amount);
    }

    public static String orUnknown(String s, String fallback) {
        return (s == null || s.isBlank() || "Unknown".equalsIgnoreCase(s)) ? fallback : s;
    }
}
