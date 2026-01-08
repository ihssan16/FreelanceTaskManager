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
import com.teamname.freelancetaskmanager.data.entities.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> taskList;
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onStatusClick(Task task);
        void onDeleteClick(Task task);
        void onEditClick(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskActionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtDescription, txtDeadline, txtStatus;
        Button btnStatus, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTaskTitle);
            txtDescription = itemView.findViewById(R.id.txtTaskDescription);
            txtDeadline = itemView.findViewById(R.id.txtTaskDeadline);
            txtStatus = itemView.findViewById(R.id.txtTaskStatus);

            btnStatus = itemView.findViewById(R.id.btnTaskStatus);
            btnEdit = itemView.findViewById(R.id.btnTaskEdit);
            btnDelete = itemView.findViewById(R.id.btnTaskDelete);
        }
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.txtTitle.setText(task.getTitle());
        holder.txtDescription.setText(task.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.txtDeadline.setText("Deadline: " + sdf.format(new Date(task.getDeadline())));
        holder.txtStatus.setText(task.getStatus());

        if ("DONE".equals(task.getStatus())) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.done_background));
            int gray = holder.itemView.getResources().getColor(android.R.color.darker_gray);
            holder.txtTitle.setTextColor(gray);
            holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.btnStatus.setText("Undo");
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(android.R.color.white));
            holder.txtTitle.setTextColor(holder.itemView.getResources().getColor(android.R.color.black));
            holder.txtTitle.setPaintFlags(holder.txtTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.btnStatus.setText("Mark Done");
        }

        holder.btnStatus.setOnClickListener(v -> {
            if (listener != null) listener.onStatusClick(task);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }
}