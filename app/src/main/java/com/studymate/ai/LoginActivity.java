package com.studymate.ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.studymate.ai.database.DatabaseHelper;
import com.studymate.ai.models.User;
import com.studymate.ai.utils.PasswordUtils;
import com.studymate.ai.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Admin shortcut: email == "admin" and password == "admin"
        if ("admin".equals(email) && "admin".equals(password)) {
            boolean ok = db.ensureAdminUser();
            User admin = db.getUserByEmail("admin");
            if (ok && admin != null) {
                session.setLogin(true, admin.getId());
                Toast.makeText(this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
                return;
            } else {
                Toast.makeText(this, "Failed to create admin user", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        User u = db.getUserByEmail(email);
        if (u == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashed = PasswordUtils.sha256(password);
        if (hashed.equals(u.getPasswordHash())) {
            session.setLogin(true, u.getId());
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
        }
    }
}
