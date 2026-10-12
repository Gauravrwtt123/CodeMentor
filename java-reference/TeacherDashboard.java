public class TeacherDashboard extends JFrame {
    private JTextArea chatMonitorArea;
    private JButton refreshBtn;

    public TeacherDashboard() {
        setTitle("CodeMentor - Teacher Admin Panel");
        setSize(600, 400);
        setLayout(new BorderLayout());

        chatMonitorArea = new JTextArea();
        add(new JScrollPane(chatMonitorArea), BorderLayout.CENTER);

        refreshBtn = new JButton("Sync Live Chats");
        refreshBtn.addActionListener(e -> loadLiveChats());
        add(refreshBtn, BorderLayout.SOUTH);
    }

    private void loadLiveChats() {
        // Fetching data using JDBC DAO
        StudentDAO dao = new StudentDAO();
        List<String> logs = dao.getChatHistory(1); // Example ID
        chatMonitorArea.setText(String.join("\n", logs));
    }
}