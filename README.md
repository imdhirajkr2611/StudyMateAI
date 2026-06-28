# StudyMate AI

StudyMate AI — Smart Learning Partner

This repository contains the Android (Java) project scaffold for StudyMate AI as agreed in the conversation. The initial-skeleton branch contains a minimal working Android Studio project structure, key Activities, Database helper skeleton, utilities and resource placeholders so you can open the project in Android Studio Flamingo and continue development.

See the project package: com.studymate.ai

What I added in this commit:
- Project Gradle files (root + app module)
- AndroidManifest
- Core Activities: Splash, Login, Register, Dashboard (skeleton implementations for all Activities will be added incrementally)
- DatabaseHelper skeleton with table creation SQL for all 7 tables
- Utilities: SessionManager, PasswordUtils (SHA-256)
- Models: User, Subject, Note, QuizQuestion (basic POJOs)
- Layouts: activity_splash, activity_login, activity_register, activity_dashboard (simple placeholders)
- Values: colors, strings, themes, dimens
- README, LICENSE (MIT), .gitignore

Next steps I will:
- Add the rest of Activities, Adapters, full layouts, drawables, and the offline AI engine in follow-up commits.

Open in Android Studio:
1. Clone the repo
2. Checkout branch `initial-skeleton`
3. Open the project in Android Studio Flamingo

