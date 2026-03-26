document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isAuthenticated()) window.location.href = 'index.html';
    
    const user = Auth.getUser();
    Auth.updateUserUI(user);

    await loadStudyContent();
});

async function loadStudyContent() {
    const container = document.getElementById('subjects-container');
    const token = localStorage.getItem('accessToken');

    try {
        const subjects = await Api.get('/study/subjects/', token);
        if (!subjects || subjects.length === 0) {
            container.innerHTML = '<p class="text-muted" style="text-align: center; grid-column: 1/-1;">No study content available at the moment.</p>';
            return;
        }

        container.innerHTML = '';
        subjects.forEach(subject => {
            const completedCount = subject.topics.filter(t => t.is_completed).length;
            const totalCount = subject.topics.length;
            const progressPercent = totalCount > 0 ? (completedCount / totalCount) * 100 : 0;

            const card = document.createElement('div');
            card.className = 'glass subject-card animate-fade-in';
            
            // Map icon names to emojis or icons if needed
            const iconMap = {
                'math': '📐',
                'english': '📚',
                'intelligence': '🧠',
                'awareness': '🌍'
            };
            const icon = iconMap[subject.icon_name.toLowerCase()] || '📖';

            card.innerHTML = `
                <div class="subject-icon">${icon}</div>
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                    <h2 style="color: white; margin-bottom: 0;">${subject.name}</h2>
                    <span class="text-muted" style="font-size: 0.85rem; font-weight: 700;">${completedCount}/${totalCount} Topics</span>
                </div>
                <div class="progress-container">
                    <div class="progress-bar" style="width: ${progressPercent}%"></div>
                </div>
                <div class="topic-list">
                    ${subject.topics.map(topic => `
                        <div class="topic-item ${topic.is_completed ? 'completed' : ''}">
                            <div class="topic-checkbox" onclick="event.stopPropagation(); toggleTopic(${topic.id}, this.parentElement, ${subject.id})"></div>
                            <span style="flex: 1; font-weight: 600; font-size: 0.95rem; cursor: pointer;" 
                                  onclick="location.href='topic-concept.html?id=${topic.id}&subject=${encodeURIComponent(subject.name)}'">
                                ${topic.name}
                            </span>
                        </div>
                    `).join('')}
                </div>
            `;
            container.appendChild(card);
        });
    } catch (err) {
        container.innerHTML = `<div class="glass" style="grid-column: 1/-1; padding: 40px; text-align: center; border-color: rgba(244, 63, 94, 0.2);">
            <p style="color: var(--accent); font-weight: 700;">Connection Error: ${err.message}</p>
        </div>`;
    }
}

async function toggleTopic(topicId, element, subjectId) {
    const token = localStorage.getItem('accessToken');
    const isCurrentlyCompleted = element.classList.contains('completed');
    const newState = !isCurrentlyCompleted;

    // Optimistic UI update
    element.classList.toggle('completed', newState);
    
    // Update local progress bar and counter immediately
    const card = element.closest('.subject-card');
    const counter = card.querySelector('.text-muted');
    const progressBar = card.querySelector('.progress-bar');
    
    let [completed, total] = counter.textContent.split(' ')[0].split('/').map(Number);
    completed = newState ? completed + 1 : completed - 1;
    counter.textContent = `${completed}/${total} Topics`;
    progressBar.style.width = `${(completed / total) * 100}%`;

    try {
        await Api.post('/study/progress/', {
            topic_id: topicId,
            is_completed: newState
        }, token);
    } catch (err) {
        console.error('Failed to update progress:', err);
        // Revert on error
        element.classList.toggle('completed', isCurrentlyCompleted);
        completed = isCurrentlyCompleted ? completed + 1 : completed - 1;
        counter.textContent = `${completed}/${total} Topics`;
        progressBar.style.width = `${(completed / total) * 100}%`;
        alert('Data Sync Failed: ' + err.message);
    }
}
