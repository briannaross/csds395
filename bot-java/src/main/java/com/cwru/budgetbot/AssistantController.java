package com.cwru.budgetbot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final IntentParser parser = new IntentParser();
    private final BudgetBotResponder responder = new BudgetBotResponder();

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody Map<String, Object> body) {
        String question = (String) body.getOrDefault("question", "");
        // later you can also accept userId, week info, etc.

        PurchaseQuery q = parser.parse(question);
        BudgetBotResponder.ResponseBundle bundle = responder.draftResponses(q);

        // For now, return all three scenarios so the front-end can show them.
        return ResponseEntity.ok(Map.of(
                "intent", q.getIntent().name(),
                "merchant", q.getMerchant(),
                "amount", q.getAmount(),
                "source", q.getSource().name(),
                "yes", bundle.yes,
                "caution", bundle.caution,
                "no", bundle.no
        ));
    }
}
