package com.example.sqliteproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite database helper implementing singleton pattern for thread-safe database access
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database configuration
    private static final String DATABASE_NAME = "student_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_STUDENTS = "students";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    private static DatabaseHelper instance;

    /**
     * Thread-safe singleton instance getter
     * @param context Application context
     * @return DatabaseHelper instance
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Private constructor to enforce singleton pattern
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple upgrade strategy: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    /**
     * Adds a new student to the database
     * @param student Student object to add
     * @return ID of the inserted student
     */
    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, student.getName());
            return db.insert(TABLE_STUDENTS, null, values);
        } finally {
            // Keep connection open for better performance
            // db.close(); // Removed for performance
        }
    }

    /**
     * Retrieves a student by ID
     * @param id Student ID to search for
     * @return Student object or null if not found
     */
    public Student getStudent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENTS,
                    new String[]{COLUMN_ID, COLUMN_NAME},
                    COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return new Student(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                );
            }
            return null;
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
    }

    /**
     * Retrieves all students from the database
     * @return List of all students, ordered by name
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_STUDENTS, null, null, null, null, null, COLUMN_NAME);
            if (cursor.moveToFirst()) {
                do {
                    Student student = new Student(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    );
                    students.add(student);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return students;
    }

    /**
     * Updates an existing student's information
     * @param student Student object with updated data
     * @return Number of rows affected
     */
    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, student.getName());

        int rowsAffected = db.update(TABLE_STUDENTS, values,
                COLUMN_ID + "=?", new String[]{String.valueOf(student.getId())});
        db.close();
        return rowsAffected;
    }

    /**
     * Deletes a student by ID
     * @param id Student ID to delete
     * @return true if student was deleted, false otherwise
     */
    public boolean deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_STUDENTS,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }

    /**
     * Gets the total count of students in the database
     * @return Number of students
     */
    public int getStudentCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_STUDENTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }
}