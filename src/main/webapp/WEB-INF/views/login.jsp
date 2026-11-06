<%@ page contentType="text/html;charset=UTF-8" %>\n<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html><head><meta charset="utf-8"><title>CodeMentor Login</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head><body>
<main class="card"><h1>CodeMentor</h1><p>Sign in to your coding mentor.</p>
<% if(request.getAttribute("error")!=null){ %><p class="error"><c:out value="${error}"/></p><% } %>
<form method="post" action="${pageContext.request.contextPath}/login"><label>Email</label><input type="email" name="email" required><label>Password</label><input type="password" name="password" required><button>Login</button></form>
<p>New here? <a href="${pageContext.request.contextPath}/register">Create account</a></p></main></body></html>
