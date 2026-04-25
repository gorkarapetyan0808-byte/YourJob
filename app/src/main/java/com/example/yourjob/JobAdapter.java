package com.example.yourjob;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    List<Job> jobList;
    private String userCity = "";
    private String userField = "";

    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public JobAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    public void setUserPreferences(String city, String field) {
        this.userCity = city != null ? city : "";
        this.userField = field != null ? field : "";
        notifyDataSetChanged();
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        holder.title.setText(job.title);
        holder.company.setText(job.company);
        holder.description.setText(job.description);
        holder.locationLabel.setText("📍 " + job.city);
        holder.fieldLabel.setText("💼 " + job.field);

        // MATCH SYSTEM: Highlight if matches user city or field
        boolean cityMatch = !userCity.isEmpty() && !"Ընտրել քաղաք".equals(userCity) && job.city != null && job.city.equalsIgnoreCase(userCity);
        boolean fieldMatch = !userField.isEmpty() && !"Ընտրել ոլորտ".equals(userField) && job.field != null && job.field.equalsIgnoreCase(userField);

        if (cityMatch && fieldMatch) {
            holder.cardView.setStrokeWidth(6);
            holder.cardView.setStrokeColor(Color.parseColor("#4CAF50")); // Green for perfect match
            holder.matchBadge.setVisibility(View.VISIBLE);
            holder.matchBadge.setText("PERFECT MATCH");
            holder.matchBadge.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (cityMatch || fieldMatch) {
            holder.cardView.setStrokeWidth(4);
            holder.cardView.setStrokeColor(Color.parseColor("#FFD700")); // Gold for partial match
            holder.matchBadge.setVisibility(View.VISIBLE);
            holder.matchBadge.setText("MATCH");
            holder.matchBadge.setBackgroundColor(Color.parseColor("#FFD700"));
        } else {
            holder.cardView.setStrokeWidth(0);
            holder.matchBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    class JobViewHolder extends RecyclerView.ViewHolder {

        TextView title, company, description, locationLabel, fieldLabel, matchBadge;
        com.google.android.material.card.MaterialCardView cardView;

        public JobViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.jobTitle);
            company = itemView.findViewById(R.id.jobCompany);
            description = itemView.findViewById(R.id.jobDescription);
            locationLabel = itemView.findViewById(R.id.jobLocationLabel);
            fieldLabel = itemView.findViewById(R.id.jobFieldLabel);
            matchBadge = itemView.findViewById(R.id.matchBadge);
            cardView = (com.google.android.material.card.MaterialCardView) itemView;

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onClick(position);
                    }
                }
            });
        }
    }
}
