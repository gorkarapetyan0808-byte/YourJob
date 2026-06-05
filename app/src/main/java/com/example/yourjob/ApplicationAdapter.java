package com.example.yourjob;

<<<<<<< HEAD
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.AppViewHolder> {

    private List<Application> applicationList;
    private boolean isEmployer;

    public ApplicationAdapter(List<Application> applicationList, boolean isEmployer) {
        this.applicationList = applicationList;
        this.isEmployer = isEmployer;
=======
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;

    public ApplicationAdapter(List<Application> applicationList) {
        this.applicationList = applicationList;
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }

    @NonNull
    @Override
<<<<<<< HEAD
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
            
            String phone = (app.applicantPhone != null) ? app.applicantPhone : "N/A";
            holder.fullInfo.setText("Age: " + app.applicantAge + " | City: " + app.applicantCity + "\nPhone: " + phone);
            
            holder.viewedStatus.setVisibility(View.GONE);
            
            if (!app.viewed) markAsViewed(app.id);
        } else {
            holder.applicantDetails.setText("My Application");
            holder.fullInfo.setVisibility(View.GONE);
            holder.viewedStatus.setVisibility(View.VISIBLE);
            
            String statusDetail = "";
            if ("accepted".equals(app.status)) {
                statusDetail = " (Accepted)"; 
            } else if ("rejected".equals(app.status)) {
                statusDetail = " (Not accepted)";
            }

            String viewedTxt = app.viewed ? "Viewed" : "Not viewed yet";
            holder.viewedStatus.setText(viewedTxt + statusDetail);
            holder.viewedStatus.setTextColor(app.viewed ? Color.parseColor("#4CAF50") : Color.WHITE);
        }
        
        holder.message.setText(app.message);
        holder.cvName.setText("CV: " + app.cvFileName);

        if (app.cvUri != null && !app.cvUri.isEmpty()) {
            holder.openCvBtn.setVisibility(View.VISIBLE);
            holder.openCvBtn.setOnClickListener(v -> fetchAndOpenCv(v.getContext(), app, holder.openCvBtn));
        } else {
            holder.openCvBtn.setVisibility(View.GONE);
        }

        holder.actionsLayout.setVisibility(View.GONE);
    }

    private void fetchAndOpenCv(Context context, Application app, Button btn) {
        if (app.cvUri.startsWith("http") || (app.cvUri.length() > 100 && !app.cvUri.equals("stored_externally"))) {
            openCvFromData(context, app.cvUri, app.cvFileName);
            return;
        }

        btn.setEnabled(false);
        btn.setText("Loading...");

        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                .getReference("cv_contents").child(app.id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        btn.setEnabled(true);
                        btn.setText("Open CV");
                        String cvData = snapshot.getValue(String.class);
                        if (cvData != null) {
                            openCvFromData(context, cvData, app.cvFileName);
                        } else {
                            Toast.makeText(context, "CV data not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        btn.setEnabled(true);
                        btn.setText("Open CV");
                        Toast.makeText(context, "Error loading CV", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openCvFromData(Context context, String data, String fileName) {
        try {
            File file = saveBase64ToFile(context, data, fileName);
            if (file != null) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, getMimeType(fileName));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open CV", Toast.LENGTH_SHORT).show();
        }
    }

    private File saveBase64ToFile(Context context, String base64Data, String fileName) {
        try {
            if (base64Data.contains(",")) base64Data = base64Data.split(",")[1];
            
            byte[] pdfAsBytes = Base64.decode(base64Data, Base64.DEFAULT);
            File filePath = new File(context.getCacheDir(), fileName);
            FileOutputStream os = new FileOutputStream(filePath, false);
            os.write(pdfAsBytes);
            os.flush();
            os.close();
            return filePath;
        } catch (IOException e) {
            return null;
        }
    }

    private String getMimeType(String fileName) {
        if (fileName.toLowerCase().endsWith(".pdf")) return "application/pdf";
        if (fileName.toLowerCase().endsWith(".doc")) return "application/msword";
        if (fileName.toLowerCase().endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "*/*";
    }

    private void markAsViewed(String appId) {
        FirebaseDatabase.getInstance("https://yourjob-59823-default-rtdb.firebaseio.com/")
                .getReference("applications").child(appId).child("viewed").setValue(true);
=======
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application app = applicationList.get(position);
        holder.name.setText(app.applicantName);
        holder.info.setText(app.applicantAge + " years old | " + app.applicantCity);
        holder.message.setText(app.message);
        holder.cvName.setText("CV: " + app.cvFileName);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

<<<<<<< HEAD
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
=======
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, info, message, cvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.appJobTitle);
            info = itemView.findViewById(R.id.appDetails);
            message = itemView.findViewById(R.id.appMessage);
            cvName = itemView.findViewById(R.id.appCvName);
>>>>>>> 0c6b6eaf772c754685d8cc660365b11912584f82
        }
    }
}
