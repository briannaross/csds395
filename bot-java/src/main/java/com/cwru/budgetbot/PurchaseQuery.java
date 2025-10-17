package com.cwru.budgetbot;

import java.util.Objects;

public class PurchaseQuery {
    private final IntentType intent;
    private final String merchant; // normalized or "Unknown"
    private final Double amount;   // nullable if not found
    private final SourceType source; // inferred from text if possible
    private final String original;

    public PurchaseQuery(IntentType intent, String merchant, Double amount, SourceType source, String original) {
        this.intent = intent;
        this.merchant = merchant;
        this.amount = amount;
        this.source = source;
        this.original = original;
    }

    public IntentType getIntent() { return intent; }
    public String getMerchant() { return merchant; }
    public Double getAmount() { return amount; }
    public SourceType getSource() { return source; }
    public String getOriginal() { return original; }

    @Override
    public String toString() {
        return "PurchaseQuery{" +
                "intent=" + intent +
                ", merchant='" + merchant + '\'' +
                ", amount=" + amount +
                ", source=" + source +
                ", original='" + original + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PurchaseQuery other)) return false;
        return intent == other.intent &&
                Objects.equals(merchant, other.merchant) &&
                Objects.equals(amount, other.amount) &&
                source == other.source &&
                Objects.equals(original, other.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intent, merchant, amount, source, original);
    }
}
