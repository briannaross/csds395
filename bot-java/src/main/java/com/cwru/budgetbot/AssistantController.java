package com.cwru.budgetbot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final IntentParser parser = new IntentParser();
    private final BudgetBotResponder responder = new BudgetBotResponder();
    private final DecisionEngine decisionEngine = new DecisionEngine();

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody AssistantRequest req) {
        String question = req.getQuestion() != null ? req.getQuestion() : "";

        // 1) Parse natural language into PurchaseQuery
        PurchaseQuery q = parser.parse(question);

        // 2) Build snapshot from JSON fields
        BudgetSnapshot snapshot = BudgetSnapshot.fromRequest(req);

        // 3) Decide YES / CAUTION / NO
        Decision decision = decisionEngine.decide(q, snapshot);

        // 4) Pick the single best response string
        String message = responder.pickResponse(q, decision);

        // 5) Return structured info + chosen message
        return ResponseEntity.ok(Map.of(
                "intent", q.getIntent().name(),
                "merchant", q.getMerchant(),
                "amount", q.getAmount(),
                "source", q.getSource().name(),
                "decision", decision.name(),
                "message", message
        ));
    }
}

