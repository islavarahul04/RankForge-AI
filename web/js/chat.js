document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isAuthenticated()) window.location.href = 'index.html';
    
    const user = Auth.getUser();
    Auth.updateUserUI(user);

    // Personalize greeting
    const greetingEl = document.getElementById('ai-greeting');
    if (greetingEl && user) {
        const firstName = user.full_name ? user.full_name.split(' ')[0] : 'Initiate';
        greetingEl.textContent = `Hello ${firstName}! I am RankForge AI. How can I assist your SSC CHSL preparation today?`;
    }

    await loadChatHistory();
});

const messagesContainer = document.getElementById('chat-messages');
const userInput = document.getElementById('user-input');
const sendBtn = document.getElementById('send-btn');
const chatForm = document.getElementById('chat-form');

async function loadChatHistory() {
    const token = localStorage.getItem('accessToken');
    try {
        const history = await Api.get('/ai/chat/', token);
        if (history && history.length > 0) {
            messagesContainer.innerHTML = '';
            history.forEach(msg => appendMessage(msg.message, msg.is_user));
        }
    } catch (err) {
        console.error('History fetch failed:', err);
    }
}

function appendMessage(text, isUser) {
    const div = document.createElement('div');
    div.className = `message ${isUser ? 'user' : 'ai'}`;
    
    if (isUser) {
        div.textContent = text;
    } else {
        // Use marked for AI messages to support rich formatting if available
        if (typeof marked !== 'undefined' && marked.parse) {
            div.innerHTML = marked.parse(text);
        } else {
            // Fallback for line breaks if marked is not available
            div.textContent = text;
        }
    }
    
    messagesContainer.appendChild(div);
    scrollToBottom();
}

function scrollToBottom() {
    setTimeout(() => {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }, 10);
}

// Voice Assistant (Web Speech API)
const micBtn = document.getElementById('mic-btn');
if (micBtn && ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window)) {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;
    recognition.lang = 'en-IN';

    recognition.onstart = () => {
        micBtn.classList.add('pulse-animation');
        micBtn.style.color = 'var(--accent)';
        userInput.placeholder = "Listening...";
    };

    recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        userInput.value = transcript;
        sendMessage();
    };

    recognition.onerror = () => {
        stopRecognition();
    };

    recognition.onend = () => {
        stopRecognition();
    };

    function stopRecognition() {
        micBtn.classList.remove('pulse-animation');
        micBtn.style.color = 'white';
        userInput.placeholder = "Query the neural network...";
    }

    micBtn.onclick = () => {
        try {
            recognition.start();
        } catch (e) {
            recognition.stop();
        }
    };
}

window.sendQuickAction = function(text) {
    userInput.value = text;
    sendMessage();
};

async function sendMessage() {
    const message = userInput.value.trim();
    if (!message) return;

    // Cleanup initial quick actions on first user message if they exist
    const initialActions = document.getElementById('quick-actions');
    if (initialActions) initialActions.style.display = 'none';

    userInput.value = '';
    userInput.disabled = true;
    sendBtn.disabled = true;

    appendMessage(message, true);

    // Show typing indicator
    const typingDiv = document.createElement('div');
    typingDiv.className = 'message ai typing-indicator';
    typingDiv.innerHTML = '<div class="dot"></div><div class="dot"></div><div class="dot"></div>';
    messagesContainer.appendChild(typingDiv);
    scrollToBottom();

    try {
        const response = await Api.post('/ai/chat/', { message }, localStorage.getItem('accessToken'));
        if (typingDiv.parentNode) messagesContainer.removeChild(typingDiv);
        appendMessage(response.message, false);
    } catch (err) {
        if (typingDiv.parentNode) messagesContainer.removeChild(typingDiv);
        if (err.message && err.message.includes('limit_exhausted')) {
            showLimitModal();
        } else {
            appendMessage('Transmission Error: ' + err.message, false);
        }
    } finally {
        userInput.disabled = false;
        sendBtn.disabled = false;
        userInput.focus();
    }
}

function showLimitModal() {
    let overlay = document.getElementById('limit-modal');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'limit-modal';
        overlay.className = 'modal-overlay';
        overlay.innerHTML = `
            <div class="modal-content">
                <div class="modal-icon">⭐</div>
                <h2 class="text-gradient" style="font-size: 1.8rem; margin-bottom: 12px;">Limit Exhausted</h2>
                <p class="text-muted" style="margin-bottom: 32px;">You've used your 20 free neural queries for today. Upgrade to Elite for unlimited access to the RankForge intelligence network.</p>
                <button class="btn-primary" style="width: 100%; margin-bottom: 16px;" onclick="location.href='profile.html'">Upgrade to Elite</button>
                <button style="background: none; border: none; color: var(--text-muted); cursor: pointer; font-weight: 600;" onclick="document.getElementById('limit-modal').classList.remove('active')">Analyze Later</button>
            </div>
        `;
        document.body.appendChild(overlay);
    }
    setTimeout(() => overlay.classList.add('active'), 10);
}

if (chatForm) {
    chatForm.onsubmit = (e) => {
        e.preventDefault();
        sendMessage();
    };
} else {
    sendBtn.onclick = sendMessage;
    userInput.onkeypress = (e) => {
        if (e.key === 'Enter') sendMessage();
    };
}
