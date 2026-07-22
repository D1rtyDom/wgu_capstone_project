package com.example.d308vacation.UI;

import android.app.AlertDialog;
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

import com.example.d308vacation.R;
import com.example.d308vacation.UI.fragment.DatePickerFragment;
import com.example.d308vacation.database.VacationRepository;
import com.example.d308vacation.model.Excursion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditExcursionActivity extends AppCompatActivity implements DatePickerFragment.IDateSetListener {

    private String name;
    private String date;
    private String description;
    private VacationRepository repo;
    private EditText nameTV;
    private EditText descriptionTV;
    private Button dateBtn;
    private boolean isAddNewExcursion = true;
    private Excursion excursion;
    private long vacationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_excursion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repo = new VacationRepository(getApplication());

        // set views
        nameTV = findViewById(R.id.edit_excursion_name);
        descriptionTV = findViewById(R.id.edit_excursion_description);
        dateBtn = findViewById(R.id.excursion_date_button);

        // check if editing existing excursion and populate view accordingly
        long excursionId = getIntent().getLongExtra("excursionId", -1);
        if (excursionId != -1) {
            // set if excursion is edit or new
            isAddNewExcursion = false;
            try {
                populateViews(excursionId);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // get vacationId
        if (isAddNewExcursion) {
            vacationId = getIntent().getLongExtra("vacationId", -1);
        }
        else {
            vacationId = excursion.getVacationId();
        }

        findViewById(R.id.excursion_date_button).setOnClickListener(view -> {
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setIDateSetListener(this);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void populateViews(long id) throws InterruptedException {
        excursion = repo.getExcursion(id);
        nameTV.setText(excursion.getName());
        descriptionTV.setText(excursion.getDescription());
        dateBtn.setText(excursion.getDate());
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
            // collect info from edit fields
            name = nameTV.getText().toString().trim();
            description = descriptionTV.getText().toString().trim();
            date = dateBtn.getText().toString();

            try {
                if (isValidDate(date)) {
                    if (!name.isEmpty()) {
                        if (isAddNewExcursion) {
                            excursion = new Excursion(name, date, description, vacationId);
                            try {
                                repo.addExcursion(excursion);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            excursion.setName(name);
                            excursion.setDescription(description);
                            excursion.setDate(date);
                            try {
                                repo.updateExcursion(excursion);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        finish();
                    }
                    else {
                        Toast.makeText(this, "Please enter an excursion name", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please enter a valid date", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;

        }
        else if (item.getItemId() == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure")
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        if (excursion != null) {
                            try {
                                repo.deleteExcursion(excursion);
                            }
                            catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        finish();
                    })
                    .setNegativeButton("Cancel", null);

            AlertDialog mDialog = builder.create();
            mDialog.show();

            return true;
        }

        return false;
    }
    // from DatePicker
    @Override
    public void processDatePickerResult(int year, int month, int day) {

        // stringify ints
        String month_str = Integer.toString(month + 1);
        String day_str = Integer.toString(day);
        String year_str = Integer.toString(year);

        date = (month_str + "/" + day_str + "/" + year_str);
        Button button = findViewById(R.id.excursion_date_button);
        button.setText(date);
    }

    public boolean isValidDate(String excursionDate) throws ParseException, InterruptedException {

        // get vacation start and end dates
        com.example.d308vacation.model.Vacation vacation = repo.getVacation(vacationId);
        String startDate = vacation.getStartDate();
        String endDate = vacation.getEndDate();

        // set string to Date and compare to make sure date is within vacation start and end dates
        // check if dates have been set
        if (!excursionDate.equals("Date") && !startDate.equals("Start Date") && !endDate.equals("End Date")) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date excursionFormattedDate = format.parse(excursionDate);
            Date startFormattedDate = format.parse(startDate);
            Date endFormattedDate = format.parse(endDate);

            return excursionFormattedDate.before(endFormattedDate) && excursionFormattedDate.after(startFormattedDate) || excursionFormattedDate.equals(startFormattedDate) || excursionFormattedDate.equals(endFormattedDate);
        }

        return false;
    }
}
