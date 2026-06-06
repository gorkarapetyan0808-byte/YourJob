package com.example.yourjob;

public class User {
    public String id;
    public String email;
    public String phone;
    public String role;
    public String name;
    public String city;
    public String age;
    public String field;
    public boolean isVerified;
    public boolean isPhoneVerified;

    public User() {

    }

    public User(String id, String email) {
        this.id = id;
        this.email = email;
        this.phone = "";
        this.role = "pending";
        this.name = "";
        this.city = "";
        this.age = "";
        this.field = "";
        this.isVerified = false;
        this.isPhoneVerified = false;
    }
}
