package com.example.d308vacation.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacation.UI.adapter.VacationExcursionsAdapter;
import com.example.d308vacation.UI.receiver.MyReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.d308vacation.R;
import com.example.d308vacation.database.VacationRepository;
import com.example.d308vacation.model.Excursion;
import com.example.d308vacation.model.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;




public class VacationDetailActivity extends AppCompatActivity {

    String name;
    String description;
    String startDate;
    String endDate;
    String accommodations;
    long id;
    TextView nameTV;
    TextView descriptionTV;
    TextView datesTV;
    TextView accommodationsTV;
    List<Excursion> excursions;

    VacationRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new VacationRepository(getApplication());

        // find views
        findViewById(R.id.add_excursion_button).setOnClickListener(view -> addExcursion());

        nameTV = findViewById(R.id.vacation_name_text);
        descriptionTV = findViewById(R.id.vacation_description_text);
        datesTV = findViewById(R.id.vacation_dates_text);
        accommodationsTV = findViewById(R.id.vacation_accommodations_text);

        // get vacation info from vacation list
        id = getIntent().getLongExtra("id", -1);
        name = getIntent().getStringExtra("name");
        description = getIntent().getStringExtra("description");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        accommodations = getIntent().getStringExtra("accommodations");
        try {
            populateVacation();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // set info from edited vacation
        try {
            Vacation vacation = repo.getVacation(id);
            name = vacation.getName();
            description = vacation.getDescription();
            accommodations = vacation.getAccommodationName();
            startDate = vacation.getStartDate();
            endDate = vacation.getEndDate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            populateVacation();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateVacation() throws InterruptedException {

        nameTV.setText(name);
        descriptionTV.setText(description);
        datesTV.setText(getResources().getString(R.string.vacation_dates, startDate, endDate));
        accommodationsTV.setText(accommodations);

        excursions = repo.getVacationExcursions(id);

        // setup recycle list
        RecyclerView recyclerView = findViewById(R.id.vacation_excursion_recycler);
        final VacationExcursionsAdapter vacationExcursionsAdapter = new VacationExcursionsAdapter(this);
        recyclerView.setAdapter(vacationExcursionsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationExcursionsAdapter.setExcursions(excursions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_vacation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        else if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(this, EditVacationActivity.class);
            intent.putExtra("vacationId", id);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.share) {
            // build String with all vacation details
            StringBuilder infoToSend = new StringBuilder(name + "\n" +
                    "----------------\n" +
                    description + "\n" +
                    "From " + startDate + " to " + endDate + "\n" +
                    "Staying at: " + accommodations + "\n" +
                    "Excursions: \n" +
                    "----------------\n");

            List<Excursion> excursions = null;
            try {
                excursions = repo.getVacationExcursions(id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (excursions != null) {
                for (int i = 0; i < excursions.size(); i++) {
                    infoToSend.append("- ").append(excursions.get(i).getName()).append("\n");
                }
            }

            // send info via createChooser
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TITLE, "My Vacation Details");
            sentIntent.putExtra(Intent.EXTRA_TEXT, infoToSend.toString());
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }
        else if (item.getItemId() == R.id.notify) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("On which day would you like to receive a reminder?")
                    .setPositiveButton("Start Date", (dialogInterface, i) -> {
                        Date date;
                        try {
                            date = format.parse(startDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        long trigger = date.getTime();
                        Intent intent = new Intent(VacationDetailActivity.this, MyReceiver.class);
                        intent.putExtra("vacationAlert", name + " starts today!");
                        PendingIntent sender = PendingIntent.getBroadcast(VacationDetailActivity.this,  ++MainActivity.numAlert, intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);

                    })
                    .setNeutralButton("Cancel", null)
                    .setNegativeButton("End Date", ((dialogInterface, i) -> {
                        Date date;
                        try {
                            date = format.parse(endDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        long trigger = date.getTime();
                        Intent intent = new Intent(VacationDetailActivity.this, MyReceiver.class);
                        intent.putExtra("vacationAlert", name + " ends today!");
                        PendingIntent sender = PendingIntent.getBroadcast(VacationDetailActivity.this,  ++MainActivity.numAlert, intent, PendingIntent.FLAG_ONE_SHOT|PendingIntent.FLAG_IMMUTABLE);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
                    }));

            AlertDialog mDialog = builder.create();
            mDialog.show();
            return true;
        }
        else if (item.getItemId() == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setPositiveButton("Ok", (dialogInterface, i) -> {
                try {
                    // Make sure vacation has no excursions before deleting
                    if (repo.getVacationExcursions(id).isEmpty()) {
                        repo.deleteVacation(repo.getVacation(id));
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Excursions must be deleted first", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).setNegativeButton("Cancel", null);

            AlertDialog mDialog = builder.create();
            mDialog.show();
            return true;
        }
        else if (item.getItemId() == R.id.sample_excursions) {
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

        Excursion excursion = new Excursion("Go for a walk", "1/1/1999", "A long walk", id);
        repo.addExcursion(excursion);
        excursion = new Excursion("Take a nap", "1/1/1999", "A long nap", id);
        repo.addExcursion(excursion);
        excursion = new Excursion("Watch TV", "1/1/1999", "A lot of TV", id);
        repo.addExcursion(excursion);
        excursion = new Excursion("Read a book", "1/1/1999", "A long book", id);
        repo.addExcursion(excursion);
        excursion = new Excursion("Do work", "1/1/1999", "A lot of work", id);
        repo.addExcursion(excursion);

        populateVacation();
    }

    public void addExcursion() {
        Intent intent = new Intent(this, EditExcursionActivity.class);
        intent.putExtra("vacationId", id);
        startActivity(intent);
    }


}// end class