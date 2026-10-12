// Temporary test script — DB connection verify karne ke liye
// Run: node test_db.js
const {
    pool,
    ensureSchema,
    testConnection,
    getActiveUserId,
    logChatExchange,
    getChatHistory
} = require("./db");

(async () => {
    try {
        await ensureSchema();
        console.log("Schema OK (database + users + chat_logs ready)");
    } catch (error) {
        console.error("Schema/Connection FAILED:", error.message);
        process.exit(1);
    }

    console.log("Ping:", (await testConnection()) ? "OK" : "FAIL");

    const userId = await getActiveUserId();
    console.log("Active user id:", userId);

    if (!userId) {
        console.error("FAIL: users table khali hai — insert possible nahi.");
        process.exit(1);
    }

    await logChatExchange(userId, "test message (test_db.js)", "test reply (test_db.js)");
    console.log("Insert OK");

    const history = await getChatHistory(5);
    console.log("History:", JSON.stringify(history, null, 2));

    // Test rows hatao taaki demo data saaf rahe
    const [clean] = await pool.query(
        "DELETE FROM chat_logs WHERE message = 'test message (test_db.js)'"
    );
    console.log("Test rows cleaned:", clean.affectedRows);

    process.exit(0);
})().catch((error) => {
    console.error("TEST FAILED:", error.message);
    process.exit(1);
});
