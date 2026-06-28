package com.studymate.ai.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.studymate.ai.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "studymate.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");

        // users
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT, last_name TEXT, email TEXT UNIQUE, password_hash TEXT, grade TEXT, is_admin INTEGER DEFAULT 0, created_at DATETIME DEFAULT CURRENT_TIMESTAMP);");

        // subjects
        db.execSQL("CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, subject_name TEXT, subject_icon TEXT, subject_color TEXT, progress INTEGER DEFAULT 0, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE);");

        // notes
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, subject_id INTEGER, title TEXT, content TEXT, tag TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");

        // quiz_questions
        db.execSQL("CREATE TABLE quiz_questions (id INTEGER PRIMARY KEY AUTOINCREMENT, subject_id INTEGER, question TEXT, option_a TEXT, option_b TEXT, option_c TEXT, option_d TEXT, correct_answer TEXT, explanation TEXT, difficulty TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");

        // quiz_attempts
        db.execSQL("CREATE TABLE quiz_attempts (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, subject_id INTEGER, score INTEGER, total INTEGER, duration_seconds INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");

        // study_sessions
        db.execSQL("CREATE TABLE study_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, subject_id INTEGER, session_date TEXT, duration_minutes INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE, FOREIGN KEY(subject_id) REFERENCES subjects(id) ON DELETE CASCADE);");

        // chat_history
        db.execSQL("CREATE TABLE chat_history (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, message TEXT, is_user INTEGER, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE);");

        Log.d(TAG, "Database created with tables");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // simple upgrade policy: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS chat_history;");
        db.execSQL("DROP TABLE IF EXISTS study_sessions;");
        db.execSQL("DROP TABLE IF EXISTS quiz_attempts;");
        db.execSQL("DROP TABLE IF EXISTS quiz_questions;");
        db.execSQL("DROP TABLE IF EXISTS notes;");
        db.execSQL("DROP TABLE IF EXISTS subjects;");
        db.execSQL("DROP TABLE IF EXISTS users;");
        onCreate(db);
    }

    // ---------------------
    // Convenience CRUD helpers for Module 1 (users)
    // ---------------------

    public long addUser(String firstName, String lastName, String email, String passwordHash, String grade, int isAdmin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("first_name", firstName);
        cv.put("last_name", lastName);
        cv.put("email", email);
        cv.put("password_hash", passwordHash);
        cv.put("grade", grade);
        cv.put("is_admin", isAdmin);
        try {
            return db.insertOrThrow("users", null, cv);
        } catch (SQLiteConstraintException ex) {
            // unique constraint failed (duplicate email)
            Log.w(TAG, "addUser: duplicate email: " + email);
            return -2;
        } catch (SQLException ex) {
            Log.e(TAG, "addUser: failed", ex);
            return -1;
        }
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT id, first_name, last_name, email, password_hash, grade, is_admin, created_at FROM users WHERE email = ? LIMIT 1", new String[]{email});
            if (c != null && c.moveToFirst()) {
                User u = new User();
                u.setId(c.getLong(c.getColumnIndexOrThrow("id")));
                u.setFirstName(c.getString(c.getColumnIndexOrThrow("first_name")));
                u.setLastName(c.getString(c.getColumnIndexOrThrow("last_name")));
                u.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
                u.setPasswordHash(c.getString(c.getColumnIndexOrThrow("password_hash")));
                u.setGrade(c.getString(c.getColumnIndexOrThrow("grade")));
                u.setIsAdmin(c.getInt(c.getColumnIndexOrThrow("is_admin")));
                u.setCreatedAt(c.getString(c.getColumnIndexOrThrow("created_at")));
                return u;
            }
        } catch (Exception ex) {
            Log.e(TAG, "getUserByEmail", ex);
        } finally {
            if (c != null) c.close();
        }
        return null;
    }

    public boolean ensureAdminUser() {
        // Ensure a user with email 'admin' exists and is marked as admin. Password will be 'admin' hashed.
        try {
            User u = getUserByEmail("admin");
            if (u != null) return true;
            String defaultHash = com.studymate.ai.utils.PasswordUtils.sha256("admin");
            long id = addUser("Admin", "", "admin", defaultHash, "", 1);
            return id > 0;
        } catch (Exception ex) {
            Log.e(TAG, "ensureAdminUser", ex);
            return false;
        }
    }
}
