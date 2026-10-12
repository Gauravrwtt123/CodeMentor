// 1. Generics: A custom wrapper for Chat Messages
public class ChatSession<T extends Message> {
    private List<T> messageHistory;

    public ChatSession() {
        // 2. Collections: Using ArrayList and HashMap
        this.messageHistory = new ArrayList<>();
    }

    public void addMessage(T msg) {
        messageHistory.add(msg);
    }

    // Using Streams (Core Java Concept) to filter messages
    public List<T> getBotMessages() {
        return messageHistory.stream()
                .filter(m -> m.getSender().equals("BOT"))
                .collect(Collectors.toList());
    }
}

// Message Hierarchy for Generics
public class Message { String sender; String content; /* getters/setters */ }
public class TextMessage extends Message { }
public class CodeSnippetMessage extends Message { String language; }