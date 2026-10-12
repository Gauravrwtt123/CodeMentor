import javax.swing.*;
import java.awt.*;

public class StudentDesktopApp extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private AsyncAIChatEngine aiEngine = new AsyncAIChatEngine();
    private int currentStudentId = 1; // Mock logged-in student

    public StudentDesktopApp() {
        setTitle("CodeMentor AI - Desktop Client");
        setSize(600, 500);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(40);
        JButton sendBtn = new JButton("Ask AI Tutor");
        
        sendBtn.addActionListener(e -> sendMessage());
        inputPanel.add(inputField);
        inputPanel.add(sendBtn);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void sendMessage() {
        String msg = inputField.getText();
        if (msg.isEmpty()) return;

        chatArea.append("You: " + msg + "\n");
        inputField.setText("");
        chatArea.append("AI Tutor is thinking...\n");

        // 1. Multithreading: Run AI call on a background thread to keep GUI responsive
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // This runs in the background thread
                return aiEngine.askAI(currentStudentId, msg).get();
            }

            @Override
            protected void done() {
                // This runs back on the Event Dispatch Thread (EDT) to update GUI safely
                try {
                    String reply = get();
                    // Remove "thinking" text and append actual reply
                    chatArea.setText(chatArea.getText().replace("AI Tutor is thinking...\n", ""));
                    chatArea.append("AI Tutor: " + reply + "\n\n");
                } catch (Exception ex) {
                    chatArea.append("Error: " + ex.getMessage() + "\n");
                }
            }
        }.execute();
    }
}