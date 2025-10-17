package com.cwru.budgetbot;

import java.text.Normalizer.Form;

public final class Normalizer {
    private Normalizer() {}

    /** Lowercase, remove accents, collapse whitespace, drop punctuation/apostrophes. */
    public static String normalize(String s) {
        if (s == null) return "";
        String t = java.text.Normalizer.normalize(s, Form.NFKD)
                .replaceAll("\\p{M}+", "")       // strip accents
                .toLowerCase()
                .replace("’", "'")
                .replaceAll("[^a-z0-9\\s]", " ") // drop punctuation to spaces
                .replaceAll("\\s+", " ")         // collapse spaces
                .trim();
        return t;
    }
}
