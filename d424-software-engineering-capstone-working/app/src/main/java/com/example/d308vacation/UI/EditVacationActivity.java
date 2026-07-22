package com.example.d308vacation.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.d308vacation.R;
import com.example.d308vacation.database.VacationRepository;
import com.example.d308vacation.model.Vacation;
import com.example.d308vacation.UI.fragment.DatePickerFragment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditVacationActivity extends AppCompatActivity implements DatePickerFragment.IDateSetListener {

    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String accommodations;
    private VacationRepository repo;
    private EditText nameTV;
    private EditText descriptionTV;
    private EditText accommodationsTV;
    private Button startDateBtn;
    private Button endDateBtn;
    private boolean isStartDate;
    private boolean isAddNewVacation = true;
    private Vacation vacation;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_vacation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new VacationRepository(getApplication());

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

        // set views
        nameTV = findViewById(R.id.edit_vacation_name);
        accommodationsTV = findViewById(R.id.edit_vacation_accommodations);
        descriptionTV = findViewById(R.id.edit_vacation_description);
        startDateBtn = findViewById(R.id.vacation_start_date_button);
        endDateBtn = findViewById(R.id.vacation_end_date_button);

        // check if editing existing vacation and populate views accordingly
        long vacationId = getIntent().getLongExtra("vacationId", -1);
        if (vacationId != -1) {
            // set if vacation is edit or new
            isAddNewVacation = false;
            try {
                populateViews(vacationId);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        findViewById(R.id.vacation_start_date_button).setOnClickListener(view -> {
            isStartDate = true;
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setIDateSetListener(this);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });

        findViewById(R.id.vacation_end_date_button).setOnClickListener(view -> {
            isStartDate = false;
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setIDateSetListener(this);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void populateViews(long id) throws InterruptedException {
        vacation = repo.getVacation(id);
        nameTV.setText(vacation.getName());
        descriptionTV.setText(vacation.getDescription());
        accommodationsTV.setText(vacation.getAccommodationName());
        startDateBtn.setText(vacation.getStartDate());
        endDateBtn.setText(vacation.getEndDate());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        else if (item.getItemId() == R.id.save) {
            // Collect info from edit fields
            name = nameTV.getText().toString().trim();
            accommodations = accommodationsTV.getText().toString().trim();
            description = descriptionTV.getText().toString().trim();
            startDate = startDateBtn.getText().toString();
            endDate = endDateBtn.getText().toString();

            // Check to see if startDate is before endDate / vice versa and assign variables and text accordingly
            try {
                if (isValidDate(startDate, endDate)) {
                    if (!name.isEmpty()) {
                        if (isAddNewVacation) {
                            vacation = new Vacation(name, accommodations, startDate, endDate, description, currentUser);
                            try {
                                repo.addVacation(vacation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            vacation.setName(name);
                            vacation.setAccommodationName(accommodations);
                            vacation.setDescription(description);
                            vacation.setStartDate(startDate);
                            vacation.setEndDate(endDate);
                            // We don't change the username during update as it's already linked
                            try {
                                repo.updateVacation(vacation);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Please enter a vacation name", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        return false;
    }

    // from DatePicker
    @Override
    public void processDatePickerResult(int year, int month, int day) throws ParseException {
        // stringify ints
        String month_str = Integer.toString(month + 1);
        String day_str = Integer.toString(day);
        String year_str = Integer.toString(year);
        String date = (month_str + "/" + day_str + "/" + year_str);


        if (isStartDate) {
            startDate = date;
            Button button = findViewById(R.id.vacation_start_date_button);
            button.setText(startDate);
        } else {
            endDate = date;
            Button button = findViewById(R.id.vacation_end_date_button);
            button.setText(endDate);
        }

    }

    public boolean isValidDate(String startDate, String endDate) throws ParseException {

        // set strings to Date and compare to make sure start is before end
        // check if dates have been set
        if (!startDate.equals("Start Date") && !endDate.equals("End Date")) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date startFormattedDate = format.parse(startDate);
            Date endFormattedDate = format.parse(endDate);

            return startFormattedDate.before(endFormattedDate);
        }

        return false;
    }
}
