package com.example.sqliteproject;

/**
 * Student entity representing a student record in the database
 */
public class Student {
    private int id;
    private String name;

    // Default constructor for database operations
    public Student() {}

    // Constructor for creating new students (without ID)
    public Student(String name) {
        this.name = name;
    }

    // Constructor for existing students (with ID from database)
    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "'}";
    }
}