// 1. Configuration Class (Encapsulation)
public class AIConfig {
    // Store your API key securely
    public static final String API_KEY = System.getenv("CODEMENTOR_API_KEY");
    
    // Note: Change this URL to your specific AI provider's endpoint 
    // (e.g., DashScope, OpenAI, Groq, or local Ollama)
    public static final String API_ENDPOINT = "https://api.your-ai-provider.com/v1/chat/completions"; 
    public static final String MODEL_NAME = "qwen-turbo"; // Or your specific model
}

// 2. Custom Exception (Exception Handling Rubric)
public class AICommunicationException extends Exception {
    public AICommunicationException(String message, Throwable cause) {
        super("AI Engine Error: " + message, cause);
    }
}