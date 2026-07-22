package com.example.d308vacation.UI.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    IDateSetListener dateSetListener;

    public void setIDateSetListener(IDateSetListener listener) {
        dateSetListener = listener;
    }

    public interface IDateSetListener {
        void processDatePickerResult(int year, int month, int day) throws ParseException;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use current date as default
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (dateSetListener != null) {
            try {
                dateSetListener.processDatePickerResult(year, month, day);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}