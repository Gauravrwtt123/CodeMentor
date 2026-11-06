package com.codementor.web;
import com.codementor.dao.UserDAO; import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*;
import java.io.IOException; import java.sql.SQLException;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req,res);}
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  try{var user=new UserDAO().authenticate(req.getParameter("email"),req.getParameter("password"));
   if(user==null){req.setAttribute("error","Invalid email or password.");doGet(req,res);return;}
   HttpSession oldSession=req.getSession(false);if(oldSession!=null)oldSession.invalidate();req.getSession(true).setAttribute("user",user);res.sendRedirect(req.getContextPath()+"/dashboard");
  }catch(SQLException e){throw new ServletException("Database error during login",e);}
 }
}
