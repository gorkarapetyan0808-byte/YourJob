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
    public boolean isApproved;

    public Job() {
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
        this.isApproved = false;
    }
}
