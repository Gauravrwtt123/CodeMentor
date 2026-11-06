<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html><html><head><meta charset="utf-8"><title>CodeMentor history</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
<body><main class="wide"><header><h1>Recent conversations</h1><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></header>
<c:choose><c:when test="${empty history}"><p>No saved conversations yet.</p></c:when><c:otherwise><c:forEach var="item" items="${history}">
<section class="card"><p><strong>You:</strong> <c:out value="${item.prompt}"/></p><p><strong>Mentor:</strong> <c:out value="${item.response}"/></p><small><c:out value="${item.createdAt}"/></small></section>
</c:forEach></c:otherwise></c:choose></main></body></html>
