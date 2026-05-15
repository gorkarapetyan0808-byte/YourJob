package com.example.yourjob;

public class Business {
    public String userId;
    public String name;
    public String phone;
    public String email;
    public String logoUri;
    public String city;
    public String field;
    public boolean isApproved;
    public String rejectionReason;

    public Business() {
    }

    public Business(String userId, String name, String phone, String email, String logoUri, String city, String field) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.logoUri = logoUri;
        this.city = city;
        this.field = field;
        this.isApproved = false;
        this.rejectionReason = "";
    }
}
