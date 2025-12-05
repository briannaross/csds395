package com.cwru.budgetbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Lightweight client for the OpenAI "responses" API.
 *
 * Configuration is read from application.properties:
 *
 *   openai.api.key=sk-...
 *   openai.model=gpt-4.1-mini
 *
 * If the key is missing or invalid, this client will just return null and
 * the rule-based bot message will be used instead (no crash).
 */
@Service
public class OpenAIChatClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAIChatClient.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper   = new ObjectMapper();

    // Read from application.properties. If missing, default to empty string.
    @Value("${openai.api.key:}")
    private String apiKey;

    // Optional: you can override this in application.properties if you want.
    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    @PostConstruct
    public void checkConfig() {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[OpenAIChatClient] openai.api.key is not set. "
                    + "OpenAI calls will be skipped and the rule-based bot message will be used instead.");
        } else {
            log.info("[OpenAIChatClient] OpenAI client initialized with model '{}'", model);
        }
    }

    /**
     * Build a rich prompt and send it to the OpenAI Responses API.
     * Returns a friendly answer string, or null if anything goes wrong.
     *
     * AssistantController should treat a non-null return value as the primary
     * message, and only fall back to the rule-based message when this is null.
     */
    public String getAdvice(String question,
                            PurchaseQuery q,
                            Decision decision,
                            BudgetSnapshot snap,
                            String botMessage) {

        // If there's no key, we just don't call OpenAI.
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }

        try {
            String prompt = buildPrompt(question, q, decision, snap, botMessage);
            String jsonResponse = callOpenAI(prompt);
            return extractText(jsonResponse);
        } catch (Exception e) {
            log.error("[OpenAIChatClient] Error while calling OpenAI", e);
            return null;  // fallback will be used by the controller
        }
    }

    // ---------- Prompt construction ----------

    private String buildPrompt(String question,
                               PurchaseQuery q,
                               Decision decision,
                               BudgetSnapshot snap,
                               String botMessage) {

        StringBuilder sb = new StringBuilder();

        sb.append("You are a helpful budgeting assistant for a college student.\n\n");

        sb.append("USER QUESTION:\n");
        sb.append("\"").append(question).append("\"\n\n");

        sb.append("STRUCTURED DATA ABOUT THE USER'S SITUATION:\n");
        sb.append("- Parsed intent: ").append(q.getIntent()).append("\n");
        sb.append("- Merchant: ").append(q.getMerchant()).append("\n");
        sb.append("- Amount: ").append(q.getAmount()).append("\n");
        sb.append("- Payment source: ").append(q.getSource()).append("\n");
        sb.append("- Decision from local rule-based bot: ").append(decision).append("\n\n");

        sb.append("BUDGET SNAPSHOT:\n");
        sb.append("- Weekly personal budget: ").append(snap.getWeeklyBudgetPersonal()).append("\n");
        sb.append("- Personal spent this week: ").append(snap.getSpentThisWeekPersonal()).append("\n");
        sb.append("- CaseCash total this semester: ").append(snap.getCaseCashTotalSemester()).append("\n");
        sb.append("- CaseCash spent this week: ").append(snap.getCaseCashSpentThisWeek()).append("\n");
        sb.append("- Meal swipes weekly total: ").append(snap.getMealSwipesWeeklyTotal()).append("\n");
        sb.append("- Meal swipes used this week: ").append(snap.getMealSwipesUsedThisWeek()).append("\n\n");

        sb.append("LOCAL BOT SUMMARY MESSAGE:\n");
        sb.append("\"").append(botMessage).append("\"\n\n");

        sb.append("YOUR TASK:\n");
        sb.append("- Use the numeric data directly to reason about affordability.\n");
        sb.append("- You are allowed to disagree with the local bot's decision.\n");
        sb.append("- Give a short, friendly answer directly to the user (2–4 sentences).\n");
        sb.append("- If the question is not actually about money or budgeting, ");
        sb.append("ignore the budget data and just answer the question helpfully.\n");

        return sb.toString();
    }

    // ---------- HTTP call to OpenAI ----------

    private String callOpenAI(String prompt) throws Exception {
        ObjectNode root = mapper.createObjectNode();
        root.put("model", model);
        root.put("input", prompt);

        String body = mapper.writeValueAsString(root);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status != 200) {
            log.warn("[OpenAIChatClient] Non-200 status from OpenAI: {} body: {}", status, response.body());
        }

        return response.body();
    }

    // ---------- Parse response text ----------

    private String extractText(String json) throws Exception {
        if (json == null || json.isBlank()) {
            return null;
        }
        JsonNode root = mapper.readTree(json);

        // responses API: output[0].content[0].text
        JsonNode output = root.path("output");
        if (!output.isArray() || output.size() == 0) {
            return null;
        }

        JsonNode firstOutput = output.get(0);
        JsonNode contentArr = firstOutput.path("content");
        if (!contentArr.isArray() || contentArr.size() == 0) {
            return null;
        }

        JsonNode firstContent = contentArr.get(0);
        JsonNode textNode = firstContent.path("text");
        if (textNode.isMissingNode()) {
            return null;
        }

        String text = textNode.asText();
        return (text == null || text.isBlank()) ? null : text.trim();
    }
}
