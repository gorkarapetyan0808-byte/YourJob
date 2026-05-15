package com.example.yourjob;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private List<Object> items;
    private AdminActionListener listener;
    private boolean showingJobs = true;

    public interface AdminActionListener {
        void onApprove(Object item);
        void onReject(Object item);
    }

    public AdminAdapter(List<Object> items, AdminActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void setShowingJobs(boolean showingJobs) {
        this.showingJobs = showingJobs;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_job, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Object item = items.get(position);
        holder.fullInfo.setVisibility(View.VISIBLE);

        if (showingJobs && item instanceof Job) {
            Job job = (Job) item;
            holder.title.setText(job.title);
            holder.details.setText(job.company + " | " + job.city);
            holder.fullInfo.setText("Description: " + job.description + "\nField: " + job.field + "\nAge: " + job.age + "\nContact: " + job.contact);
        } else if (!showingJobs && item instanceof Business) {
            Business business = (Business) item;
            holder.title.setText(business.name);
            holder.details.setText(business.field + " | " + business.city);
            holder.fullInfo.setText("Email: " + business.email + "\nPhone: " + business.phone + "\nCity: " + business.city + "\nField: " + business.field);
        }

        holder.approveBtn.setOnClickListener(v -> listener.onApprove(item));
        holder.rejectBtn.setOnClickListener(v -> listener.onReject(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView title, details, fullInfo;
        Button approveBtn, rejectBtn;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.adminItemTitle);
            details = itemView.findViewById(R.id.adminItemDetails);
            fullInfo = itemView.findViewById(R.id.adminItemFullInfo);
            approveBtn = itemView.findViewById(R.id.btnApproveAdmin);
            rejectBtn = itemView.findViewById(R.id.btnRejectAdmin);
        }
    }
}
