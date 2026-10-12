
const express = require("express");
const cors = require("cors");
require("dotenv").config({ path: "code.env" });
const {
    ensureSchema,
    testConnection,
    getActiveUserId,
    logChatExchange,
    getChatHistory
} = require("./db");

const app = express();

// Middleware
app.use(cors());
app.use(express.json({ limit: "20kb" }));

// Configuration
const PORT = process.env.PORT || 3000;
const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const GEMINI_MODEL = process.env.GEMINI_MODEL || "gemini-3.8-flash";

// AI tutor instructions
const SYSTEM_PROMPT = `
You are CodeMentor, a friendly AI coding tutor.

Your responsibilities:
1. Explain Java, DSA, SQL, OOP, and programming concepts simply.
2. Use beginner-friendly Hinglish when the user asks in Hinglish.
3. Explain code step by step when requested.
4. Give correct, readable code examples.
5. Explain time and space complexity for DSA problems when relevant.
6. Help students understand concepts instead of only giving answers.
7. If the question is unclear, ask a short clarifying question.
8. Never claim that code was executed or tested unless it actually was.
`;

// Health check endpoint (AI + DB dono ka status)
app.get("/api/health", async (req, res) => {
    const dbConnected = await testConnection();

    res.json({
        success: true,
        message: "CodeMentor backend is running",
        aiConfigured: Boolean(process.env.GEMINI_API_KEY),
        dbConnected: dbConnected
    });
});

// Chat history endpoint (DB se recent messages)
app.get("/api/history", async (req, res) => {
    try {
        const history = await getChatHistory(req.query.limit);
        res.json({
            success: true,
            history: history
        });
    } catch (error) {
        console.error("History error:", error.message);
        res.status(503).json({
            success: false,
            error: "Database is not connected. Please check your DB settings."
        });
    }
});

// Gemini API call with retry — 503/429 spikes usually temporary hote hain
async function callGeminiWithRetry(apiUrl, payload, attempts = 3) {
    let lastStatus = 0;
    let lastResult = {};

    for (let attempt = 1; attempt <= attempts; attempt++) {
        const apiResponse = await fetch(apiUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "x-goog-api-key": GEMINI_API_KEY
            },
            body: JSON.stringify(payload),
            signal: AbortSignal.timeout(30000)
        });

        if (apiResponse.ok) {
            return {
                ok: true,
                status: apiResponse.status,
                result: await apiResponse.json()
            };
        }

        lastStatus = apiResponse.status;
        lastResult = await apiResponse.json().catch(() => ({}));

        // Sirf temporary errors (503 high demand / 429 rate limit) par retry
        const retryable = apiResponse.status === 503 || apiResponse.status === 429;
        if (!retryable || attempt === attempts) {
            break;
        }

        const waitMs = attempt * 2000;
        console.warn(
            "Gemini " + lastStatus + " (attempt " + attempt + "/" + attempts +
            ") — " + (waitMs / 1000) + " sec baad retry kar raha hoon..."
        );
        await new Promise(resolve => setTimeout(resolve, waitMs));
    }

    return { ok: false, status: lastStatus, result: lastResult };
}

// AI chat endpoint
app.post("/api/chat", async (req, res) => {
    try {
        const { message } = req.body || {};

        // Validate message
        if (typeof message !== "string" || !message.trim()) {
            return res.status(400).json({
                success: false,
                error: "Please enter a message."
            });
        }

        if (message.length > 10000) {
            return res.status(400).json({
                success: false,
                error: "Message is too long. Please keep it under 10000 characters."
            });
        }

        // Check API key
        if (!GEMINI_API_KEY) {
            return res.status(503).json({
                success: false,
                error: "AI is not configured. Please check the GEMINI_API_KEY in your .env file."
            });
        }

        // Build API URL without template-literal syntax issues
        const apiUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/" +
            GEMINI_MODEL +
            ":generateContent";

        // Send request to Gemini (503/429 par automatic retry ke saath)
        const payload = {
            systemInstruction: {
                parts: [
                    { text: SYSTEM_PROMPT }
                ]
            },
            contents: [
                {
                    role: "user",
                    parts: [
                        { text: message.trim() }
                    ]
                }
            ],
            generationConfig: {
                temperature: 0.7,
                maxOutputTokens: 2048
            }
        };

        const apiCall = await callGeminiWithRetry(apiUrl, payload);

        // Handle Gemini API errors
        if (!apiCall.ok) {
            console.error(
                "Gemini API error:",
                apiCall.status,
                apiCall.result.error?.message || "Unknown API error"
            );

            if (apiCall.status === 503 || apiCall.status === 429) {
                return res.status(503).json({
                    success: false,
                    error: "AI service par abhi bahut load hai. 2-3 minute baad dobara try karo."
                });
            }

            return res.status(502).json({
                success: false,
                error: "The AI service could not answer right now. Please try again."
            });
        }

        const result = apiCall.result;

        // Extract AI response
        const reply = result.candidates?.[0]?.content?.parts
            ?.map(part => part.text || "")
            .join("")
            .trim();

        if (!reply) {
            return res.status(502).json({
                success: false,
                error: "The AI returned an empty response. Please try again."
            });
        }

        // Send response to frontend
        // Chat ko DB mein save karna (best-effort — DB down ho to chat phir bhi chale)
        try {
            const userId = await getActiveUserId();

            if (userId) {
                await logChatExchange(userId, message.trim(), reply);
            } else {
                console.log("Chat log skip: users table khali hai (koi user nahi).");
            }
        } catch (dbError) {
            console.error("DB log error:", dbError.message);
        }

        return res.json({
            success: true,
            reply: reply,
            source: "ai"
        });

    } catch (error) {
        console.error("Chat error:", error.message);

        if (error.name === "TimeoutError" || error.name === "AbortError") {
            return res.status(504).json({
                success: false,
                error: "The AI request timed out. Please try again."
            });
        }

        return res.status(500).json({
            success: false,
            error: "Something went wrong on the server. Please try again."
        });
    }
});

// Handle unknown API routes
app.use("/api", (req, res) => {
    res.status(404).json({
        success: false,
        error: "API endpoint not found."
    });
});

// Start server
const server = app.listen(PORT, async () => {
    console.log(`CodeMentor backend running at http://localhost:${PORT}`);
    console.log(`Health check: http://localhost:${PORT}/api/health`);
    console.log(`Gemini AI configured: ${Boolean(GEMINI_API_KEY)}`);

    // Database connect + schema auto-create
    try {
        await ensureSchema();
        const dbConnected = await testConnection();
        console.log(`MySQL connected: ${dbConnected} (${process.env.DB_NAME || "codementor"})`);

        if (!dbConnected) {
            console.error("⚠️  DB connect nahi hua. code.env mein DB_PASSWORD check karo.");
        }
    } catch (error) {
        console.error("❌ Database connection failed:", error.message);
        console.error("   code.env mein DB_HOST / DB_USER / DB_PASSWORD / DB_NAME check karo.");
    }
});

// Port already in use ka clear error dikhana
server.on("error", (error) => {
    if (error.code === "EADDRINUSE") {
        console.error(`\n❌ Port ${PORT} pehle se busy hai (server pehle se chal raha hai).`);
        console.error(`Purana server band karke dobara chalao:`);
        console.error(`   netstat -ano | findstr :${PORT}`);
        console.error(`   taskkill /F /PID <upar wala PID>\n`);
        process.exit(1);
    }
    throw error;
});

