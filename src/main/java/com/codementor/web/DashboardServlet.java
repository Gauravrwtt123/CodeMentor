package com.codementor.web;
import com.codementor.model.User; import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*; import java.io.IOException;
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  User user=(User)req.getSession().getAttribute("user"); if(user==null){res.sendRedirect(req.getContextPath()+"/login");return;}
  req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req,res);
 }
}
