package com.codementor.web;
import com.codementor.model.User; import jakarta.servlet.*; import jakarta.servlet.annotation.WebFilter; import jakarta.servlet.http.*; import java.io.IOException;
/** Central route guard for session-protected pages and actions. */
@WebFilter(urlPatterns={"/dashboard","/chat","/history"})
public class AuthenticationFilter implements Filter {
 public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws IOException,ServletException{
  HttpServletRequest req=(HttpServletRequest)request;HttpServletResponse res=(HttpServletResponse)response;
  HttpSession session=req.getSession(false);User user=session==null?null:(User)session.getAttribute("user");
  if(user==null){res.sendRedirect(req.getContextPath()+"/login");return;}chain.doFilter(request,response);
 }
}
