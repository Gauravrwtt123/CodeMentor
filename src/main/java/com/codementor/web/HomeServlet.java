package com.codementor.web;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/") public class HomeServlet extends HttpServlet{
 protected void doGet(HttpServletRequest req,HttpServletResponse res)throws IOException{res.sendRedirect(req.getContextPath()+"/login");}
}
