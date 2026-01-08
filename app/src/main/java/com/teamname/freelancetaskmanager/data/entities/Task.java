package com.teamname.freelancetaskmanager.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long projectId; // FK to Project
    private String title;
    private String description;
    private long deadline;
    private String status; // "PENDING" or "DONE"

    public Task(long projectId, String title, String description, long deadline) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = "PENDING";
    }

    // ===== GETTERS & SETTERS =====
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getProjectId() { return projectId; }
    public void setProjectId(long projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getDeadline() { return deadline; }
    public void setDeadline(long deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}