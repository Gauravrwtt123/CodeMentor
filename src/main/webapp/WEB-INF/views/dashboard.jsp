<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.codementor.model.User" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<% User user=(User)session.getAttribute("user"); if(user==null){response.sendRedirect(request.getContextPath()+"/login");return;} %>
<!doctype html><html><head><meta charset="utf-8"><title>CodeMentor Dashboard</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body><main class="wide">
<header><div><h1>CodeMentor</h1><p>Welcome, <c:out value="<%=user.getName()%>"/>!</p></div>
<div><a href="${pageContext.request.contextPath}/history">Chat history</a>
<form method="post" action="${pageContext.request.contextPath}/logout"><button class="secondary">Logout</button></form></div></header>
<section class="card"><h2>Ask your coding mentor</h2><p>Ask about Java, DSA, arrays, JDBC, or programming concepts.</p>
<form method="post" action="${pageContext.request.contextPath}/chat"><textarea name="message" rows="4" maxlength="4000" placeholder="Explain Java inheritance with an example..." required></textarea><button>Ask Mentor</button></form>
<c:if test="${not empty error}"><p class="error"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty question}"><h3>Your question</h3><p><c:out value="${question}"/></p><h3>Mentor answer</h3><p class="answer"><c:out value="${answer}"/></p></c:if>
</section><p class="note">Java Servlet MVC + JDBC + MySQL educational project.</p></main></body></html>
