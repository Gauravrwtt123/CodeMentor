package com.codementor.web;

import com.codementor.util.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/** Diagnostic endpoint: verifies the app can open a JDBC connection. */
@WebServlet("/db-test")
public class DatabaseTestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try {
            if (DBConnection.testConnection()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println("SUCCESS: CodeMentor connected to MySQL database 'codementor'.");
            } else {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().println("FAILED: MySQL connection is not valid.");
            }
        } catch (SQLException | ExceptionInInitializerError e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getWriter().println("FAILED: Cannot connect to MySQL.");
            response.getWriter().println("Check MySQL service, database name, db.properties / DB_* environment variables, and credentials.");
            getServletContext().log("CodeMentor JDBC connection test failed", e);
        }
    }
}
