package com.teamname.freelancetaskmanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.teamname.freelancetaskmanager.data.dao.ProjectDao;
import com.teamname.freelancetaskmanager.data.dao.TaskDao;
import com.teamname.freelancetaskmanager.data.entities.Project;
import com.teamname.freelancetaskmanager.data.entities.Task;

@Database(entities = {Project.class, Task.class}, version = 3, exportSchema = false)
public abstract class DatabaseHelper extends RoomDatabase {

    public abstract ProjectDao projectDao();
    public abstract TaskDao taskDao(); // <-- ADD THIS

    // Singleton pattern
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DatabaseHelper.class,
                            "freelance_database"
                    )
                    .fallbackToDestructiveMigration() // OK for development
                    .build();
        }
        return instance;
    }
}