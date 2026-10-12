import java.util.*;
import java.net.http.*;
import java.net.URI;

// 1. Interface (OOP)
public interface AIService {
    String getAIResponse(String userPrompt, List<Map<String, String>> chatHistory) throws AICommunicationException;
}

// 2. Concrete Implementation using Core Java HTTP Client
public class CloudAIService implements AIService {
    
    private final HttpClient httpClient;

    public CloudAIService() {
        // Java 11+ built-in HTTP Client (Core Java Concept)
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Override
    public String getAIResponse(String userPrompt, List<Map<String, String>> chatHistory) throws AICommunicationException {
        try {
            // Build JSON Payload manually to avoid external dependencies like Gson/Jackson
            StringBuilder jsonPayload = new StringBuilder();
            jsonPayload.append("{")
                       .append("\"model\": \"").append(AIConfig.MODEL_NAME).append("\",")
                       .append("\"messages\": [");

            // Add System Prompt (Teaches the AI to be a coding tutor)
            jsonPayload.append("{\"role\": \"system\", \"content\": \"You are CodeMentor, an expert Java and coding tutor. Explain concepts simply and provide code examples.\"},");

            // Add Chat History (Collections usage)
            for (Map<String, String> msg : chatHistory) {
                jsonPayload.append("{\"role\": \"").append(msg.get("role"))
                           .append("\", \"content\": \"").append(escapeJson(msg.get("content"))).append("\"},");
            }

            // Add Current User Prompt
            jsonPayload.append("{\"role\": \"user\", \"content\": \"").append(escapeJson(userPrompt)).append("\"}")
                       .append("]}");

            // Create HTTP Request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AIConfig.API_ENDPOINT))
                    .header("Authorization", "Bearer " + AIConfig.API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString()))
                    .build();

            // Send Request and get Response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractContentFromJson(response.body());
            } else {
                throw new AICommunicationException("API returned status " + response.statusCode(), null);
            }

        } catch (Exception e) {
            throw new AICommunicationException("Failed to communicate with AI", e);
        }
    }

    // Helper to parse JSON response without external libraries
    private String extractContentFromJson(String jsonResponse) {
        // Simple string extraction for the "content" field
        int start = jsonResponse.indexOf("\"content\":\"") + 11;
        int end = jsonResponse.indexOf("\"", start);
        if (start > 10 && end > start) {
            return jsonResponse.substring(start, end).replace("\\n", "\n");
        }
        return "I'm sorry, I couldn't process that request.";
    }

    // Helper to escape quotes in JSON
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}