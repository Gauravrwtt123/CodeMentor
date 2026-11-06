package com.codementor.web;
import com.codementor.dao.UserDAO; import jakarta.servlet.annotation.WebServlet; import jakarta.servlet.http.*; import jakarta.servlet.*;
import java.io.IOException; import java.sql.SQLException;
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req,res);}
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
  String name=req.getParameter("name"),email=req.getParameter("email"),password=req.getParameter("password");
  if(name==null||name.isBlank()||email==null||email.isBlank()||password==null||password.length()<8){req.setAttribute("error","Enter name/email and a password of at least 8 characters.");doGet(req,res);return;}
  try{var user=new UserDAO().register(name,email,password);req.getSession(true).setAttribute("user",user);res.sendRedirect(req.getContextPath()+"/dashboard");}
  catch(SQLException e){req.setAttribute("error","Registration failed. Email may already be registered.");doGet(req,res);}
 }
}
