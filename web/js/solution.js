let allQuestions = [];
let filteredQuestions = [];
let userAnswers = {};
let currentIdx = 0;
let showOnlyMistakes = true;

document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const testId = urlParams.get('test_id');
    const attemptId = urlParams.get('attempt_id');

    if (!testId) {
        window.location.href = 'history.html';
        return;
    }

    await loadData(testId, attemptId);
});

async function loadData(testId, attemptId) {
    const token = localStorage.getItem('accessToken');
    try {
        // 1. Load Test Questions
        const testData = await Api.get(`/tests/${testId}/`, token);
        allQuestions = testData.questions;

        // 2. Load Attempt Results (to get user answers)
        const history = await Api.get('/tests/history/', token);
        const attempt = history.find(h => h.id == attemptId) || history.find(h => h.test_id == testId);
        
        if (attempt) {
            userAnswers = attempt.selected_answers || {};
            document.getElementById('status-chip').textContent = `Score: ${attempt.score} / ${attempt.total_questions * 2}`;
        }

        prepareFilteredList();
        
        document.getElementById('loading').classList.add('hidden');
        document.getElementById('q-container').classList.remove('hidden');
        
        renderQuestion();
        renderPallet();
    } catch (e) {
        console.error('Failed to load solution data:', e);
        alert('Failed to load solution data: ' + e.message);
        window.location.href = 'history.html';
    }
}

function prepareFilteredList() {
    if (showOnlyMistakes) {
        filteredQuestions = allQuestions.filter((q, idx) => {
            const userAnswer = userAnswers[q.id];
            return userAnswer === undefined || userAnswer !== q.correct_option;
        });
        
        if (filteredQuestions.length === 0) {
            alert('Perfect score! No mistakes to show. Switching to "Show All" mode.');
            showOnlyMistakes = false;
            filteredQuestions = allQuestions;
            document.getElementById('filter-btn').textContent = 'Show Mistakes Only';
            document.getElementById('filter-text').textContent = 'Showing all questions';
        }
    } else {
        filteredQuestions = allQuestions;
    }
}

function renderQuestion() {
    if (filteredQuestions.length === 0) return;
    
    const q = filteredQuestions[currentIdx];
    const originalIdx = allQuestions.indexOf(q);
    
    document.getElementById('q-num').textContent = `Question ${originalIdx + 1}`;
    document.getElementById('q-text').textContent = q.question_text;
    document.getElementById('opt-0').textContent = q.option1;
    document.getElementById('opt-1').textContent = q.option2;
    document.getElementById('opt-2').textContent = q.option3;
    document.getElementById('opt-3').textContent = q.option4;

    const userAnswer = userAnswers[q.id];
    const correctOption = q.correct_option;

    // Reset styles
    for (let i = 0; i < 4; i++) {
        const container = document.getElementById(`opt-container-${i}`);
        container.classList.remove('correct', 'wrong');
    }

    const statusText = document.getElementById('q-status-text');
    statusText.classList.remove('correct', 'incorrect');

    if (userAnswer === correctOption) {
        document.getElementById(`opt-container-${correctOption}`).classList.add('correct');
        statusText.textContent = 'Correct';
        statusText.classList.add('correct');
    } else {
        // Mark correct one in green
        document.getElementById(`opt-container-${correctOption}`).classList.add('correct');
        // If user answered, mark their wrong choice in red
        if (userAnswer !== undefined && userAnswer !== -1) {
            document.getElementById(`opt-container-${userAnswer}`).classList.add('wrong');
            statusText.textContent = 'Incorrect';
            statusText.classList.add('incorrect');
        } else {
            statusText.textContent = 'Unanswered';
            statusText.classList.add('incorrect');
        }
    }

    // Footer button visibility
    document.getElementById('next-btn').textContent = (currentIdx === filteredQuestions.length - 1) ? 'Finish Review' : 'Next Problem →';
}

function renderPallet() {
    const pallet = document.getElementById('pallet-grid');
    pallet.innerHTML = '';
    
    filteredQuestions.forEach((q, i) => {
        const originalIdx = allQuestions.indexOf(q);
        const div = document.createElement('div');
        div.className = 'pallet-num';
        if (i === currentIdx) div.classList.add('active');
        
        const userAnswer = userAnswers[q.id];
        if (userAnswer === q.correct_option) div.classList.add('correct');
        else div.classList.add('incorrect');

        div.textContent = originalIdx + 1;
        div.onclick = () => {
            currentIdx = i;
            renderQuestion();
            renderPallet();
        };
        pallet.appendChild(div);
    });

    document.getElementById('pallet-status').textContent = `${filteredQuestions.length} Questions`;
}

function next() {
    if (currentIdx < filteredQuestions.length - 1) {
        currentIdx++;
        renderQuestion();
        renderPallet();
    } else {
        location.href = 'history.html';
    }
}

function prev() {
    if (currentIdx > 0) {
        currentIdx--;
        renderQuestion();
        renderPallet();
    }
}

function toggleFilter() {
    showOnlyMistakes = !showOnlyMistakes;
    currentIdx = 0;
    document.getElementById('filter-btn').textContent = showOnlyMistakes ? 'Show All Questions' : 'Show Mistakes Only';
    document.getElementById('filter-text').textContent = showOnlyMistakes ? 'Filtering for incorrect questions' : 'Showing all questions';
    prepareFilteredList();
    renderQuestion();
    renderPallet();
}
