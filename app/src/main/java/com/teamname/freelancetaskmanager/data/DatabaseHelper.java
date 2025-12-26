package com.teamname.freelancetaskmanager.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.teamname.freelancetaskmanager.data.dao.ProjectDao;
import com.teamname.freelancetaskmanager.data.entities.Project;

@Database(entities = {Project.class}, version = 1, exportSchema = false)
public abstract class DatabaseHelper extends RoomDatabase {

    public abstract ProjectDao projectDao();

    // Singleton pattern pour éviter plusieurs instances
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            DatabaseHelper.class,
                            "freelance_database" // Nom de la base de données
                    )
                    .fallbackToDestructiveMigration() // En développement seulement
                    .build();
        }
        return instance;
    }
}