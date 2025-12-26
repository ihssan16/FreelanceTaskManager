package com.teamname.freelancetaskmanager.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public interface OnItemClickListener {
        void onItemClick(Project project);
    }

    private OnItemClickListener listener;

    public ProjectAdapter(List<Project> projectList, OnItemClickListener listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProjectName, textViewClient, textViewDescription, textViewBudget, textViewDeadline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProjectName = itemView.findViewById(R.id.textViewProjectName);
            textViewClient = itemView.findViewById(R.id.textViewClient);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewBudget = itemView.findViewById(R.id.textViewBudget);
            textViewDeadline = itemView.findViewById(R.id.textViewDeadline);
        }

        public void bind(Project project, OnItemClickListener listener) {
            textViewProjectName.setText(project.getName());
            textViewClient.setText("Client: " + project.getClient());
            textViewDescription.setText(project.getDescription());
            textViewBudget.setText("Budget: " + project.getBudget() + "€");

            // Formater la date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            String formattedDate = sdf.format(new Date(project.getDeadline()));
            textViewDeadline.setText("Échéance: " + formattedDate);

            itemView.setOnClickListener(v -> listener.onItemClick(project));
        }
    }

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
        holder.bind(project, listener);
    }

    @Override
    public int getItemCount() {
        return projectList == null ? 0 : projectList.size();
    }

    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
        notifyDataSetChanged();
    }
}