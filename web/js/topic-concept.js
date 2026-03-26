document.addEventListener('DOMContentLoaded', async () => {
    if (!Auth.isAuthenticated()) window.location.href = 'index.html';
    
    const user = Auth.getUser();
    Auth.updateUserUI(user);

    const urlParams = new URLSearchParams(window.location.search);
    const topicId = urlParams.get('id');
    const subjectName = urlParams.get('subject');

    if (!topicId) {
        window.location.href = 'study-zone.html';
        return;
    }

    if (subjectName) {
        document.getElementById('subject-name').textContent = subjectName;
    }

    await loadTopicDetails(topicId);
});

async function loadTopicDetails(topicId) {
    const token = localStorage.getItem('accessToken');
    const theoryEl = document.getElementById('theory-content');
    const formulasEl = document.getElementById('formulas-content');
    const examplesContainer = document.getElementById('examples-container');
    const topicTitleEl = document.getElementById('topic-title');

    try {
        // Since there is no single topic detail endpoint for users, 
        // we find it in the subjects list or we could implement one.
        // For now, let's fetch subjects and find the topic.
        const subjects = await Api.get('/study/subjects/', token);
        let foundTopic = null;

        for (const subject of subjects) {
            foundTopic = subject.topics.find(t => t.id == topicId);
            if (foundTopic) break;
        }

        if (!foundTopic) {
            theoryEl.textContent = "Error: Topic intelligence not found.";
            return;
        }

        // Display Data
        topicTitleEl.textContent = foundTopic.name;
        theoryEl.textContent = foundTopic.theory || "Theory content is being prepared for this topic.";
        formulasEl.textContent = foundTopic.formulas || "No specific formulas listed for this topic.";

        examplesContainer.innerHTML = '';
        if (foundTopic.examples && foundTopic.examples.length > 0) {
            foundTopic.examples.forEach(example => {
                const block = document.createElement('div');
                block.className = 'example-block';
                block.innerHTML = `
                    <div class="example-question">${example.question}</div>
                    <div class="example-solution">
                        <strong>Solution:</strong><br>
                        ${example.solution}
                    </div>
                `;
                examplesContainer.appendChild(block);
            });
        } else {
            examplesContainer.innerHTML = '<p class="text-muted">Example problems will be added soon.</p>';
        }

    } catch (err) {
        console.error('Failed to load topic details:', err);
        theoryEl.innerHTML = `<span style="color: var(--accent)">Telemetry Failure: ${err.message}</span>`;
    }
}
