package com.teamname.freelancetaskmanager.ui.adapters;

import android.graphics.Paint;
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
    private OnProjectActionListener listener;

    // ================= INTERFACE =================
    public interface OnProjectActionListener {
        void onStatusClick(Project project);
        void onDeleteClick(Project project);
        void onEditClick(Project project);

        // 🔥 PROJECT CLICK
        void onProjectClick(Project project);
    }

    // ================= CONSTRUCTOR =================
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
        Button btnEdit;

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
            btnEdit = itemView.findViewById(R.id.btnEdit);
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

        // ===== BASIC INFO =====
        holder.textViewProjectName.setText(project.getName());
        holder.textViewClient.setText("Client: " + project.getClient());
        holder.textViewDescription.setText(project.getDescription());
        holder.textViewBudget.setText("Budget: " + project.getBudget() + "€");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.textViewDeadline.setText("Deadline: " + sdf.format(new Date(project.getDeadline())));

        // ===== STATUS =====
        holder.txtStatus.setText(project.getStatus());

        if ("DONE".equals(project.getStatus())) {

            holder.itemView.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.done_background)
            );

            int gray = holder.itemView.getResources().getColor(android.R.color.darker_gray);
            holder.textViewProjectName.setTextColor(gray);
            holder.textViewClient.setTextColor(gray);
            holder.textViewDescription.setTextColor(gray);
            holder.textViewBudget.setTextColor(gray);
            holder.textViewDeadline.setTextColor(gray);

            holder.textViewProjectName.setPaintFlags(
                    holder.textViewProjectName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.txtStatus.setTextColor(
                    holder.itemView.getResources().getColor(R.color.done_text)
            );

            holder.btnStatus.setText("Undo");

        } else {

            holder.itemView.setBackgroundColor(
                    holder.itemView.getResources().getColor(android.R.color.white)
            );

            int black = holder.itemView.getResources().getColor(android.R.color.black);
            holder.textViewProjectName.setTextColor(black);
            holder.textViewClient.setTextColor(black);
            holder.textViewDescription.setTextColor(black);
            holder.textViewBudget.setTextColor(black);
            holder.textViewDeadline.setTextColor(black);

            holder.textViewProjectName.setPaintFlags(
                    holder.textViewProjectName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );

            holder.txtStatus.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_red_dark)
            );

            holder.btnStatus.setText("Mark Done");
        }

        // ===== BUTTON CLICKS =====
        holder.btnStatus.setOnClickListener(v -> {
            if (listener != null) listener.onStatusClick(project);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(project);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(project);
        });

        // 🔥 PROJECT CLICK (WHOLE CARD)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProjectClick(project);
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