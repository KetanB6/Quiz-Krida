# QuizKrida: Online Quiz System

A backend-driven quiz platform designed with modular architecture and service separation in mind.

The system was initially designed as a single application with three functional modules:
1. Quiz Creation  
2. Quiz Gameplay (Play by Quiz ID)  
3. AI-based Quiz Generation  

As the system evolved, the AI-based quiz generation module was separated into an independent service to improve:
- architecture clarity  
- cost efficiency  
- scalability  
- maintainability  
- system stability  

This resulted in a clean, service-oriented design where core business logic and AI functionality are handled independently.

---

## System Modules

### Module 1 — Create Quiz (Core System)
- Database-backed quiz creation  
- Persistent storage  
- REST API based design  

### Module 2 — Play Quiz (Core System)
- Play quizzes using Quiz ID  
- DB-driven quiz retrieval  
- Stateless gameplay APIs  

### Module 3 — AI Quiz Generation (Separated Service)
- Originally part of the core system  
- Later extracted into an independent service  
- No database dependency  
- Uses external AI APIs  
- Designed as a stateless microservice  

---

## Architecture

### Core System — `QuizKrida: online-quiz-system`
- Handles:
  - Quiz creation  
  - Quiz storage  
  - Quiz gameplay  
- Database-driven  
- Focused on core business logic  
- Stable and production-oriented backend design  

---

### AI Service — `quizbyai-service` (Independent Microservice)
- Handles:
  - AI-based quiz generation  
- Stateless architecture  
- No database dependency  
- External AI API integration  
- Designed for:
  - cost control  
  - independent scaling  
  - loose coupling  
  - service isolation  

---

## System Design Flow

```text
Frontend
   ├── online-quiz-system  → Core quiz logic (DB-based)
   └── quizbyai-service    → AI quiz generation (Stateless service)
