package com.example.yourjob;

public class User {
    public String id;
    public String email;
    public String role; // "personal", "business", "skipped", "pending"
    public String name;
    public String city;
    public String age;
    public String field;
    public boolean isVerified;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.role = "pending";
        this.name = "";
        this.city = "";
        this.age = "";
        this.field = "";
        this.isVerified = false;
    }
}
