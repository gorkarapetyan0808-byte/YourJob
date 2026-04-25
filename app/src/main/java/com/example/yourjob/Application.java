package com.example.yourjob;

public class Application {
    public String id;
    public String jobId;
    public String jobTitle;
    public String applicantId;
    public String applicantName;
    public String applicantAge;
    public String applicantCity;
    public String message;
    public String cvFileName;
    public String cvUri;
    public String status; // "pending", "accepted", "rejected"
    public long timestamp;

    public Application() {
    }

    public Application(String id, String jobId, String jobTitle, String applicantId, 
                       String applicantName, String applicantAge, String applicantCity, 
                       String message, String cvFileName, String cvUri) {
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.applicantAge = applicantAge;
        this.applicantCity = applicantCity;
        this.message = message;
        this.cvFileName = cvFileName;
        this.cvUri = cvUri;
        this.status = "pending";
        this.timestamp = System.currentTimeMillis();
    }
}
