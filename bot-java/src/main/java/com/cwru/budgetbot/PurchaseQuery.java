package com.cwru.budgetbot;

public class PurchaseQuery {

    private final IntentType intent;
    private final String merchant;
    private final Double amount;
    private final SourceType source;
    private final String originalText;
    private final boolean cheapPreference;

    public PurchaseQuery(IntentType intent,
                         String merchant,
                         Double amount,
                         SourceType source,
                         String originalText,
                         boolean cheapPreference) {
        this.intent = intent;
        this.merchant = merchant;
        this.amount = amount;
        this.source = source;
        this.originalText = originalText;
        this.cheapPreference = cheapPreference;
    }

    public IntentType getIntent() {
        return intent;
    }

    public String getMerchant() {
        return merchant;
    }

    public Double getAmount() {
        return amount;
    }

    public SourceType getSource() {
        return source;
    }

    public String getOriginalText() {
        return originalText;
    }

    /** True if the user explicitly asked for something cheap / affordable. */
    public boolean isCheapPreference() {
        return cheapPreference;
    }
}
