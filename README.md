# CodeMentor — Java Servlet + JDBC Web Application

CodeMentor is an educational coding-mentor web app implemented with **Java 17, Jakarta Servlets 6, JSP, JDBC, MySQL, Maven, and Tomcat 10.1**. The Java web backend is the main application. `legacy-ui/` is retained as a reference to the earlier Node/Express UI and is not the Java app entry point.

## Architecture

```text
Browser (JSP / HTML / CSS)
        |
        v
Jakarta Servlet Controller (Login/Register/Dashboard/Chat/History)
        |
        v
Service layer (Core Java, interface-based polymorphism, ExecutorService)
        |
        v
DAO layer (JDBC + PreparedStatement + try-with-resources)
        |
        v
MySQL (users, chat_logs)
```

- **MVC:** JSP views, Servlet controllers, model/DAO/service classes.
- **Core Java:** encapsulation in model classes, `Mentor` interface and implementation, exception handling, collections, and `ExecutorService`.
- **JDBC:** `DBConnection`, `UserDAO`, `ChatDAO`; parameterized SQL and CRUD-style create/read operations.
- **Servlets:** registration, login, logout, dashboard, chat, chat-history routes; session authentication filter.
- **Security basics:** BCrypt password hashes, PreparedStatement, session invalidation at login/logout, JSP `c:out` escaping for user text.

## Requirements

- JDK 17+
- Maven 3.8+
- MySQL 8+
- Apache Tomcat 10.1 (Jakarta Servlet 6)

## Setup (Windows + MySQL Workbench + Tomcat 10.1)

### 1. Start MySQL and create the schema

Open MySQL Workbench, connect to your local MySQL server, open `database/devmentor.sql`, and click the lightning bolt (Execute). This creates the `codementor` database and the `users` and `chat_logs` tables.

Alternatively, in PowerShell (replace `YOUR_MYSQL_PASSWORD` with your MySQL root password):

```powershell
cd path\to\CodeMentor
mysql -u root -pYOUR_MYSQL_PASSWORD < database\devmentor.sql
```

If `mysql` is not recognized, use MySQL Workbench or add MySQL's `bin` folder to PATH.

### 2. Configure the JDBC connection

Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties`. Open the new file and set your real MySQL password:

```properties
db.url=jdbc:mysql://localhost:3306/codementor?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
db.username=root
db.password=YOUR_MYSQL_PASSWORD
```

The application also supports environment variables `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; environment variables override `db.properties`. Never upload your real database password to GitHub.

### 3. Build and deploy

Run these commands from the project root (the folder containing `pom.xml`):

```powershell
mvn clean package
```

Copy `target\codementor.war` into Tomcat 10.1's `webapps` folder and start Tomcat. Open `http://localhost:8080/codementor/`.

### 4. Verify the JDBC connection

Open `http://localhost:8080/codementor/db-test`. A successful connection displays:

`SUCCESS: CodeMentor connected to MySQL database 'codementor'.`

If it fails, verify that MySQL service `MySQL80` is running, the database was created, and the username/password in `db.properties` are correct. The endpoint does not expose database credentials.

### 5. Test database-backed features

1. Register an account. The user record should appear in `codementor.users` (password is stored as a BCrypt hash).
2. Log in.
3. Send a message to the mentor. The prompt and response should be saved in `codementor.chat_logs`.
4. Open Chat History. It reads the current user's stored records.

## Routes

| Route | Method | Purpose |
|---|---|---|
| `/login` | GET/POST | Login |
| `/register` | GET/POST | Register |
| `/dashboard` | GET | Authenticated dashboard |
| `/chat` | POST | Generate mentor answer and persist it |
| `/history` | GET | Read logged-in user's saved chat history |
| `/logout` | POST | Invalidate session |

## Note about the mentor response

The current Java backend uses a small rule-based mentor implementation so it runs without an external API key. To restore Gemini/other AI responses, implement the `Mentor` interface with an API-backed provider and configure the provider secret through environment variables. The previous Node.js AI endpoint is not automatically invoked by this Java WAR.

## Viva explanation

- **Servlet** receives the HTTP request and forwards/redirects the response.
- **JSP** renders the view.
- **Service** contains application logic.
- **DAO** isolates SQL/database logic.
- **JDBC** connects Java to MySQL.
- **PreparedStatement** binds values safely and helps prevent SQL injection.
- **BCrypt** stores password hashes rather than plaintext passwords.
- **ExecutorService** executes mentor response work asynchronously.
