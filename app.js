
const messages = document.getElementById("messages");
const form = document.getElementById("chatForm");
const promptBox = document.getElementById("prompt");
const sendBtn = document.getElementById("sendBtn");

// Message ko chat screen par show karna
function addMessage(text, who) {
    const row = document.createElement("div");
    row.className = `message ${who}`;

    if (who === "assistant") {
        const icon = document.createElement("div");
        icon.className = "bot-icon";
        icon.textContent = "✦";
        row.appendChild(icon);
    }

    const bubble = document.createElement("div");
    bubble.className = "bubble";
    bubble.textContent = text;

    row.appendChild(bubble);
    messages.appendChild(row);

    messages.scrollTop = messages.scrollHeight;
}

// Backend se real AI response lena
async function getBackendReply(message) {
    const response = await fetch("http://localhost:3000/api/chat", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            message: message
        })
    });

    const data = await response.json();

    if (!response.ok || !data.success) {
        throw new Error(data.error || "AI request failed");
    }

    return data.reply;
}

// User ka message bhejna
async function sendMessage(text) {
    const value = (text ?? promptBox.value).trim();

    if (!value) {
        return;
    }

    addMessage(value, "user");
    promptBox.value = "";
    sendBtn.disabled = true;

    try {
        const reply = await getBackendReply(value);

        if (reply) {
            addMessage(reply, "assistant");
        } else {
            addMessage(
                "AI se empty response mila. Please dobara try karo.",
                "assistant"
            );
        }

    } catch (error) {
        console.error("AI Backend Error:", error);

        addMessage(
            "AI connection failed: " + error.message +
            ". Check karo ki backend running hai.",
            "assistant"
        );

    } finally {
        sendBtn.disabled = false;
        promptBox.focus();
    }
}

// Send button aur form submit
form.addEventListener("submit", function (event) {
    event.preventDefault();
    sendMessage();
});

// Enter se message send karna
promptBox.addEventListener("keydown", function (event) {
    if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
});

// Suggested prompt buttons
document.querySelectorAll("[data-prompt]").forEach(function (button) {
    button.addEventListener("click", function () {
        sendMessage(button.dataset.prompt);
    });
});

// New Chat button
document.getElementById("newChat").addEventListener("click", function () {
    messages.innerHTML = "";

    addMessage(
        "New chat started! Ask me about Java, DSA, SQL, or any programming topic.",
        "assistant"
    );
});

// Theme toggle
document.getElementById("themeToggle").addEventListener("click", function () {
    document.body.classList.toggle("dark");
});

// Sidebar navigation
document.querySelectorAll(".nav-item").forEach(function (button) {
    button.addEventListener("click", function () {
        document.querySelectorAll(".nav-item").forEach(function (item) {
            item.classList.remove("active");
        });

        button.classList.add("active");

        const view = button.dataset.view;

        if (view === "roadmap") {
            addMessage(
                "Java learning roadmap: Java fundamentals → Arrays → Sorting → Hashing → Two Pointers → Sliding Window → Linked Lists → Stack and Queue → Trees.",
                "assistant"
            );
        } else if (view === "progress") {
            addMessage(
                "Progress tracker abhi demo section hai. Isme progress save karne ke liye database aur login functionality add karni hogi.",
                "assistant"
            );
        } else {
            addMessage(
                "Welcome back to CodeMentor AI Coding Tutor! Java, DSA, SQL ya kisi bhi programming topic ke baare mein poochho.",
                "assistant"
            );
        }
    });
});

