import java.util.*;
import java.util.concurrent.*;

// 1. Interface for Tutor Operations (OOP)
public interface LiveTutorService {
    void requestHumanTutor(int studentId, String studentName, String lastMessage);
    void replyToStudent(int studentId, String teacherMessage);
    List<ChatRequest> getPendingRequests();
}

// 2. Concrete Implementation with Synchronization & Collections
public class LiveChatManager implements LiveTutorService {
    
    // Singleton pattern for shared state across Web and GUI
    private static LiveChatManager instance;
    
    // Collections: Thread-safe queue for pending requests
    private final BlockingQueue<ChatRequest> pendingRequests = new LinkedBlockingQueue<>();
    
    // Collections: Thread-safe map for active human chat sessions
    private final Map<Integer, List<String>> activeHumanChats = new ConcurrentHashMap<>();

    private LiveChatManager() {}

    public static synchronized LiveChatManager getInstance() {
        if (instance == null) {
            instance = new LiveChatManager();
        }
        return instance;
    }

    @Override
    public void requestHumanTutor(int studentId, String studentName, String lastMessage) {
        ChatRequest request = new ChatRequest(studentId, studentName, lastMessage, System.currentTimeMillis());
        pendingRequests.offer(request); // Non-blocking add
        
        // Initialize active chat history for this student
        activeHumanChats.putIfAbsent(studentId, new ArrayList<>());
        activeHumanChats.get(studentId).add("SYSTEM: Student requested human tutor.");
    }

    @Override
    public void replyToStudent(int studentId, String teacherMessage) {
        if (activeHumanChats.containsKey(studentId)) {
            activeHumanChats.get(studentId).add("TEACHER: " + teacherMessage);
            
            // TODO: Call StudentDAO.saveChatLog(studentId, teacherMessage, "HUMAN") here
        }
    }

    @Override
    public List<ChatRequest> getPendingRequests() {
        return new ArrayList<>(pendingRequests); // Return a copy to avoid concurrent modification
    }
    
    public List<String> getActiveChatHistory(int studentId) {
        return activeHumanChats.getOrDefault(studentId, new ArrayList<>());
    }
}

// Helper Class for Collections
class ChatRequest {
    int studentId;
    String studentName;
    String lastMessage;
    long timestamp;

    public ChatRequest(int studentId, String studentName, String lastMessage, long timestamp) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }
}