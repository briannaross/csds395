package com.cwru.budgetbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller for the chatbot API.
 *
 * POST /assistant/ask
 * Body: AssistantRequest (question + optional budget fields)
 * Response: JSON with parsed data, decision, and an LLM-generated message.
 */
@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final IntentParser intentParser;
    private final DecisionEngine decisionEngine;
    private final BudgetBotResponder responder;
    private final OpenAIChatClient openAIChatClient;

    @Autowired
    public AssistantController(IntentParser intentParser,
                               DecisionEngine decisionEngine,
                               BudgetBotResponder responder,
                               OpenAIChatClient openAIChatClient) {
        this.intentParser = intentParser;
        this.decisionEngine = decisionEngine;
        this.responder = responder;
        this.openAIChatClient = openAIChatClient;
    }

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody AssistantRequest request) {

        String question = request.getQuestion() == null ? "" : request.getQuestion().trim();

        // 1) Build BudgetSnapshot from request (with safe defaults if fields are missing)
        double weeklyBudgetPersonal   = orDefault(request.getWeeklyBudgetPersonal(),   80.0);
        double spentThisWeekPersonal  = orDefault(request.getSpentThisWeekPersonal(),  35.0);
        double caseCashTotalSemester  = orDefault(request.getCaseCashTotalSemester(),  300.0);
        double caseCashSpentThisWeek  = orDefault(request.getCaseCashSpentThisWeek(), 15.0);
        int    mealSwipesWeeklyTotal  = orDefault(request.getMealSwipesWeeklyTotal(), 17);
        int    mealSwipesUsedThisWeek = orDefault(request.getMealSwipesUsedThisWeek(), 5);

        BudgetSnapshot snapshot = new BudgetSnapshot(
                weeklyBudgetPersonal,
                spentThisWeekPersonal,
                caseCashTotalSemester,
                caseCashSpentThisWeek,
                mealSwipesWeeklyTotal,
                mealSwipesUsedThisWeek
        );

        // 2) Parse the natural language question into a PurchaseQuery
        PurchaseQuery q = intentParser.parse(question);

        // 3) Decide YES / CAUTION / NO using your rule-based engine
        Decision decision = decisionEngine.decide(q, snapshot);

        // 4) Let the original responder generate a suggestion message
        String botMessage = responder.pickResponse(q, decision);

        // 5) Call OpenAI with the question + structured data + botMessage
        String llmMessage = openAIChatClient.getAdvice(question, q, decision, snapshot, botMessage);

        // 6) Choose which message to expose as the main "message"
        String primaryMessage = (llmMessage != null && !llmMessage.isBlank())
                ? llmMessage
                : botMessage;

        // 7) Build JSON response
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source",   q.getSource() != null ? q.getSource().name() : null);
        result.put("intent",   q.getIntent() != null ? q.getIntent().name() : null);
        result.put("merchant", q.getMerchant());
        result.put("decision", decision != null ? decision.toString() : null);

        // MAIN MESSAGE the frontend will display:
        result.put("message", primaryMessage);

        // Optional debug fields (helpful for your write-up)
        result.put("botMessage", botMessage);
        result.put("llmMessage", llmMessage);

        result.put("amount", q.getAmount());

        return result;
    }

    // ------- small helpers for defaults -------

    private double orDefault(Double value, double defaultValue) {
        return (value == null || value.isNaN()) ? defaultValue : value;
    }

    private int orDefault(Integer value, int defaultValue) {
        return (value == null) ? defaultValue : value;
    }
}
