package com.studymate.ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.studymate.ai.database.DatabaseHelper;
import com.studymate.ai.utils.PasswordUtils;
import com.studymate.ai.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etGrade;
    private Button btnCreate;
    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmailReg);
        etPassword = findViewById(R.id.etPasswordReg);
        etGrade = findViewById(R.id.etGrade);
        btnCreate = findViewById(R.id.btnCreate);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        btnCreate.setOnClickListener(v -> attemptRegister());
    }

    private boolean isPasswordStrong(String pw) {
        if (pw == null) return false;
        if (pw.length() < 6) return false;
        boolean hasLetter = false, hasDigit = false;
        for (char c : pw.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    private void attemptRegister() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String grade = etGrade.getText().toString().trim();

        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Password must be at least 6 characters and include letters and numbers", Toast.LENGTH_LONG).show();
            return;
        }

        // check duplicate email
        if (db.getUserByEmail(email) != null) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashed = PasswordUtils.sha256(password);
        long id = db.addUser(first, last, email, hashed, grade, 0);
        if (id > 0) {
            session.setLogin(true, id);
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
            finish();
        } else if (id == -2) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}
