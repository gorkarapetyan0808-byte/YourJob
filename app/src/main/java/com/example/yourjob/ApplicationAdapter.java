package com.example.yourjob;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.AppViewHolder> {

    private List<Application> applicationList;
    private boolean isEmployer;

    public ApplicationAdapter(List<Application> applicationList, boolean isEmployer) {
        this.applicationList = applicationList;
        this.isEmployer = isEmployer;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        final Application app = applicationList.get(position);

        holder.jobTitle.setText(app.jobTitle);
        holder.statusBadge.setVisibility(View.GONE);

        if (isEmployer) {
            holder.applicantDetails.setText("From: " + app.applicantName);
            holder.fullInfo.setVisibility(View.VISIBLE);
            holder.fullInfo.setText("Age: " + app.applicantAge + " | City: " + app.applicantCity);
            if (!app.viewed) markAsViewed(app.id);
        } else {
            holder.applicantDetails.setText("My Application");
            holder.fullInfo.setVisibility(View.GONE);
        }
        
        holder.message.setText(app.message);
        holder.cvName.setText("CV: " + app.cvFileName);

        // Status Logic as requested
        String displayStatus = "";
        if ("accepted".equals(app.status)) {
            displayStatus = ""; // Hide if accepted
        } else if ("rejected".equals(app.status)) {
            displayStatus = "Not accepted";
        } else {
            displayStatus = "Waiting...";
        }

        // Viewed Logic
        String viewedTxt = app.viewed ? "Viewed" : "Not viewed yet";
        holder.viewedStatus.setText(viewedTxt + (displayStatus.isEmpty() ? "" : " (" + displayStatus + ")"));
        holder.viewedStatus.setTextColor(app.viewed ? Color.parseColor("#4CAF50") : Color.WHITE);

        if (app.cvUri != null && !app.cvUri.isEmpty()) {
            holder.openCvBtn.setVisibility(View.VISIBLE);
            holder.openCvBtn.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.cvUri));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "Cannot open CV", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.openCvBtn.setVisibility(View.GONE);
        }

        holder.actionsLayout.setVisibility(View.GONE);
    }

    private void markAsViewed(String appId) {
        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                .getReference("applications").child(appId).child("viewed").setValue(true);
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, applicantDetails, statusBadge, message, cvName, viewedStatus, fullInfo;
        Button openCvBtn;
        View actionsLayout;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.appJobTitle);
            applicantDetails = itemView.findViewById(R.id.appDetails);
            fullInfo = itemView.findViewById(R.id.appFullInfo);
            statusBadge = itemView.findViewById(R.id.appStatusBadge);
            message = itemView.findViewById(R.id.appMessage);
            cvName = itemView.findViewById(R.id.appCvName);
            viewedStatus = itemView.findViewById(R.id.appViewedStatus);
            openCvBtn = itemView.findViewById(R.id.btnOpenCv);
            actionsLayout = itemView.findViewById(R.id.employerActionsLayout);
        }
    }
}
