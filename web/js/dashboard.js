document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    // Set User Info
    const email = localStorage.getItem('userEmail');
    if (email) {
        document.getElementById('welcome-msg').textContent = `Forge on, ${email.split('@')[0]}!`;
        document.getElementById('user-avatar').textContent = email[0].toUpperCase();
    }

    await loadStats();
    await loadMockTests();
});

async function loadStats() {
    const token = localStorage.getItem('accessToken');
    try {
        // We'll use the existing streak and progress endpoints if they exist, 
        // or just mock for now if we haven't mapped them all yet.
        // For CHSL, we usually fetch user profile or dashboard stats.
        // Let's check history to see total tests
        const history = await Api.get('/test-history/', token);
        document.getElementById('stat-tests').textContent = history.length;
        
        if (history.length > 0) {
            const total = history.reduce((sum, item) => sum + (item.score || 0), 0);
            document.getElementById('stat-avg').textContent = Math.round(total / history.length) + '%';
        }

        const streakData = await Api.get('/streak/', token);
        document.getElementById('stat-streak').textContent = streakData.current_streak + ' Days';
    } catch (e) {
        console.error('Failed to load stats', e);
    }
}

async function loadMockTests() {
    const token = localStorage.getItem('accessToken');
    const testList = document.getElementById('test-list');
    const isPremium = localStorage.getItem('isPremium') === 'true';

    try {
        const tests = await Api.get('/mock-tests/', token);
        testList.innerHTML = '';

        tests.forEach(test => {
            const card = document.createElement('div');
            card.className = 'glass test-card animate-fade-in';
            
            const isLocked = !test.is_free && !isPremium && !test.is_unlocked_manually;
            const badgeClass = test.is_free ? 'badge-free' : 'badge-locked';
            const badgeText = test.is_free ? 'Free' : 'Premium';

            card.innerHTML = `
                <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 20px;">
                    <span class="badge ${badgeClass}">${badgeText}</span>
                    <span style="font-size: 0.8rem; color: var(--text-muted); font-weight: 600;">#${test.order}</span>
                </div>
                <h4 style="font-size: 1.25rem; font-weight: 800; margin-bottom: 12px; height: 3em; overflow: hidden;">${test.name}</h4>
                <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 24px;">
                    ${test.question_count} Questions • 60 Minutes
                </p>
                <button class="btn-primary" style="width: 100%; ${isLocked ? 'filter: grayscale(1); opacity: 0.7;' : ''}" 
                        onclick="startTest(${test.id}, ${isLocked})">
                    ${isLocked ? 'Locked' : 'Start Test'}
                </button>
            `;
            testList.appendChild(card);
        });
    } catch (e) {
        testList.innerHTML = '<p style="color: var(--error);">Failed to load mock tests. Please try again.</p>';
    }
}

function startTest(testId, isLocked) {
    if (isLocked) {
        alert('This test is locked! Please upgrade to premium or ask admin to unlock it.');
        return;
    }
    // Store selected test and navigate
    sessionStorage.setItem('currentTestId', testId);
    window.location.href = 'exam-mode.html';
}
