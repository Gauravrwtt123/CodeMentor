package com.codementor.web;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/logout") public class LogoutServlet extends HttpServlet{
 protected void doPost(HttpServletRequest req,HttpServletResponse res)throws IOException{HttpSession s=req.getSession(false);if(s!=null)s.invalidate();res.sendRedirect(req.getContextPath()+"/login");}
}
