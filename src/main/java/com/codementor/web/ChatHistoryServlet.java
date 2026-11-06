package com.codementor.web;
import com.codementor.dao.ChatDAO;import com.codementor.model.User;import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;import java.sql.SQLException;
@WebServlet("/history")
public class ChatHistoryServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  User user=(User)req.getSession(false).getAttribute("user");
  try{req.setAttribute("history",new ChatDAO().findRecentByUser(user.getId(),30));req.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(req,res);}
  catch(SQLException e){throw new ServletException("Unable to load chat history",e);}
 }
}
