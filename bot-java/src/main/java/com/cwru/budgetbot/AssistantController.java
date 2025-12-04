package com.cwru.budgetbot;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant")
@CrossOrigin(origins = "http://localhost:3000")
public class AssistantController {

    private final IntentParser parser = new IntentParser();
    private final BudgetBotResponder responder = new BudgetBotResponder();
    private final DecisionEngine decisionEngine = new DecisionEngine();

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody AssistantRequest req) {
        String question = req.getQuestion() != null ? req.getQuestion() : "";

        PurchaseQuery q = parser.parse(question);

        BudgetSnapshot snapshot = BudgetSnapshot.fromRequest(req);

        Decision decision = decisionEngine.decide(q, snapshot);

        String message = responder.pickResponse(q, decision);

        return ResponseEntity.ok(Map.of(
                "intent", q.getIntent() != null ? q.getIntent().name() : "UNKNOWN",
                "merchant", q.getMerchant() != null ? q.getMerchant() : "Unknown",
                "amount", q.getAmount() != null ? q.getAmount() : 0.0,
                "source", q.getSource() != null ? q.getSource().name() : "UNKNOWN",
                "decision", decision != null ? decision.name() : "CAUTION",
                "message", message != null ? message : "I couldn't process that request."
        ));
    }
}
