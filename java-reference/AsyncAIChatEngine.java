import java.util.*;
import java.util.concurrent.*;

public class AsyncAIChatEngine {
    
    // 1. Collections: Store chat history per student
    private final Map<Integer, List<Map<String, String>>> studentContexts = new ConcurrentHashMap<>();
    
    // 2. Multithreading: ExecutorService for managing background AI tasks
    private final ExecutorService aiExecutor = Executors.newFixedThreadPool(5);
    
    private final AIService aiService;

    public AsyncAIChatEngine() {
        this.aiService = new CloudAIService();
    }

    // Asynchronous method using CompletableFuture (Advanced Core Java)
    public CompletableFuture<String> askAI(int studentId, String userMessage) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get or create chat history for this student
                List<Map<String, String>> history = studentContexts.computeIfAbsent(studentId, k -> new ArrayList<>());

                // Call the AI
                String aiResponse = aiService.getAIResponse(userMessage, history);

                // Update Collections (Chat History)
                Map<String, String> userMsg = new HashMap<>();
                userMsg.put("role", "user");
                userMsg.put("content", userMessage);
                history.add(userMsg);

                Map<String, String> botMsg = new HashMap<>();
                botMsg.put("role", "assistant");
                botMsg.put("content", aiResponse);
                history.add(botMsg);

                // Keep history limited to last 10 messages to save API tokens
                if (history.size() > 20) {
                    history.subList(0, 2).clear(); 
                }

                return aiResponse;

            } catch (AICommunicationException e) {
                return "Error connecting to AI Tutor: " + e.getMessage();
            }
        }, aiExecutor);
    }
}