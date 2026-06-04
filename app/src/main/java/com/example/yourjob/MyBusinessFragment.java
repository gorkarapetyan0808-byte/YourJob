package com.example.yourjob;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.ContentResolver;
import java.io.IOException;
import java.io.InputStream;

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

    TextView displayName, displayContact, displayLocation, approvalStatus;
    ImageView displayLogo;
    Button editBtn, postJobBtn, deleteBusinessBtn;
    RecyclerView myJobsRecycler;
    JobAdapter adapter;
    List<Job> myJobList;
    ProgressBar progressBar;

    DatabaseReference mDatabase;
    String userId;
    boolean isBusinessApproved = false;

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
        approvalStatus = view.findViewById(R.id.businessApprovalStatus);
        displayLogo = view.findViewById(R.id.displayBusinessLogo);
        editBtn = view.findViewById(R.id.editBusinessButton);
        postJobBtn = view.findViewById(R.id.postJobButton);
        deleteBusinessBtn = view.findViewById(R.id.deleteBusinessButton);
        myJobsRecycler = view.findViewById(R.id.myJobsRecycler);
        progressBar = view.findViewById(R.id.businessProgressBar);

        myJobsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        myJobList = new ArrayList<>();
        adapter = new JobAdapter(myJobList);
        adapter.setEmployerList(true); 
        myJobsRecycler.setAdapter(adapter);

        editBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), EditBusinessActivity.class)));

        postJobBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(BusinessManager.getName(getContext()))) {
                Toast.makeText(getContext(), "Please create a business profile first!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), EditBusinessActivity.class));
            } else if (!isBusinessApproved) {
                Toast.makeText(getContext(), "Your business profile is pending approval. You cannot post jobs yet.", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(getContext(), PostJobActivity.class));
            }
        });

        if (deleteBusinessBtn != null) {
            deleteBusinessBtn.setOnClickListener(v -> showDeleteBusinessConfirmation());
        }

        adapter.setOnItemClickListener(position -> {
            Job selectedJob = myJobList.get(position);
            Intent intent = new Intent(getContext(), JobApplicationsActivity.class);
            intent.putExtra("jobId", selectedJob.id);
            intent.putExtra("jobTitle", selectedJob.title);
            startActivity(intent);
        });

        adapter.setOnDeleteClickListener(position -> {
            showDeleteJobConfirmation(myJobList.get(position).id);
        });

        loadBusinessData();
        loadMyJobsFromFirebase();

        return view;
    }

    private void showDeleteJobConfirmation(String jobId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Job Posting")
                .setMessage("Are you sure you want to delete this job?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mDatabase.child("jobs").child(jobId).removeValue();
                    Toast.makeText(getContext(), "Job deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteBusinessConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Business Profile");
        builder.setMessage("Are you sure you want to delete your business profile and all its job postings?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteBusinessAndJobs());
        builder.setNegativeButton("Cancel", null);
        builder.show();
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
                        if (isAdded() && getContext() != null) {
                            BusinessManager.delete(getContext());
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            loadBusinessData();
                            myJobList.clear();
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) { if (isAdded()) progressBar.setVisibility(View.GONE); }
                });
    }

    private void loadBusinessData() {
        if (!isAdded() || getContext() == null) return;

        String name = BusinessManager.getName(getContext());
        String phone = BusinessManager.getPhone(getContext());
        String email = BusinessManager.getEmail(getContext());
        String logoData = BusinessManager.getLogo(getContext());
        String city = BusinessManager.getCity(getContext());
        String field = BusinessManager.getField(getContext());

        if (TextUtils.isEmpty(name)) {
            displayName.setText("No profile");
            displayContact.setText("Click Create to start");
            displayLocation.setVisibility(View.GONE);
            editBtn.setText("Create");
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.GONE);
            if (approvalStatus != null) approvalStatus.setVisibility(View.GONE);
        } else {
            displayName.setText(name);
            displayContact.setText(phone + " | " + email);
            displayLocation.setVisibility(View.VISIBLE);
            displayLocation.setText(city + " | " + field);
            editBtn.setText("Edit");
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.VISIBLE);
            
            mDatabase.child("businesses").child(userId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!isAdded()) return;
                            Business b = snapshot.getValue(Business.class);
                            if (b != null) {
                                isBusinessApproved = b.isApproved;
                                if (approvalStatus != null) {
                                    if (isBusinessApproved) {
                                        approvalStatus.setVisibility(View.GONE); 
                                    } else {
                                        approvalStatus.setVisibility(View.VISIBLE);
                                        if (!TextUtils.isEmpty(b.rejectionReason)) {
                                            approvalStatus.setText("Not Accepted: " + b.rejectionReason);
                                            approvalStatus.setTextColor(android.graphics.Color.RED);
                                        } else {
                                            approvalStatus.setText("Waiting...");
                                            approvalStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                                        }
                                    }
                                }
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }

        if (!TextUtils.isEmpty(logoData)) {
            if (logoData.startsWith("data:image")) {
                try {
                    String base64Image = logoData.split(",")[1];
                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    displayLogo.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    displayLogo.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                try {
                    Uri logoUri = Uri.parse(logoData);
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(logoUri);
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        displayLogo.setImageBitmap(bitmap);
                        inputStream.close();
                    }
                } catch (Exception e) {
                    displayLogo.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            }
        } else {
            displayLogo.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void loadMyJobsFromFirebase() {
        if (userId == null) return;
        mDatabase.child("jobs").orderByChild("publisherId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        myJobList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Job job = ds.getValue(Job.class);
                            if (job != null) myJobList.add(job);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
