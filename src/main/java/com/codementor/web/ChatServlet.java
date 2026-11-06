package com.codementor.web;

import com.codementor.dao.ChatDAO;
import com.codementor.model.User;
import com.codementor.service.MentorService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.*;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private MentorService service;

    @Override
    public void init() { service = new MentorService(); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = (User) req.getSession(false).getAttribute("user");
        String prompt = req.getParameter("message");

        if (prompt == null || prompt.isBlank() || prompt.length() > 4000) {
            req.setAttribute("error", "Enter a question between 1 and 4000 characters.");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);
            return;
        }

        prompt = prompt.trim();
        try {
            String answer = service.answerAsync(prompt).get(10, TimeUnit.SECONDS);
            new ChatDAO().save(user.getId(), prompt, answer);
            req.setAttribute("question", prompt);
            req.setAttribute("answer", answer);
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, res);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException("Mentor request interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new ServletException("Mentor service failed", e);
        } catch (SQLException e) {
            throw new ServletException("Could not save chat to MySQL", e);
        }
    }

    @Override
    public void destroy() { if (service != null) service.close(); }
}
