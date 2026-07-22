package com.example.d308vacation.UI;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.d308vacation.UI.adapter.VacationAdapter;
import com.example.d308vacation.database.VacationRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.d308vacation.R;
import com.example.d308vacation.model.Excursion;
import com.example.d308vacation.model.Vacation;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class VacationListActivity extends AppCompatActivity {

    VacationRepository repo;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get current user
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    masterKeyAlias,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            currentUser = sharedPreferences.getString("current_user", "");
        } catch (GeneralSecurityException | IOException e) {
            currentUser = "";
        }

        // setup recycler list
        setRecyclerView();

        // add button listener
        findViewById(R.id.add_vacation_button).setOnClickListener(view -> {
            try {
                addVacation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void addVacation() throws InterruptedException {
        Intent intent = new Intent(this, EditVacationActivity.class);
        startActivity(intent);
    }

    // update the vacation list when using up button
    @Override
    public void onResume() {
        super.onResume();
        setRecyclerView();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.vacation_recycler);
        repo  = new VacationRepository(getApplication());
        List<Vacation> vacations;
        try {
            vacations = repo.getVacationsByUser(currentUser);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(vacations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vacation_list_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.search) {
            onSearchRequested();
            return true;
        }
        else if (item.getItemId() == R.id.report) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.sample_data) {
            try {
                createSampleDate();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    private void createSampleDate() throws InterruptedException {
        repo = new VacationRepository(getApplication());

        Vacation vacation = new Vacation("Las Vegas", "Hilton", "9/10/2024", "9/17/2024", "Gambling fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Disney Land", "Magic Castle", "9/19/2024", "9/22/2024", "Family fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Grand Canyon", "Colorado Castle", "10/10/2024", "10/27/2024", "Big hole fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Paris", "Louvre", "11/05/2024", "11/16/2024", "Weird food fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Bora Bora", "Treehouse", "12/19/2024", "12/22/2024", "Volcanic fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Glacier Park", "Ice Berg Motel", "1/1/2025", "1/9/2025", "Cold fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Rome", "St. Peter's Basilica", "2/22/2025", "2/28/2025", "Ancient fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Swiss Alps", "St Moritz", "3/5/2025", "3/18/2025", "Skiing fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("Maui", "Haleakala Hotel", "4/8/2025", "4/11/2025", "Luau fun times", currentUser);
        repo.addVacation(vacation);
        vacation = new Vacation("London", "Tower of London", "5/1/2025", "11/2/2048", "Prison fun times", currentUser);
        repo.addVacation(vacation);

        setRecyclerView();
    }
}
