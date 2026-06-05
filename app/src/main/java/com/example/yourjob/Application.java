package com.example.yourjob;

public class Application {
    public String id;
    public String jobId;
    public String jobTitle;
    public String applicantId;
    public String applicantName;
    public String applicantAge;
    public String applicantCity;
<<<<<<< HEAD
    public String applicantPhone;
    public String message;
    public String cvFileName;
    public String cvUri;
    public String status;
    public boolean viewed;
=======
    public String message;
    public String cvFileName;
    public String cvUri;
    public String status; // "pending", "accepted", "rejected"
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    public long timestamp;

    public Application() {
    }

<<<<<<< HEAD
    public Application(String id, String jobId, String jobTitle, String applicantId, String applicantName, 
                       String applicantAge, String applicantCity, String applicantPhone, String message, String cvFileName, String cvUri) {
=======
    public Application(String id, String jobId, String jobTitle, String applicantId, 
                       String applicantName, String applicantAge, String applicantCity, 
                       String message, String cvFileName, String cvUri) {
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        this.id = id;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.applicantId = applicantId;
        this.applicantName = applicantName;
        this.applicantAge = applicantAge;
        this.applicantCity = applicantCity;
<<<<<<< HEAD
        this.applicantPhone = applicantPhone;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        this.message = message;
        this.cvFileName = cvFileName;
        this.cvUri = cvUri;
        this.status = "pending";
<<<<<<< HEAD
        this.viewed = false;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        this.timestamp = System.currentTimeMillis();
    }
}
