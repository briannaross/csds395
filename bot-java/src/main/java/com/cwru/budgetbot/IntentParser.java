package com.cwru.budgetbot;

import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class IntentParser {

    private final MerchantLexicon lexicon;
    private final MoneyParser moneyParser;
    private final SwipeParser swipeParser;

    public IntentParser(MerchantLexicon lexicon,
                        MoneyParser moneyParser,
                        SwipeParser swipeParser) {
        this.lexicon = lexicon;
        this.moneyParser = moneyParser;
        this.swipeParser = swipeParser;
    }

    public PurchaseQuery parse(String input) {
        String text = input.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        // 1) INTENT: rule-based only
        IntentType intent = detectIntent(lower);

        // 2) MERCHANT: canonical name via lexicon lookup
        Optional<MerchantLexicon.Match> match = lexicon.find(text);
        // use lambda instead of method reference to avoid the compiler complaint
        String merchant = match
                .map(MerchantLexicon.Match::canonicalName)   // note the () via method ref
                .orElse(null);
        // ^ assumes Match has a public field canonicalName (which is how we had it earlier)

        // 3) AMOUNT OF MONEY (if any)
        MoneyParser.Result money = moneyParser.parse(lower);
        Double amount = (money != null) ? money.amount : null;

        // 4) SOURCE TYPE (PERSONAL / CASE_CASH / MEAL_SWIPE)
        SourceType source = swipeParser.detectSource(lower, merchant, amount);

        // 5) CHEAPNESS PREFERENCE
        boolean cheapPreference =
                lower.contains("cheap") ||
                        lower.contains("tight budget") ||
                        lower.contains("save money") ||
                        lower.contains("broke");

        return new PurchaseQuery(
                intent,
                merchant,
                amount,
                source,
                text,
                cheapPreference
        );
    }

    // ----------------- helper for intent detection -----------------

    private IntentType detectIntent(String lower) {
        // status / “how am I doing” questions
        if (lower.contains("how am i doing") ||
                lower.contains("how'm i doing") ||
                lower.contains("how am i doing on my budget") ||
                lower.contains("on my budget") ||
                lower.contains("on track") ||
                lower.contains("over budget")) {
            return IntentType.HOW_AM_I_DOING;
        }

        // recommendations
        if (lower.contains("where should i eat") ||
                lower.contains("where can i eat") ||
                lower.contains("any recommendations") ||
                lower.contains("what should i get for food") ||
                lower.contains("where can i get groceries") ||
                lower.contains("where can i get food") ||
                lower.contains("somewhere cheap") ||
                lower.contains("cheap place to eat")) {
            return IntentType.RECS;
        }

        // “can I buy / spend” style questions
        if (lower.startsWith("can i") ||
                lower.startsWith("is it okay if") ||
                lower.contains("okay to spend") ||
                lower.contains("is it ok if i spend") ||
                lower.contains("should i buy") ||
                lower.contains("should i spend") ||
                lower.contains("will i stay on track if i spend")) {
            return IntentType.CAN_I_BUY;
        }

        return IntentType.UNKNOWN;
    }
}
