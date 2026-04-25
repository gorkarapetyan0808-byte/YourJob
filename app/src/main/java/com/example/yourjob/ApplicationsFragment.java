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

import java.util.List;

public class ApplicationsFragment extends Fragment {

    RecyclerView recyclerView;
    AppAdapter adapter;

    public ApplicationsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applications, container, false);
        
        recyclerView = view.findViewById(R.id.appsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 🔥 LOAD PERSISTED APPLICATIONS
        JobStorage.loadApplications(getContext());
        
        adapter = new AppAdapter(JobStorage.applications);
        recyclerView.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
        List<Application> list;
        AppAdapter(List<Application> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Application app = list.get(position);
            holder.jobTitle.setText(app.jobTitle);
            holder.details.setText("Applicant: " + app.applicantName + " (" + app.applicantAge + "y., " + app.applicantCity + ")");
            holder.message.setText("Message: " + app.message);
            holder.cvName.setText("CV: " + app.cvFileName);

            // Open CV on click
            holder.itemView.setOnClickListener(v -> {
                if (app.cvUri != null && !app.cvUri.isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(app.cvUri), "*/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Cannot open CV file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView jobTitle, details, message, cvName;
            ViewHolder(View itemView) {
                super(itemView);
                jobTitle = itemView.findViewById(R.id.appJobTitle);
                details = itemView.findViewById(R.id.appDetails);
                message = itemView.findViewById(R.id.appMessage);
                cvName = itemView.findViewById(R.id.appCvName);
            }
        }
    }
}
