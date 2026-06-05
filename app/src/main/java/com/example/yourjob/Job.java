package com.example.yourjob;

public class Job {
    public String id;
    public String title;
    public String company;
    public String description;
    public String age;
    public String field;
    public String contact;
    public String city;
    public String publisherId;
<<<<<<< HEAD
    public boolean isApproved;
    public long timestamp;

    public Job() {
=======

    public Job() {
        // Default constructor required for calls to DataSnapshot.getValue(Job.class)
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }

    public Job(String id, String title, String company, String description,
               String age, String field, String contact, String city, String publisherId) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.age = age;
        this.field = field;
        this.contact = contact;
        this.city = city;
        this.publisherId = publisherId;
<<<<<<< HEAD
        this.isApproved = false;
        this.timestamp = System.currentTimeMillis();
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }
}
