package com.teamname.freelancetaskmanager.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.teamname.freelancetaskmanager.data.DatabaseHelper;
import com.teamname.freelancetaskmanager.data.dao.ProjectDao;
import com.teamname.freelancetaskmanager.data.entities.Project;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectRepository {

    private ProjectDao projectDao;
    private LiveData<List<Project>> allProjects;
    private ExecutorService executor;

    public ProjectRepository(Application application) {
        DatabaseHelper database = DatabaseHelper.getInstance(application);
        projectDao = database.projectDao();
        allProjects = projectDao.getAllProjects();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public void insert(Project project) {
        executor.execute(() -> projectDao.insert(project));
    }

    public void update(Project project) {
        executor.execute(() -> projectDao.update(project));
    }

    public void delete(Project project) {
        executor.execute(() -> projectDao.delete(project));
    }


    public void updateStatus(int id, String status) {
        executor.execute(() -> projectDao.updateStatus(id, status));
    }
}