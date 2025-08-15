package com.example.sqliteproject;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

/**
 * Main activity demonstrating the improved SQLite database implementation
 * Shows proper usage of repository pattern with async operations
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private StudentRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize repository with singleton database helper
        repository = new StudentRepository(this);
        performDatabaseOperations();
    }

    /**
     * Demonstrates database operations using the repository pattern
     */
    private void performDatabaseOperations() {
        // Add students asynchronously to prevent UI blocking
        addStudent("Alice", true);
        addStudent("Bob", false);
    }

    private void addStudent(String name, boolean loadAllAfter) {
        repository.addStudentAsync(new Student(name), new StudentRepository.DatabaseCallback<Long>() {
            @Override
            public void onSuccess(Long id) {
                Log.d(TAG, "Student added with ID: " + id);
                if (loadAllAfter) loadAllStudents();
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error adding student", error);
            }
        });
    }

    /**
     * Loads and displays all students from the database
     */
    private void loadAllStudents() {
        repository.getAllStudentsAsync(new StudentRepository.DatabaseCallback<List<Student>>() {
            @Override
            public void onSuccess(List<Student> students) {
                Log.d(TAG, "Retrieved " + students.size() + " students:");
                for (Student student : students) {
                    Log.d(TAG, student.toString());
                }
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error loading students", error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources to prevent memory leaks
        if (repository != null) {
            repository.shutdown();
        }
    }
}