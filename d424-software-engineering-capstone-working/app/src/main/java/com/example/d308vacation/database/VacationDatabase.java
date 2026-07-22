package com.example.d308vacation.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.d308vacation.dao.ExcursionDao;
import com.example.d308vacation.dao.VacationDao;
import com.example.d308vacation.model.Excursion;
import com.example.d308vacation.model.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 5, exportSchema = false)
public abstract class VacationDatabase extends RoomDatabase {
    public abstract VacationDao vacationDao();
    public abstract ExcursionDao excursionDao();

    // Support async
    private static volatile VacationDatabase INSTANCE;

    static VacationDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VacationDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VacationDatabase.class, "vacation.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
