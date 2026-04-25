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

public class JobStorage {
    public static ArrayList<Job> jobs = new ArrayList<>();
    public static ArrayList<Application> applications = new ArrayList<>();

    private static final String PREF_NAME = "jobs_data";
    private static final String KEY_JOBS = "all_jobs";
    private static final String KEY_APPS = "all_applications";

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
                array.put(obj);
            }
            editor.putString(KEY_JOBS, array.toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadJobs(Context context) {
        if (!jobs.isEmpty()) return;
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_JOBS, null);
        
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    jobs.add(new Job(
                        obj.optString("id", ""),
                        obj.getString("title"),
                        obj.getString("company"),
                        obj.getString("description"),
                        obj.getString("age"),
                        obj.getString("field"),
                        obj.getString("contact"),
                        obj.getString("city"),
                        obj.optString("publisherId", "")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearAll(Context context) {
        jobs.clear();
        applications.clear();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static void saveApplications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            JSONArray array = new JSONArray();
            for (Application app : applications) {
                JSONObject obj = new JSONObject();
                obj.put("id", app.id);
                obj.put("jobId", app.jobId);
                obj.put("jobTitle", app.jobTitle);
                obj.put("applicantId", app.applicantId);
                obj.put("applicantName", app.applicantName);
                obj.put("applicantAge", app.applicantAge);
                obj.put("applicantCity", app.applicantCity);
                obj.put("message", app.message);
                obj.put("cvFileName", app.cvFileName);
                obj.put("cvUri", app.cvUri);
                array.put(obj);
            }
            editor.putString(KEY_APPS, array.toString());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadApplications(Context context) {
        if (!applications.isEmpty()) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_APPS, null);
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    applications.add(new Application(
                        obj.optString("id", ""),
                        obj.optString("jobId", ""),
                        obj.getString("jobTitle"),
                        obj.optString("applicantId", ""),
                        obj.getString("applicantName"),
                        obj.getString("applicantAge"),
                        obj.getString("applicantCity"),
                        obj.getString("message"),
                        obj.getString("cvFileName"),
                        obj.getString("cvUri")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadApplicationsFromFirebase(Context context, Runnable onComplete) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();
        mDatabase.child("applications").orderByChild("applicantId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        applications.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Application app = ds.getValue(Application.class);
                            if (app != null) {
                                applications.add(app);
                            }
                        }
                        saveApplications(context);
                        if (onComplete != null) onComplete.run();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (onComplete != null) onComplete.run();
                    }
                });
    }
}
