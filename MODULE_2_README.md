# Module 2 — Dashboard

This commit implements Module 2 (Dashboard) for the StudyMate AI app and was pushed to the initial-skeleton branch.

What I added
- Student dashboard view (dashboard_student.xml): greeting, study goal progress bar, 4 stat cards (subjects, avg quiz %, notes, streak).
- Admin dashboard view (dashboard_admin.xml): global stats (users, notes, quizzes, questions) and action buttons for Manage Users, Add Question, View Questions, Reset Data.
- Updated activity_dashboard.xml to include both student and admin dashboards and a BottomNavigationView (menu in res/menu/menu_bottom_nav.xml).
- DashboardActivity.java: loads current user, switches between student/admin dashboards, and populates stats from DatabaseHelper.
- AdminActivity placeholder (activity_admin.xml + AdminActivity.java) to avoid runtime errors when admin clicks Manage Users.
- DatabaseHelper: added helper methods used by the dashboard (counts, averages, resetAllData, getUserById).
- Strings and menu resources updated.

How to run and test Module 2 locally
1. Pull the latest branch:
   - git checkout initial-skeleton
   - git pull origin initial-skeleton
2. Open the project in Android Studio and let Gradle sync.
3. Run the app on API 21+ emulator or device.
4. Test flows:
   - Login as admin (email: "admin", password: "admin") — you'll see the Admin dashboard with global stats (likely zero until you add data).
   - Register a new user and login — you'll see the Student dashboard with stats computed from the local DB (empty initially).
   - Use the bottom navigation to attempt to open Subjects, AI Chat, Progress or Settings — if those Activities are not yet implemented you'll see a "Feature not implemented yet" toast instead of a crash.

Notes
- The Dashboard relies on several helper methods in DatabaseHelper; these are implemented as simple SQL aggregate queries.
- Reset All Data runs a resetAllData() helper which triggers an onUpgrade-style reset. Use cautiously.

What's next
- I can implement Module 3 (Subjects Manager) next: RecyclerView with add/edit/delete, color & emoji picker, and subject progress handling. Say "Go ahead — Module 3" and I will implement and push it.
