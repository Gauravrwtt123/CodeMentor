@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    // Singleton instance of the AI Engine
    private AsyncAIChatEngine aiEngine = new AsyncAIChatEngine();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer studentId = (Integer) session.getAttribute("studentId");
        String userMessage = request.getParameter("message");

        // 1. Trigger AI in background thread
        CompletableFuture<String> futureResponse = aiEngine.askAI(studentId, userMessage);

        // 2. Wait for response (or use WebSockets/AJAX in a real production app, 
        // but for college Servlets, synchronous wait or redirect is standard)
        try {
            String botReply = futureResponse.get(); // Blocks until AI finishes
            
            // Save to Database (JDBC Rubric)
            StudentDAO dao = new StudentDAO();
            dao.saveChatLog(studentId, userMessage, botReply);

            // Forward to JSP
            request.setAttribute("botReply", botReply);
            request.setAttribute("userMsg", userMessage);
            request.getRequestDispatcher("chatroom.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("botReply", "The AI tutor is currently busy. Please try again.");
            request.getRequestDispatcher("chatroom.jsp").forward(request, response);
        }
    }
}