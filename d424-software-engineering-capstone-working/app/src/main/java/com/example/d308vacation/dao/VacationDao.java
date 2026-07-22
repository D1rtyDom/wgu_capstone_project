package com.example.d308vacation.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacation.model.Vacation;

import java.util.List;

@Dao
public interface VacationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addVacation(Vacation vacation);

    @Update
    void updateVacation(Vacation vacation);

    @Delete
    void deleteVacation(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY id")
    List<Vacation> getVacations();

    @Query("SELECT * FROM vacations WHERE username = :username ORDER BY id")
    List<Vacation> getVacationsByUser(String username);

    @Query("SELECT * FROM vacations WHERE id = :vacationId")
    Vacation getVacation(long vacationId);

    @Query("SELECT * FROM vacations WHERE name LIKE :query AND username = :username ORDER BY name")
    List<Vacation> getQueriedVacationsByUser(String query, String username);
}
