package com.teamname.freelancetaskmanager.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.teamname.freelancetaskmanager.data.entities.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY deadline ASC")
    LiveData<List<Task>> getTasksForProject(long projectId);
}