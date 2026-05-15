package com.example.yourjob;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdminAdapter adapter;
    List<Object> pendingList = new ArrayList<>();
    ProgressBar progressBar;
    TabLayout tabLayout;
    DatabaseReference mDatabase;
    boolean showingJobs = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.adminRecycler);
        progressBar = findViewById(R.id.adminProgressBar);
        tabLayout = findViewById(R.id.adminTabLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminAdapter(pendingList, new AdminAdapter.AdminActionListener() {
            @Override
            public void onApprove(Object item) {
                approveItem(item);
            }

            @Override
            public void onReject(Object item) {
                showRejectionDialog(item);
            }
        });
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/").getReference();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showingJobs = tab.getPosition() == 0;
                loadPendingItems();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadPendingItems();
    }

    private void loadPendingItems() {
        progressBar.setVisibility(View.VISIBLE);
        String path = showingJobs ? "jobs" : "businesses";
        
        mDatabase.child(path).orderByChild("isApproved").equalTo(false)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pendingList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (showingJobs) {
                                Job job = ds.getValue(Job.class);
                                if (job != null) pendingList.add(job);
                            } else {
                                Business business = ds.getValue(Business.class);
                                if (business != null) pendingList.add(business);
                            }
                        }
                        adapter.setShowingJobs(showingJobs);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void approveItem(Object item) {
        if (item instanceof Job) {
            mDatabase.child("jobs").child(((Job) item).id).child("isApproved").setValue(true);
        } else if (item instanceof Business) {
            mDatabase.child("businesses").child(((Business) item).userId).child("isApproved").setValue(true);
            mDatabase.child("businesses").child(((Business) item).userId).child("rejectionReason").setValue("");
        }
        Toast.makeText(this, "Approved successfully", Toast.LENGTH_SHORT).show();
    }

    private void showRejectionDialog(Object item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reject and Provide Reason");
        
        final EditText input = new EditText(this);
        input.setHint("Enter reason for rejection...");
        builder.setView(input);

        builder.setPositiveButton("Reject", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            rejectItem(item, reason);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void rejectItem(Object item, String reason) {
        if (item instanceof Job) {
            // For jobs, we might just delete them or set a reason if we had a field
            mDatabase.child("jobs").child(((Job) item).id).removeValue();
        } else if (item instanceof Business) {
            // For businesses, we keep the profile but set the reason so they can fix it
            String uid = ((Business) item).userId;
            mDatabase.child("businesses").child(uid).child("rejectionReason").setValue(reason);
            mDatabase.child("businesses").child(uid).child("isApproved").setValue(false);
        }
        Toast.makeText(this, "Rejected", Toast.LENGTH_SHORT).show();
    }
}
