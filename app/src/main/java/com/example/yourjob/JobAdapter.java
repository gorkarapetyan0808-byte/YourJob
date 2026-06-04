package com.example.yourjob;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList;
    private List<String> favoriteIds = new ArrayList<>();
    private String userCity = "";
    private String userField = "";
    private String userAge = "";
    private boolean isEmployerList = false;
    private boolean isAdmin = false;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnFavoriteClickListener {
        void onFavClick(int position, boolean isFav);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    private OnItemClickListener listener;
    private OnFavoriteClickListener favListener;
    private OnDeleteClickListener deleteListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener favListener) {
        this.favListener = favListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public JobAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    public void setEmployerList(boolean employerList) {
        isEmployerList = employerList;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        notifyDataSetChanged();
    }

    public void setUserPreferences(String city, String field, String age) {
        this.userCity = city != null ? city.trim() : "";
        this.userField = field != null ? field.trim() : "";
        this.userAge = age != null ? age.trim() : "";
        notifyDataSetChanged();
    }

    public void setFavoriteIds(List<String> favoriteIds) {
        this.favoriteIds = favoriteIds != null ? favoriteIds : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.job_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        Context context = holder.itemView.getContext();

        holder.title.setText(job.title);
        holder.company.setText(job.company);
        holder.description.setText(job.description);
        holder.locationLabel.setText("📍 " + job.city);
        holder.fieldLabel.setText("💼 " + job.field);

        String currentUserId = FirebaseAuth.getInstance().getUid();
        
        if (isAdmin || (isEmployerList && job.publisherId != null && job.publisherId.equals(currentUserId))) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.favBtn.setVisibility(View.GONE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
            holder.favBtn.setVisibility(View.VISIBLE);
            
            boolean isFav = favoriteIds.contains(job.id);
            holder.favBtn.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
            holder.favBtn.setImageTintList(ColorStateList.valueOf(isFav ? Color.parseColor("#FFD700") : Color.parseColor("#778DA9")));
        }

        int score = calculateMatchScore(job);

        if (score >= 100) {
            applyMatchStyle(holder, context.getString(R.string.match_perfect), "#4CAF50", 10);
        } else if (score >= 80) {
            applyMatchStyle(holder, context.getString(R.string.match_strong), "#2196F3", 7);
        } else if (score >= 60) {
            applyMatchStyle(holder, context.getString(R.string.match_medium), "#00BCD4", 5);
        } else if (score >= 40) {
            applyMatchStyle(holder, context.getString(R.string.match_good), "#FFD700", 4);
        } else if (score >= 20) {
            applyMatchStyle(holder, context.getString(R.string.match_age), "#9E9E9E", 2);
        } else {
            holder.cardView.setStrokeWidth(0);
            holder.matchBadge.setVisibility(View.GONE);
        }
    }

    private void applyMatchStyle(JobViewHolder holder, String text, String colorHex, int stroke) {
        int color = Color.parseColor(colorHex);
        holder.matchBadge.setVisibility(View.VISIBLE);
        holder.matchBadge.setText(text);
        holder.matchBadge.setBackgroundTintList(ColorStateList.valueOf(color));
        holder.cardView.setStrokeWidth(stroke);
        holder.cardView.setStrokeColor(color);
    }

    private int calculateMatchScore(Job job) {
        int score = 0;
        if (isValid(userCity) && job.city != null && job.city.trim().equalsIgnoreCase(userCity.trim())) score += 40;
        if (isValid(userField) && job.field != null && job.field.trim().equalsIgnoreCase(userField.trim())) score += 40;
        if (isValid(userAge) && isAgeInRange(userAge, job.age)) score += 20;
        return score;
    }

    private boolean isValid(String v) {
        return v != null && !v.isEmpty() && !v.contains("Select") && !v.contains("All");
    }

    private boolean isAgeInRange(String userAgeStr, String jobAgeRange) {
        if (userAgeStr == null || jobAgeRange == null || !jobAgeRange.contains("-")) return false;
        try {
            int ageNum = Integer.parseInt(userAgeStr.replaceAll("[^0-9]", "").trim());
            String[] parts = jobAgeRange.split("-");
            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());
            return ageNum >= min && ageNum <= max;
        } catch (Exception e) { return false; }
    }

    @Override
    public int getItemCount() { return jobList.size(); }

    class JobViewHolder extends RecyclerView.ViewHolder {
        TextView title, company, description, locationLabel, fieldLabel, matchBadge;
        ImageButton favBtn, deleteBtn;
        MaterialCardView cardView;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.jobTitle);
            company = itemView.findViewById(R.id.jobCompany);
            description = itemView.findViewById(R.id.jobDescription);
            locationLabel = itemView.findViewById(R.id.jobLocationLabel);
            fieldLabel = itemView.findViewById(R.id.jobFieldLabel);
            matchBadge = itemView.findViewById(R.id.matchBadge);
            favBtn = itemView.findViewById(R.id.favBtn);
            deleteBtn = itemView.findViewById(R.id.btnDeleteJob);
            cardView = itemView.findViewById(R.id.jobCard);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onClick(pos);
                }
            });

            favBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (favListener != null && pos != RecyclerView.NO_POSITION) {
                    boolean isCurrentlyFav = favoriteIds.contains(jobList.get(pos).id);
                    favListener.onFavClick(pos, !isCurrentlyFav);
                }
            });

            deleteBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (deleteListener != null && pos != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(pos);
                }
            });
        }
    }
}
