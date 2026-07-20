package com.studymate.ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.studymate.ai.database.DatabaseHelper;
import com.studymate.ai.models.User;
import com.studymate.ai.utils.SessionManager;

import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private SessionManager session;
    private User currentUser;

    private View studentLayout;
    private View adminLayout;

    // Student views
    private TextView tvGreeting;
    private ProgressBar pbStudyGoal;
    private TextView tvSubjectsCount, tvNotesCount, tvQuizScore, tvStreak;

    // Admin views
    private TextView tvTotalUsers, tvTotalNotes, tvTotalQuizzes, tvTotalQuestions;
    private Button btnManageUsers, btnAddQuestion, btnViewQuestions, btnResetData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            // not logged in -> go to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        long userId = session.getUserId();
        currentUser = db.getUserById(userId);
        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            session.clear();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        studentLayout = findViewById(R.id.layout_student);
        adminLayout = findViewById(R.id.layout_admin);

        // student widgets
        tvGreeting = findViewById(R.id.tvGreeting);
        pbStudyGoal = findViewById(R.id.pbStudyGoal);
        tvSubjectsCount = findViewById(R.id.tvSubjectsCount);
        tvNotesCount = findViewById(R.id.tvNotesCount);
        tvQuizScore = findViewById(R.id.tvQuizScore);
        tvStreak = findViewById(R.id.tvStreak);

        // admin widgets
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalNotes = findViewById(R.id.tvTotalNotes);
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);

        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnViewQuestions = findViewById(R.id.btnViewQuestions);
        btnResetData = findViewById(R.id.btnResetData);

        // bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // already here
                return true;
            } else if (id == R.id.nav_subjects) {
                safeStartActivity(SubjectActivity.class);
                return true;
            } else if (id == R.id.nav_chat) {
                safeStartActivity(ChatbotActivity.class);
                return true;
            } else if (id == R.id.nav_progress) {
                safeStartActivity(ProgressActivity.class);
                return true;
            } else if (id == R.id.nav_settings) {
                safeStartActivity(SettingsActivity.class);
                return true;
            }
            return false;
        });

        if (currentUser.getIsAdmin() == 1) {
            showAdminDashboard();
        } else {
            showStudentDashboard();
        }
    }

    private void showStudentDashboard() {
        studentLayout.setVisibility(View.VISIBLE);
        adminLayout.setVisibility(View.GONE);

        tvGreeting.setText(getGreeting());

        int subjects = db.getSubjectCountForUser(currentUser.getId());
        int notes = db.getNoteCountForUser(currentUser.getId());
        double avgScore = db.getAverageQuizScoreForUser(currentUser.getId());
        int streak = db.getCurrentStreakForUser(currentUser.getId());

        tvSubjectsCount.setText(String.valueOf(subjects));
        tvNotesCount.setText(String.valueOf(notes));
        tvQuizScore.setText(String.format("%.0f%%", avgScore));
        tvStreak.setText(String.valueOf(streak));

        // Study goal progress is average of subject progress (0-100)
        int progress = db.getAverageSubjectProgressForUser(currentUser.getId());
        pbStudyGoal.setProgress(progress);
    }

    private void showAdminDashboard() {
        studentLayout.setVisibility(View.GONE);
        adminLayout.setVisibility(View.VISIBLE);

        int totalUsers = db.getTotalUsers();
        int totalNotes = db.getTotalNotes();
        int totalQuizzes = db.getTotalQuizAttempts();
        int totalQuestions = db.getTotalQuestions();

        tvTotalUsers.setText(String.valueOf(totalUsers));
        tvTotalNotes.setText(String.valueOf(totalNotes));
        tvTotalQuizzes.setText(String.valueOf(totalQuizzes));
        tvTotalQuestions.setText(String.valueOf(totalQuestions));

        btnManageUsers.setOnClickListener(v -> safeStartActivity(AdminActivity.class));
        btnAddQuestion.setOnClickListener(v -> safeStartActivity(AddQuestionActivity.class));
        btnViewQuestions.setOnClickListener(v -> safeStartActivity(ViewQuestionsActivity.class));
        btnResetData.setOnClickListener(v -> {
            // simple confirmation dialog could be added; for now, try to call DB reset if implemented
            boolean ok = db.resetAllData();
            if (ok) Toast.makeText(this, "All data reset", Toast.LENGTH_SHORT).show();
            else Toast.makeText(this, "Reset not implemented", Toast.LENGTH_SHORT).show();
        });
    }

    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }

    private void safeStartActivity(Class<?> cls) {
        try {
            startActivity(new Intent(this, cls));
        } catch (Exception ex) {
            Toast.makeText(this, "Feature not implemented yet", Toast.LENGTH_SHORT).show();
        }
    }
}
