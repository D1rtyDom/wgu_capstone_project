package com.example.d308vacation.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.d308vacation.model.Excursion;

import java.util.List;

@Dao
public interface ExcursionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addExcursion(Excursion excursion);

    @Update
    void updateExcursion(Excursion excursion);

    @Delete
    void deleteExcursion(Excursion excursion);

    @Query("SELECT * FROM excursions ORDER BY id")
    List<Excursion> getExcursions();

    @Query("SELECT * FROM excursions WHERE id = :excursionId")
    Excursion getExcursion(long excursionId);

    @Query("SELECT * FROM excursions where vacationId = :id")
    List<Excursion> getVacationExcursions(long id);
}
