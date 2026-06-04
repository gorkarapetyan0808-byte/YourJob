package com.example.yourjob;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JobStorage {
    public static ArrayList<Job> jobs = new ArrayList<>();
    public static ArrayList<Application> applications = new ArrayList<>();

    private static final String PREF_NAME = "jobs_data";
    private static final String KEY_JOBS = "all_jobs";
    private static final String KEY_APPS = "all_applications";
    private static final String FIREBASE_URL = "https://yourjob-59823-default-rtdb.firebaseio.com/";

    public static void saveJobs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            JSONArray array = new JSONArray();
            for (Job job : jobs) {
                JSONObject obj = new JSONObject();
                obj.put("id", job.id);
                obj.put("title", job.title);
                obj.put("company", job.company);
                obj.put("description", job.description);
                obj.put("age", job.age);
                obj.put("field", job.field);
                obj.put("contact", job.contact);
                obj.put("city", job.city);
                obj.put("publisherId", job.publisherId);
                obj.put("isApproved", job.isApproved);
                obj.put("timestamp", job.timestamp);
                array.put(obj);
            }
            editor.putString(KEY_JOBS, array.toString());
            editor.apply();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void addProfessionalSampleJobs() {
        if (FirebaseAuth.getInstance().getUid() == null) return;

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance(FIREBASE_URL).getReference().child("jobs");
        
        String[][] data = {
            {"Senior Java Developer", "Synopsys Armenia", "Build scalable microservices for global platforms. Requirements: 5+ years Java/Spring Boot experience.", "25-55", "IT / Programming", "010-123456", "Yerevan"},
            {"Cardiology Nurse", "Astghik Medical Center", "Join our cardiology department. Professional nursing license and 2 years experience mandatory. Full-time position.", "22-45", "Medicine / Pharmacy", "011-555666", "Yerevan"},
            {"English Language Teacher", "AUA Extension", "Teach academic English and TOEFL preparation courses to students. Excellent communication skills required.", "24-60", "Education / Teaching", "060-612345", "Yerevan"},
            {"Internal Audit Manager", "AmeriaBank", "Lead internal audit processes and compliance checks. ACCA or CPA qualification required.", "28-50", "Accounting", "010-561111", "Yerevan"},
            {"Regional Sales Executive", "Coca-Cola HBC", "Manage retail distribution and partnership agreements in Kotayk region. Strong negotiation skills required.", "22-40", "Sales / Business Development", "010-432211", "Abovyan"},
            {"Senior UI Designer", "Picsart", "Create world-class user experiences for our mobile creative suite. Figma expert required.", "23-45", "UI/UX Design", "099-000000", "Yerevan"},
            {"Front Desk Manager", "Marriott Hotel", "Supervise reception operations and ensure top-tier guest satisfaction at our resort. Fluency in English is required.", "25-45", "Tourism / Hospitality", "010-599000", "Tsaghkadzor"},
            {"Structural Engineer", "Horizon 95", "Calculate and design reinforced concrete structures for modern residential complexes. AutoCAD/Revit skills required.", "25-60", "Construction / Architecture", "010-223344", "Gyumri"},
            {"Talent Acquisition Lead", "Digitain", "Scale our engineering teams globally. Experience in high-volume IT recruitment is essential.", "25-45", "HR / Recruiting", "010-323232", "Yerevan"},
            {"Executive Chef", "Tavern Yerevan", "Innovate traditional Armenian desserts with a modern twist for our restaurant chain. Leadership skills are mandatory.", "24-50", "Cook / Chef", "010-545545", "Yerevan"},
            {"Logistics Coordinator", "Glovo Armenia", "Oversee daily warehouse logistics, inventory management, and courier dispatching.", "20-45", "Logistics / Delivery", "044-112233", "Vanadzor"},
            {"Digital Marketing Manager", "Galaxy Group", "Design multi-channel marketing campaigns for premium electronics and retail brands. Google Ads expert required.", "25-40", "Marketing / PR", "010-210000", "Yerevan"},
            {"Quality Control Chemist", "Natali Pharm", "Conduct laboratory tests on pharmaceutical products to ensure safety and quality standards.", "23-50", "Medicine / Pharmacy", "010-778899", "Armavir"},
            {"Safety & Security Manager", "G4S Armenia", "Develop emergency response plans and manage security personnel for industrial sites.", "30-55", "Security / Guard", "010-667788", "Kapan"},
            {"Legal Advisor", "SoftConstruct", "Draft and review international software development and licensing agreements. Focus on IP law.", "26-55", "Legal / Law", "010-445566", "Yerevan"}
        };

        for (int i = 0; i < data.length; i++) {
            String fixedId = "sample_job_" + (i + 1);
            String[] jobData = data[i];
            Job job = new Job(fixedId, jobData[0], jobData[1], jobData[2], jobData[3], jobData[4], jobData[5], jobData[6], "admin_sample");
            job.isApproved = true; 
            job.timestamp = System.currentTimeMillis() - (i * 3600000); 
            mDatabase.child(fixedId).setValue(job);
        }
    }

    public static void loadApplicationsFromFirebase(Context context, Runnable onComplete) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance(FIREBASE_URL).getReference();
        mDatabase.child("applications").orderByChild("applicantId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        applications.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Application app = ds.getValue(Application.class);
                            if (app != null) {
                                if (app.id == null || app.id.isEmpty()) app.id = ds.getKey();
                                applications.add(app);
                            }
                        }
                        if (onComplete != null) onComplete.run();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { if (onComplete != null) onComplete.run(); }
                });
    }

    public static void clearAll(Context context) {
        jobs.clear();
        applications.clear();
    }
}
