package com.teamname.freelancetaskmanager.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamname.freelancetaskmanager.data.entities.Project;

import java.util.List;

@Dao
public interface ProjectDao {

    // Insérer un projet
    @Insert
    void insert(Project project);

    // Mettre à jour un projet
    @Update
    void update(Project project);

    // Supprimer un projet
    @Delete
    void delete(Project project);

    // Récupérer tous les projets
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    LiveData<List<Project>> getAllProjects();

    // Récupérer un projet par son ID
    @Query("SELECT * FROM projects WHERE id = :projectId")
    LiveData<Project> getProjectById(int projectId);

    // Supprimer tous les projets
    @Query("DELETE FROM projects")
    void deleteAllProjects();
}