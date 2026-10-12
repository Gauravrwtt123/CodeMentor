@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private StudentDAO studentDAO = new StudentDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Session Management
        HttpSession session = request.getSession();
        Integer studentId = (Integer) session.getAttribute("studentId");
        
        if (studentId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userMessage = request.getParameter("message");
        
        // 2. Core Java / Backend Logic Integration
        AsyncChatProcessor processor = new AsyncChatProcessor();
        Thread botThread = new Thread(processor);
        botThread.start();
        processor.enqueueMessage(userMessage);

        // 3. Save to Database
        // studentDAO.saveMessage(studentId, userMessage);

        // 4. Request Dispatcher to forward to JSP (View)
        request.setAttribute("botReply", "CodeBot is thinking...");
        RequestDispatcher dispatcher = request.getRequestDispatcher("chatroom.jsp");
        dispatcher.forward(request, response);
    }
}