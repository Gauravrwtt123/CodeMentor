# DevMentor - Java Web Project

A Java Web application designed around the marking rubric.

## Rubric coverage

### Java GUI/Web concepts
- OOP: inheritance, polymorphism, interfaces, custom exception handling
- Collections & Generics: List, Map, Set, generic repository
- Multithreading & Synchronization: synchronized streak service + ExecutorService
- Database operation classes: UserDAO and QuestionDAO
- JDBC: DriverManager + PreparedStatement + ResultSet
- Servlets & Web Integration: LoginServlet, DashboardServlet, QuestionServlet, ChatServlet, LogoutServlet

## Run

1. Install JDK 17, Maven and MySQL 8.
2. Run `database/devmentor_db.sql`.
3. Edit `src/main/resources/db.properties`.
4. Run:
   `mvn clean package`
5. Deploy `target/devmentor.war` to Tomcat 10.1.
6. Open:
   `http://localhost:8080/devmentor/`

Demo:
- Email: demo@devmentor.com

## Important
This project uses Jakarta Servlet 6 and is intended for Tomcat 10.1+.
