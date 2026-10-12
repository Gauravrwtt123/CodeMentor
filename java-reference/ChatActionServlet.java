@WebServlet("/chatAction")
public class ChatActionServlet extends HttpServlet {
    private LiveChatManager chatManager = LiveChatManager.getInstance();
    private StudentDAO studentDAO = new StudentDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer studentId = (Integer) session.getAttribute("studentId");
        String studentName = (String) session.getAttribute("studentName");

        if ("request_human".equals(action)) {
            String lastMessage = request.getParameter("lastMessage");
            
            // 1. Add to thread-safe queue (Multithreading)
            chatManager.requestHumanTutor(studentId, studentName, lastMessage);
            
            // 2. Save to DB (JDBC)
            studentDAO.saveChatLog(studentId, lastMessage, "STUDENT");
            studentDAO.saveChatLog(studentId, "A human tutor has been notified and will join shortly.", "SYSTEM");

            request.setAttribute("status", "Human tutor requested! Please wait.");
            request.setAttribute("mode", "HUMAN_WAITING");
            request.getRequestDispatcher("chatroom.jsp").forward(request, response);
            
        } else if ("send_message".equals(action)) {
            String message = request.getParameter("message");
            String mode = request.getParameter("mode"); // "AI" or "HUMAN"

            studentDAO.saveChatLog(studentId, message, "STUDENT");

            if ("HUMAN".equals(mode)) {
                // Add to active chat history for teacher to see
                chatManager.getActiveChatHistory(studentId).add("STUDENT: " + message);
                request.setAttribute("botReply", "Message sent to your human tutor.");
            } else {
                // Fallback to existing AI logic
                // request.setAttribute("botReply", aiEngine.askAI(studentId, message).get());
            }
            request.getRequestDispatcher("chatroom.jsp").forward(request, response);
        }
    }
}