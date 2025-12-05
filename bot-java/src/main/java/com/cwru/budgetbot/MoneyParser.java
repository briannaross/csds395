package com.cwru.budgetbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class MoneyParser {

    // Matches things like "$5", "$5.25", "5 dollars", "5 bucks"
    private static final Pattern MONEY_PATTERN =
            Pattern.compile("(\\$?)(\\d+(?:\\.\\d{1,2})?)\\s*(dollars?|bucks?)?",
                    Pattern.CASE_INSENSITIVE);

    public static class Result {
        public final Double amount;
        public final String rawText;

        public Result(Double amount, String rawText) {
            this.amount = amount;
            this.rawText = rawText;
        }
    }

    public Result parse(String text) {
        Matcher m = MONEY_PATTERN.matcher(text);
        if (m.find()) {
            String numberPart = m.group(2);
            try {
                double value = Double.parseDouble(numberPart);
                return new Result(value, m.group(0));
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return null;
    }
}
