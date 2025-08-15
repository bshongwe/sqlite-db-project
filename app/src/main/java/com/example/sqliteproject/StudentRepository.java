package com.example.sqliteproject;

import android.content.Context;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository pattern implementation for student data access
 * Handles all database operations on background threads
 */
public class StudentRepository {
    private DatabaseHelper dbHelper;
    private ExecutorService executor;

    public StudentRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        executor = Executors.newFixedThreadPool(2); // Optimized for mobile
    }

    /**
     * Callback interface for asynchronous database operations
     * @param <T> Type of result expected
     */
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception error);
    }

    /**
     * Adds a student asynchronously
     * @param student Student to add
     * @param callback Callback with the new student ID
     */
    public void addStudentAsync(Student student, DatabaseCallback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = dbHelper.addStudent(student);
                callback.onSuccess(id);
            } catch (android.database.SQLException e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Retrieves a student by ID asynchronously
     * @param id Student ID to search for
     * @param callback Callback with the student or null
     */
    public void getStudentAsync(int id, DatabaseCallback<Student> callback) {
        executor.execute(() -> {
            try {
                Student student = dbHelper.getStudent(id);
                callback.onSuccess(student);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Retrieves all students asynchronously
     * @param callback Callback with list of all students
     */
    public void getAllStudentsAsync(DatabaseCallback<List<Student>> callback) {
        executor.execute(() -> {
            try {
                List<Student> students = dbHelper.getAllStudents();
                callback.onSuccess(students);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Updates a student asynchronously
     * @param student Student with updated information
     * @param callback Callback with number of rows affected
     */
    public void updateStudentAsync(Student student, DatabaseCallback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rowsAffected = dbHelper.updateStudent(student);
                callback.onSuccess(rowsAffected);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Deletes a student asynchronously
     * @param id Student ID to delete
     * @param callback Callback with deletion success status
     */
    public void deleteStudentAsync(int id, DatabaseCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                boolean deleted = dbHelper.deleteStudent(id);
                callback.onSuccess(deleted);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Shuts down the executor service to prevent memory leaks
     * Should be called when the repository is no longer needed
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}