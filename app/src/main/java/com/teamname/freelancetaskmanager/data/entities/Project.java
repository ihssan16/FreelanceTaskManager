package com.teamname.freelancetaskmanager.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "projects")
public class Project {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String client;
    private String description;
    private double budget;
    private long deadline; // Timestamp en millisecondes
    private long createdAt;

    // ✅ TASK STATUS
    @NonNull
    private String status; // "PENDING" or "DONE"

    // Constructeur sans id (car auto-généré)
    public Project(String name, String client, String description, double budget, long deadline) {
        this.name = name;
        this.client = client;
        this.description = description;
        this.budget = budget;
        this.deadline = deadline;
        this.createdAt = System.currentTimeMillis();
        this.status = "PENDING"; // ✅ default
    }

    // ===== GETTERS =====
    public int getId() { return id; }
    public String getName() { return name; }
    public String getClient() { return client; }
    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public long getDeadline() { return deadline; }
    public long getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }

    // ===== SETTERS =====
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setClient(String client) { this.client = client; }
    public void setDescription(String description) { this.description = description; }
    public void setBudget(double budget) { this.budget = budget; }
    public void setDeadline(long deadline) { this.deadline = deadline; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setStatus(@NonNull String status) { this.status = status; }
}