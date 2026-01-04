package com.teamname.freelancetaskmanager.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teamname.freelancetaskmanager.R;
import com.teamname.freelancetaskmanager.data.entities.Project;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private List<Project> projectList;

    // ✅ Interface to communicate with MainActivity
    public interface OnProjectActionListener {
        void onStatusClick(Project project);
        void onDeleteClick(Project project);
    }

    private OnProjectActionListener listener;
    // ✅ Constructor
    public ProjectAdapter(List<Project> projectList, OnProjectActionListener listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    // ================= VIEW HOLDER =================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewProjectName;
        TextView textViewClient;
        TextView textViewDescription;
        TextView textViewBudget;
        TextView textViewDeadline;
        TextView txtStatus;
        Button btnStatus;
        Button btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewProjectName = itemView.findViewById(R.id.textViewProjectName);
            textViewClient = itemView.findViewById(R.id.textViewClient);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewBudget = itemView.findViewById(R.id.textViewBudget);
            textViewDeadline = itemView.findViewById(R.id.textViewDeadline);

            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnStatus = itemView.findViewById(R.id.btnStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }

    // ================= ADAPTER METHODS =================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Project project = projectList.get(position);

        // ===== Bind basic info =====
        holder.textViewProjectName.setText(project.getName());
        holder.textViewClient.setText("Client: " + project.getClient());
        holder.textViewDescription.setText(project.getDescription());
        holder.textViewBudget.setText("Budget: " + project.getBudget() + "€");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String formattedDate = sdf.format(new Date(project.getDeadline()));
        holder.textViewDeadline.setText("Deadline: " + formattedDate);

        // ===== Bind status =====
        holder.txtStatus.setText(project.getStatus());

        if ("DONE".equals(project.getStatus())) {
            holder.txtStatus.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_green_dark)
            );
            holder.btnStatus.setText("Undo");
        } else {
            holder.txtStatus.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_red_dark)
            );
            holder.btnStatus.setText("Mark Done");
        }

        // ===== Button click =====
        holder.btnStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusClick(project);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(project);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    // ================= UPDATE LIST =================
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
        notifyDataSetChanged();
    }

}