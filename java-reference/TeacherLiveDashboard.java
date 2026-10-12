import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeacherLiveDashboard extends JFrame {
    private DefaultListModel<String> requestListModel;
    private JList<String> requestList;
    private JTextArea chatArea;
    private JTextField replyField;
    
    private LiveChatManager chatManager = LiveChatManager.getInstance();
    private int selectedStudentId = -1;

    public TeacherLiveDashboard() {
        setTitle("CodeMentor - Live Human Tutor Dashboard");
        setSize(800, 500);
        setLayout(new BorderLayout());

        // Left Panel: Pending Requests
        requestListModel = new DefaultListModel<>();
        requestList = new JList<>(requestListModel);
        requestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = requestList.getSelectedIndex();
                if (index != -1) {
                    // Extract student ID from the list item (simplified parsing)
                    String item = requestListModel.getElementAt(index);
                    selectedStudentId = Integer.parseInt(item.split(" - ")[0].replace("ID:", "").trim());
                    loadChatHistory(selectedStudentId);
                }
            }
        });
        add(new JScrollPane(requestList), BorderLayout.WEST);

        // Right Panel: Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Bottom Panel: Reply Input
        JPanel bottomPanel = new JPanel(new BorderLayout());
        replyField = new JTextField();
        JButton sendBtn = new JButton("Send to Student");
        sendBtn.addActionListener(e -> sendReply());
        bottomPanel.add(replyField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Multithreading: Background timer to refresh requests every 3 seconds
        Timer refreshTimer = new Timer(3000, e -> refreshRequests());
        refreshTimer.start();
    }

    private void refreshRequests() {
        // Runs on EDT (Event Dispatch Thread), safe for GUI updates
        List<ChatRequest> requests = chatManager.getPendingRequests();
        requestListModel.clear();
        for (ChatRequest req : requests) {
            requestListModel.addElement("ID:" + req.studentId + " - " + req.studentName + " (" + req.lastMessage + ")");
        }
        
        // Also refresh chat area if a student is selected
        if (selectedStudentId != -1) {
            loadChatHistory(selectedStudentId);
        }
    }

    private void loadChatHistory(int studentId) {
        chatArea.setText("");
        List<String> history = chatManager.getActiveChatHistory(studentId);
        for (String msg : history) {
            chatArea.append(msg + "\n");
        }
    }

    private void sendReply() {
        String reply = replyField.getText();
        if (reply.isEmpty() || selectedStudentId == -1) return;

        // 1. Update in-memory collection
        chatManager.replyToStudent(selectedStudentId, reply);
        
        // 2. Persist to Database (JDBC)
        // new StudentDAO().saveChatLog(selectedStudentId, reply, "HUMAN");

        // 3. Update GUI
        chatArea.append("TEACHER: " + reply + "\n");
        replyField.setText("");
    }
}