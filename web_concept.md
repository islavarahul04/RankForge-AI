# RankForge AI: Web Concept & Technology Overview

RankForge AI's web platform is designed as a high-performance, premium extension of the mobile ecosystem, providing students with a robust environment for intensive exam preparation and administrators with powerful management tools.

## 🌟 Core Concept: "Forging Success"

The website centers around the mission to **democratize elite coaching** using AI. It transitions the mobile experience to a larger format optimized for:

- **Cognitive Deep-Dives**: Detailed analysis of strengths and weaknesses that are easier to digest on a desktop screen.
- **Exam Simulation**: A high-fidelity "Exam Mode" that mimics the actual computer-based testing (CBT) environment of major competitive exams.
- **Personalized Growth**: Using AI to predict success paths and suggest specific study zones based on performance data.

---

## 🛠️ Technical Stack

The web architecture is built for speed, clean aesthetics, and seamless synchronization with the Android application.

### Frontend: Premium & Lightweight
- **Core**: Vanilla HTML5, CSS3, and JavaScript (ES6+). By avoiding heavy frameworks (like React/Angular), the site remains exceptionally fast and responsive.
- **Typography**: [Outfit](https://fonts.google.com/specimen/Outfit) via Google Fonts—a modern, geometric sans-serif that reflects the AI-driven nature of the brand.
- **Design Language**: 
    - **Glassmorphism**: Uses backdrop filters and translucent layers to create a "premium" feel.
    - **Dynamic UI**: Custom CSS animations (`animate-fade-in`) and interactive state transitions.
- **Communication**: Uses the Native **Fetch API** to communicate with the backend via JSON.

### Backend: Robust & Scalable
- **Engine**: [Django](https://www.djangoproject.com/) (Python). A high-level framework that ensures security and rapid development.
- **API**: A unified RESTful API that serves **both** the Android app and the Web portal, ensuring 100% data consistency.
- **Database**: **SQLite**. A reliable, serverless database engine used for storing user profiles, mock test metadata, and performance history.
- **Admin Portal**: A dedicated secure layer for managing:
    - User subscriptions and tiers.
    - Mock test creation and question banks.
    - Global notifications and support tickets.

---

## 🔄 Integration Strategy

RankForge AI uses a **Decoupled Architecture**:
1. **Single Source of Truth**: All data resides in the Django backend.
2. **Cross-Platform Sync**: Whether a user takes a test on their phone or the web, their "Success Predictor" and "Study Zone" update instantly across all devices.
3. **Platform Optimization**: The web version includes a specialized "Admin Portal" (located at `/admin/`) not present in the standard user app, allowing for real-time content management.
