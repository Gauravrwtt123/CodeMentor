
const mysql = require("mysql2/promise");
require("dotenv").config({ path: "code.env" });

const DB_CONFIG = {
    host: process.env.DB_HOST || "localhost",
    port: Number(process.env.DB_PORT) || 3306,
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    database: process.env.DB_NAME || "codementor",
    waitForConnections: true,
    connectionLimit: 10
};

const pool = mysql.createPool(DB_CONFIG);

// Server start par database + table auto-create (agar pehle se nahi hai)
async function ensureSchema() {
    const admin = await mysql.createConnection({
        host: DB_CONFIG.host,
        port: DB_CONFIG.port,
        user: DB_CONFIG.user,
        password: DB_CONFIG.password
    });

    try {
        await admin.query(
            "CREATE DATABASE IF NOT EXISTS `" + DB_CONFIG.database + "` " +
            "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
        );
    } finally {
        await admin.end();
    }

    // Pehle users table (FK parent), phir chat_logs — dono actual schema ke hisaab se
    await pool.query(
        "CREATE TABLE IF NOT EXISTS users (" +
        "id INT NOT NULL AUTO_INCREMENT, " +
        "name VARCHAR(100) NOT NULL, " +
        "email VARCHAR(150) NOT NULL UNIQUE, " +
        "password VARCHAR(255) NOT NULL, " +
        "streak_count INT DEFAULT 0, " +
        "target_topic VARCHAR(100), " +
        "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, " +
        "PRIMARY KEY (id)" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
    );

    await pool.query(
        "CREATE TABLE IF NOT EXISTS chat_logs (" +
        "id INT NOT NULL AUTO_INCREMENT, " +
        "user_id INT NOT NULL, " +
        "message TEXT NOT NULL, " +
        "response TEXT, " +
        "sender_type VARCHAR(20) DEFAULT 'AI', " +
        "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, " +
        "PRIMARY KEY (id), " +
        "KEY user_id (user_id), " +
        "CONSTRAINT chat_logs_ibfk_1 FOREIGN KEY (user_id) " +
        "REFERENCES users (id) ON DELETE CASCADE" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci"
    );
}

// Lightweight ping — health check ke liye
async function testConnection() {
    try {
        const [rows] = await pool.query("SELECT 1 AS ok");
        return rows[0].ok === 1;
    } catch (error) {
        return false;
    }
}

// Active user dhoondho jiske naam par chat log hona hai (login system abhi nahi hai)
// Pehle user ko use karta hai; users table khali ho toh null
async function getActiveUserId() {
    const [rows] = await pool.query(
        "SELECT id FROM users ORDER BY id ASC LIMIT 1"
    );
    return rows.length > 0 ? rows[0].id : null;
}

// Ek chat exchange (user message + AI reply) ko DB mein save karna
async function logChatExchange(userId, userMessage, aiReply) {
    await pool.execute(
        "INSERT INTO chat_logs (user_id, message, response) VALUES (?, ?, ?)",
        [userId, userMessage, aiReply]
    );
}

// Recent chat history wapas lena (purane se naye order mein)
async function getChatHistory(limit = 50) {
    const safeLimit = Math.max(1, Math.min(200, Number(limit) || 50));

    const [rows] = await pool.query(
        "SELECT id, user_id, message, response, sender_type, created_at " +
        "FROM chat_logs ORDER BY id DESC LIMIT " + safeLimit
    );

    return rows.reverse();
}

module.exports = {
    pool,
    ensureSchema,
    testConnection,
    getActiveUserId,
    logChatExchange,
    getChatHistory
};
