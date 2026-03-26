let questions = [];
let currentIdx = 0;
let answers = {}; // { question_id: selected_option_index }
let markedReview = new Set();
let timerSeconds = 3600;
let timerInterval;

document.addEventListener('DOMContentLoaded', async () => {
    const testId = sessionStorage.getItem('currentTestId');
    console.log('Exam Initialization: currentTestId =', testId);
    if (!testId) {
        console.error('Redirecting to dashboard: currentTestId is NULL');
        window.location.href = 'dashboard.html';
        return;
    }

    await loadQuestions(testId);
    startTimer();
});

async function loadQuestions(testId) {
    const token = localStorage.getItem('accessToken');
    try {
        const data = await Api.get(`/tests/${testId}/`, token);
        questions = data.questions;
        document.getElementById('exam-title').textContent = data.name;
        
        document.getElementById('loading').classList.add('hidden');
        document.getElementById('q-container').classList.remove('hidden');
        
        initPallet();
        renderQuestion();
        setupSections();
    } catch (e) {
        alert('Failed to load test: ' + e.message);
        window.location.href = 'dashboard.html';
    }
}

function initPallet() {
    const pallet = document.getElementById('pallet-grid');
    pallet.innerHTML = '';
    questions.forEach((q, i) => {
        const dot = document.createElement('div');
        dot.className = 'pallet-num';
        dot.id = `pallet-${i}`;
        dot.textContent = i + 1;
        dot.onclick = () => jumpTo(i);
        pallet.appendChild(dot);
    });
}

function renderQuestion() {
    const q = questions[currentIdx];
    document.getElementById('q-num').textContent = `Question ${currentIdx + 1}`;
    document.getElementById('q-text').textContent = q.question_text;
    document.getElementById('opt-0').textContent = q.option1;
    document.getElementById('opt-1').textContent = q.option2;
    document.getElementById('opt-2').textContent = q.option3;
    document.getElementById('opt-3').textContent = q.option4;

    // Highlight selected option
    const options = document.querySelectorAll('.option-item');
    options.forEach((opt, i) => {
        opt.classList.remove('selected');
        if (answers[q.id] === i) opt.classList.add('selected');
    });

    // Update pallet active
    document.querySelectorAll('.pallet-num').forEach(d => d.classList.remove('active'));
    document.getElementById(`pallet-${currentIdx}`).classList.add('active');

    // Update Section Tab
    updateSectionTabs(q.section);
}

function selectOption(index) {
    const q = questions[currentIdx];
    answers[q.id] = index;
    renderQuestion();
    updatePalletStatus();
}

function saveAndNext() {
    if (currentIdx < questions.length - 1) {
        currentIdx++;
        renderQuestion();
    } else {
        alert('This is the last question. You can click Submit Test to finish.');
    }
}

function prevQuestion() {
    if (currentIdx > 0) {
        currentIdx--;
        renderQuestion();
    }
}

function jumpTo(idx) {
    currentIdx = idx;
    renderQuestion();
}

function markForReview() {
    const q = questions[currentIdx];
    if (markedReview.has(q.id)) markedReview.delete(q.id);
    else markedReview.add(q.id);
    updatePalletStatus();
}

function clearResponse() {
    const q = questions[currentIdx];
    delete answers[q.id];
    renderQuestion();
    updatePalletStatus();
}

function updatePalletStatus() {
    questions.forEach((q, i) => {
        const dot = document.getElementById(`pallet-${i}`);
        dot.classList.remove('answered', 'marked');
        if (answers[q.id] !== undefined) dot.classList.add('answered');
        if (markedReview.has(q.id)) dot.classList.add('marked');
    });
    
    const count = Object.keys(answers).length;
    document.getElementById('pallet-status').textContent = `${count} Answered`;
}

function startTimer() {
    timerInterval = setInterval(() => {
        timerSeconds--;
        if (timerSeconds <= 0) {
            clearInterval(timerInterval);
            autoSubmit();
        }
        const m = Math.floor(timerSeconds / 60);
        const s = timerSeconds % 60;
        document.getElementById('timer').textContent = `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
        
        if (timerSeconds < 300) document.getElementById('timer').style.color = 'var(--error)';
    }, 1000);
}

async function submitTestManual() {
    if (confirm('Are you sure you want to submit the test?')) {
        await finalizeTest();
    }
}

async function autoSubmit() {
    alert('Time is up! Submitting your test...');
    await finalizeTest();
}

async function finalizeTest() {
    clearInterval(timerInterval);
    const token = localStorage.getItem('accessToken');
    const testId = sessionStorage.getItem('currentTestId');

    let score = 0;
    let correctCount = 0;
    let incorrectCount = 0;
    let engScore = 0;
    let quantScore = 0;
    let reasonScore = 0;
    let gkScore = 0;

    questions.forEach(q => {
        const userAnswer = answers[q.id];
        if (userAnswer !== undefined && userAnswer !== null && userAnswer !== -1) {
            if (userAnswer === q.correct_option) {
                score += 2;
                correctCount++;
                if (q.section === 'English') engScore += 2;
                else if (q.section === 'Quantitative') quantScore += 2;
                else if (q.section === 'Intelligence') reasonScore += 2;
                else if (q.section === 'Awareness') gkScore += 2;
            } else {
                incorrectCount++;
            }
        }
    });

    const payload = {
        test_id: testId,
        score: score,
        total_questions: questions.length,
        correct_count: correctCount,
        incorrect_count: incorrectCount,
        eng_score: engScore,
        quant_score: quantScore,
        reason_score: reasonScore,
        gk_score: gkScore,
        selected_answers: answers
    };

    try {
        const result = await Api.post('/tests/submit/', payload, token);
        sessionStorage.setItem('lastResult', JSON.stringify(result));
        window.location.href = 'result.html';
    } catch (e) {
        alert('Submission failed: ' + e.message);
    }
}

function setupSections() {
    const sections = [...new Set(questions.map(q => q.section))];
    const tabContainer = document.getElementById('section-tabs');
    tabContainer.innerHTML = '';
    
    sections.forEach(sec => {
        const tab = document.createElement('div');
        tab.className = 'section-tab';
        tab.id = `tab-${sec}`;
        tab.textContent = sec;
        tab.onclick = () => jumpToSection(sec);
        tabContainer.appendChild(tab);
    });
}

function jumpToSection(sectionName) {
    const firstIdx = questions.findIndex(q => q.section === sectionName);
    if (firstIdx !== -1) jumpTo(firstIdx);
}

function updateSectionTabs(currentSection) {
    document.querySelectorAll('.section-tab').forEach(t => {
        t.classList.remove('active');
        if (t.textContent === currentSection) t.classList.add('active');
    });
}
