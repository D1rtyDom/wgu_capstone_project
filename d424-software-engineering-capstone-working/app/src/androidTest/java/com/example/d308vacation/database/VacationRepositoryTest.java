package com.example.d308vacation.database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.d308vacation.dao.VacationDao;
import com.example.d308vacation.model.Vacation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class VacationRepositoryTest {

    private VacationDatabase db;
    private VacationDao dao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, VacationDatabase.class).build();
        dao = db.vacationDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void setAndGetVacation() {
        Vacation vacation = new Vacation("Test Vacation", "Test Hotel", "1/1/1999", "1/1/2000", "A test vacation.", "testUser");
        dao.addVacation(vacation);
        List<Vacation> returnVacation = dao.getVacations();
        assertThat(returnVacation, contains(vacation));
    }
}
