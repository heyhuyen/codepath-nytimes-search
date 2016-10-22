package com.huyentran.nytsearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.DatePicker;

import com.huyentran.nytsearch.utils.DateUtils;

import java.util.Calendar;

/**
 * Date picker dialog fragment.
 */
public class DatePickerFragment extends DialogFragment {

    private static final String DATE_KEY = "date";
    private static final String MIN_DATE_STR = "1851-09-18";
    private static final Calendar MIN_DATE = DateUtils.getDateFromString(MIN_DATE_STR);
    private static final long MIN_DATE_MILLIS = MIN_DATE.getTimeInMillis();

    public DatePickerFragment() {
        // Empty constructor required for DialogFragment
    }

    public static DatePickerFragment newInstance(String date) {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(DATE_KEY, date);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Today's date
        Calendar today = Calendar.getInstance();

        // pre-set date, if applicable
        String dateString = getArguments().getString(DATE_KEY);
        Calendar date = Calendar.getInstance();
        if (dateString != null && !TextUtils.isEmpty(dateString)) {
            date = DateUtils.getDateFromString(dateString);
        }
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        // parent needs to implement this interface
        DatePickerDialog.OnDateSetListener listener =
                (DatePickerDialog.OnDateSetListener) getTargetFragment();

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                listener, year, month, day);

        // Set
        DatePicker datePicker = dialog.getDatePicker();
        datePicker.setMaxDate(today.getTimeInMillis());
        datePicker.setMinDate(MIN_DATE_MILLIS);

        return dialog;
    }
}
