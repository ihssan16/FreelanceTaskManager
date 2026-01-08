package com.teamname.freelancetaskmanager.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.teamname.freelancetaskmanager.data.DatabaseHelper;
import com.teamname.freelancetaskmanager.data.dao.TaskDao;
import com.teamname.freelancetaskmanager.data.entities.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {

    private TaskDao taskDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public TaskViewModel(@NonNull Application application) {
        super(application);
        DatabaseHelper db = DatabaseHelper.getInstance(application);
        taskDao = db.taskDao();
    }

    public LiveData<List<Task>> getTasksForProject(long projectId) {
        return taskDao.getTasksForProject(projectId);
    }

    public void insert(Task task) {
        executor.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executor.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executor.execute(() -> taskDao.delete(task));
    }
}