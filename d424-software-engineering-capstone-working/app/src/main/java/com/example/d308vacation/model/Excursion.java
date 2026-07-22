package com.example.d308vacation.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "excursions")
public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String date;

    private String description;

    private long vacationId;

    public Excursion(String name, String date, String description, long vacationId) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.vacationId = vacationId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getVacationId() {
        return vacationId;
    }

    public void setVacationId(long vacationId) {
        this.vacationId = vacationId;
    }
}
