package com.example.yourjob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class ApplicationsFragment extends Fragment {

    RecyclerView recyclerView;
    ApplicationAdapter adapter;
    List<Application> myAppsList = new ArrayList<>();
    String userId;

    public ApplicationsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);
        
        userId = FirebaseAuth.getInstance().getUid();
        recyclerView = view.findViewById(R.id.appsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new ApplicationAdapter(myAppsList, false);
        recyclerView.setAdapter(adapter);

        loadMyApplications();
        
        return view;
    }

    private void loadMyApplications() {
        if (userId == null) return;

        DatabaseReference db = FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                .getReference("applications");

        db.orderByChild("applicantId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myAppsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Application app = ds.getValue(Application.class);
                            if (app != null) myAppsList.add(app);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
