package com.example.yourjob;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBusinessFragment extends Fragment {

    TextView displayName, displayContact, displayLocation;
    ImageView displayLogo;
    Button editBtn, postJobBtn, deleteBusinessBtn;
    RecyclerView myJobsRecycler;
    JobAdapter adapter;
    List<Job> myJobList;
    ProgressBar progressBar;

    DatabaseReference mDatabase;
    String userId;

    public MyBusinessFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_business, container, false);

        userId = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        displayName = view.findViewById(R.id.displayBusinessName);
        displayContact = view.findViewById(R.id.displayBusinessContact);
        displayLocation = view.findViewById(R.id.displayBusinessLocation);
        displayLogo = view.findViewById(R.id.displayBusinessLogo);
        editBtn = view.findViewById(R.id.editBusinessButton);
        postJobBtn = view.findViewById(R.id.postJobButton);
        deleteBusinessBtn = view.findViewById(R.id.deleteBusinessButton);
        myJobsRecycler = view.findViewById(R.id.myJobsRecycler);
        progressBar = view.findViewById(R.id.businessProgressBar);

        myJobsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        myJobList = new ArrayList<>();
        adapter = new JobAdapter(myJobList);
        myJobsRecycler.setAdapter(adapter);

        editBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), EditBusinessActivity.class)));

        postJobBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(BusinessManager.getName(getContext()))) {
                Toast.makeText(getContext(), "Please create a business profile first!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), EditBusinessActivity.class));
            } else {
                startActivity(new Intent(getContext(), PostJobActivity.class));
            }
        });

        if (deleteBusinessBtn != null) {
            deleteBusinessBtn.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        adapter.setOnItemClickListener(position -> {
            Job selectedJob = myJobList.get(position);
            Intent intent = new Intent(getContext(), JobApplicationsActivity.class);
            intent.putExtra("jobId", selectedJob.id);
            intent.putExtra("jobTitle", selectedJob.title);
            startActivity(intent);
        });

        loadBusinessData();
        loadMyJobsFromFirebase();

        return view;
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Business Profile")
                .setMessage("Are you sure you want to delete your business profile and all its job postings? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteBusinessAndJobs())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteBusinessAndJobs() {
        if (userId == null) return;
        progressBar.setVisibility(View.VISIBLE);

        mDatabase.child("jobs").orderByChild("publisherId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        BusinessManager.delete(getContext());
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Business profile and jobs deleted", Toast.LENGTH_SHORT).show();
                        loadBusinessData();
                        myJobList.clear();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void loadBusinessData() {
        String name = BusinessManager.getName(getContext());
        String phone = BusinessManager.getPhone(getContext());
        String email = BusinessManager.getEmail(getContext());
        String logoUriStr = BusinessManager.getLogo(getContext());
        String city = BusinessManager.getCity(getContext());
        String field = BusinessManager.getField(getContext());

        if (TextUtils.isEmpty(name)) {
            displayName.setText("No business profile");
            displayContact.setText("Click Edit to set up your business");
            displayLocation.setVisibility(View.GONE);
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.GONE);
        } else {
            displayName.setText(name);
            displayContact.setText(phone + " | " + email);
            displayLocation.setVisibility(View.VISIBLE);
            displayLocation.setText(city + " | " + field);
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(logoUriStr)) {
            displayLogo.setImageURI(Uri.parse(logoUriStr));
        } else {
            displayLogo.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void loadMyJobsFromFirebase() {
        if (userId == null) return;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        mDatabase.child("jobs").orderByChild("publisherId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myJobList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Job job = ds.getValue(Job.class);
                            if (job != null) myJobList.add(job);
                        }
                        adapter.notifyDataSetChanged();
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
