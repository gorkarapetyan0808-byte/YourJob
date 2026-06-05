package com.example.yourjob;

public class User {
    public String id;
    public String email;
<<<<<<< HEAD
    public String phone;
    public String role;
=======
    public String role; // "personal", "business", "skipped", "pending"
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    public String name;
    public String city;
    public String age;
    public String field;
    public boolean isVerified;
<<<<<<< HEAD
    public boolean isPhoneVerified;

    public User() {
=======

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
<<<<<<< HEAD
        this.phone = "";
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        this.role = "pending";
        this.name = "";
        this.city = "";
        this.age = "";
        this.field = "";
        this.isVerified = false;
<<<<<<< HEAD
        this.isPhoneVerified = false;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }
}
