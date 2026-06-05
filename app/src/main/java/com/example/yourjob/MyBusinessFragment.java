package com.example.yourjob;

import android.app.AlertDialog;
<<<<<<< HEAD
import android.content.DialogInterface;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
<<<<<<< HEAD
import android.util.Base64;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
<<<<<<< HEAD
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.ContentResolver;
import java.io.IOException;
import java.io.InputStream;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

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

<<<<<<< HEAD
    TextView displayName, displayContact, displayLocation, approvalStatus;
=======
    TextView displayName, displayContact, displayLocation;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    ImageView displayLogo;
    Button editBtn, postJobBtn, deleteBusinessBtn;
    RecyclerView myJobsRecycler;
    JobAdapter adapter;
    List<Job> myJobList;
    ProgressBar progressBar;

    DatabaseReference mDatabase;
    String userId;
<<<<<<< HEAD
    boolean isBusinessApproved = false;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82

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
<<<<<<< HEAD
        approvalStatus = view.findViewById(R.id.businessApprovalStatus);
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        displayLogo = view.findViewById(R.id.displayBusinessLogo);
        editBtn = view.findViewById(R.id.editBusinessButton);
        postJobBtn = view.findViewById(R.id.postJobButton);
        deleteBusinessBtn = view.findViewById(R.id.deleteBusinessButton);
        myJobsRecycler = view.findViewById(R.id.myJobsRecycler);
        progressBar = view.findViewById(R.id.businessProgressBar);

        myJobsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        myJobList = new ArrayList<>();
        adapter = new JobAdapter(myJobList);
<<<<<<< HEAD
        adapter.setEmployerList(true); 
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        myJobsRecycler.setAdapter(adapter);

        editBtn.setOnClickListener(v -> startActivity(new Intent(getContext(), EditBusinessActivity.class)));

        postJobBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(BusinessManager.getName(getContext()))) {
                Toast.makeText(getContext(), "Please create a business profile first!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), EditBusinessActivity.class));
<<<<<<< HEAD
            } else if (!isBusinessApproved) {
                Toast.makeText(getContext(), "Your business profile is pending approval. You cannot post jobs yet.", Toast.LENGTH_LONG).show();
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
            } else {
                startActivity(new Intent(getContext(), PostJobActivity.class));
            }
        });

        if (deleteBusinessBtn != null) {
<<<<<<< HEAD
            deleteBusinessBtn.setOnClickListener(v -> showDeleteBusinessConfirmation());
=======
            deleteBusinessBtn.setOnClickListener(v -> showDeleteConfirmationDialog());
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        }

        adapter.setOnItemClickListener(position -> {
            Job selectedJob = myJobList.get(position);
            Intent intent = new Intent(getContext(), JobApplicationsActivity.class);
            intent.putExtra("jobId", selectedJob.id);
            intent.putExtra("jobTitle", selectedJob.title);
            startActivity(intent);
        });

<<<<<<< HEAD
        adapter.setOnDeleteClickListener(position -> {
            showDeleteJobConfirmation(myJobList.get(position).id);
        });

=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        loadBusinessData();
        loadMyJobsFromFirebase();

        return view;
    }

<<<<<<< HEAD
    private void showDeleteJobConfirmation(String jobId) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Job Posting")
                .setMessage("Are you sure you want to delete this job?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mDatabase.child("jobs").child(jobId).removeValue();
                    Toast.makeText(getContext(), "Job deleted", Toast.LENGTH_SHORT).show();
                })
=======
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Business Profile")
                .setMessage("Are you sure you want to delete your business profile and all its job postings? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteBusinessAndJobs())
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                .setNegativeButton("Cancel", null)
                .show();
    }

<<<<<<< HEAD
    private void showDeleteBusinessConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Business Profile");
        builder.setMessage("Are you sure you want to delete your business profile and all its job postings?");
        builder.setPositiveButton("Delete", (dialog, which) -> deleteBusinessAndJobs());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
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
<<<<<<< HEAD
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
=======
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
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                });
    }

    private void loadBusinessData() {
<<<<<<< HEAD
        if (!isAdded() || getContext() == null) return;

        String name = BusinessManager.getName(getContext());
        String phone = BusinessManager.getPhone(getContext());
        String email = BusinessManager.getEmail(getContext());
        String logoData = BusinessManager.getLogo(getContext());
=======
        String name = BusinessManager.getName(getContext());
        String phone = BusinessManager.getPhone(getContext());
        String email = BusinessManager.getEmail(getContext());
        String logoUriStr = BusinessManager.getLogo(getContext());
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        String city = BusinessManager.getCity(getContext());
        String field = BusinessManager.getField(getContext());

        if (TextUtils.isEmpty(name)) {
<<<<<<< HEAD
            displayName.setText("No profile");
            displayContact.setText("Click Create to start");
            displayLocation.setVisibility(View.GONE);
            editBtn.setText("Create");
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.GONE);
            if (approvalStatus != null) approvalStatus.setVisibility(View.GONE);
=======
            displayName.setText("No business profile");
            displayContact.setText("Click Edit to set up your business");
            displayLocation.setVisibility(View.GONE);
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.GONE);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        } else {
            displayName.setText(name);
            displayContact.setText(phone + " | " + email);
            displayLocation.setVisibility(View.VISIBLE);
            displayLocation.setText(city + " | " + field);
<<<<<<< HEAD
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
=======
            if (deleteBusinessBtn != null) deleteBusinessBtn.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(logoUriStr)) {
            displayLogo.setImageURI(Uri.parse(logoUriStr));
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        } else {
            displayLogo.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void loadMyJobsFromFirebase() {
        if (userId == null) return;
<<<<<<< HEAD
=======
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        mDatabase.child("jobs").orderByChild("publisherId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
<<<<<<< HEAD
                        if (!isAdded()) return;
=======
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
                        myJobList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Job job = ds.getValue(Job.class);
                            if (job != null) myJobList.add(job);
                        }
                        adapter.notifyDataSetChanged();
<<<<<<< HEAD
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
=======
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
