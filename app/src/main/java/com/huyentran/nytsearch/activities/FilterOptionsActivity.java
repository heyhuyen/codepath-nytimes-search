package com.huyentran.nytsearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.fragments.DatePickerFragment;
import com.huyentran.nytsearch.model.FilterSettings;
import com.huyentran.nytsearch.utils.DateUtils;

import java.util.Calendar;
import java.util.HashSet;

import static com.huyentran.nytsearch.utils.Constants.DATE_PICKER_KEY;
import static com.huyentran.nytsearch.utils.Constants.EMPTY;
import static com.huyentran.nytsearch.utils.Constants.FILTER_SETTINGS_KEY;
import static com.huyentran.nytsearch.utils.Constants.NEWS_DESK_ARTS;
import static com.huyentran.nytsearch.utils.Constants.NEWS_DESK_FASHION;
import static com.huyentran.nytsearch.utils.Constants.NEWS_DESK_SPORTS;

/**
 * Activity for setting advanced search filter options.
 */
public class FilterOptionsActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener{

    private EditText etBeginDate;
    private ImageButton btnClearBeginDate;
    private Spinner spSortOrder;
    private CheckBox cbArts;
    private CheckBox cbFashion;
    private CheckBox cbSports;

    private FilterSettings filterSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.filterSettings =
                (FilterSettings) getIntent().getSerializableExtra(FILTER_SETTINGS_KEY);

        setupViews();
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews() {
        setupBeginDate();
        setupSortSpinner();
        setupNewsDeskCheckboxes();
    }

    private void setupBeginDate() {
        this.etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        String beginDate = this.filterSettings.getBeginDate();
        if (beginDate != null && !TextUtils.isEmpty(beginDate)) {
            this.etBeginDate.setText(beginDate);
        }
        this.etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        this.btnClearBeginDate = (ImageButton) findViewById(R.id.btnClearBeginDate);
        this.btnClearBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear begin date text
                etBeginDate.setText(EMPTY);
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment =
                DatePickerFragment.newInstance(this.etBeginDate.getText().toString());
        newFragment.show(getSupportFragmentManager(), DATE_PICKER_KEY);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        this.etBeginDate.setText(DateUtils.getStringFromDate(c));
    }

    private void setupSortSpinner() {
        this.spSortOrder = (Spinner) findViewById(R.id.spSortOrder);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_order_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spSortOrder.setAdapter(adapter);
        FilterSettings.SortOrder sortSetting = this.filterSettings.getSortOrder();
        this.spSortOrder.setSelection(sortSetting.ordinal());
    }

    private void setupNewsDeskCheckboxes() {
        this.cbArts = (CheckBox) findViewById(R.id.cbArts);
        this.cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        this.cbSports = (CheckBox) findViewById(R.id.cbSports);

        HashSet<String> newsDeskValues = this.filterSettings.getNewsDeskValues();
        if (newsDeskValues.contains(NEWS_DESK_ARTS)) {
            this.cbArts.setChecked(true);
        }
        if (newsDeskValues.contains(NEWS_DESK_FASHION)) {
            this.cbFashion.setChecked(true);
        }
        if (newsDeskValues.contains(NEWS_DESK_SPORTS)) {
            this.cbSports.setChecked(true);
        }

        this.cbArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbArts.isChecked()) {
                    filterSettings.addNewsDeskValue(NEWS_DESK_ARTS);
                } else {
                    filterSettings.removeNewsDeskValue(NEWS_DESK_ARTS);
                }
            }
        });
        this.cbFashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbArts.isChecked()) {
                    filterSettings.addNewsDeskValue(NEWS_DESK_FASHION);
                } else {
                    filterSettings.removeNewsDeskValue(NEWS_DESK_FASHION);
                }
            }
        });
        this.cbSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSports.isChecked()) {
                    filterSettings.addNewsDeskValue(NEWS_DESK_SPORTS);
                } else {
                    filterSettings.removeNewsDeskValue(NEWS_DESK_SPORTS);
                }
            }
        });
    }

    public void onSave(View view) {
        String beginDate = this.etBeginDate.getText().toString();
        this.filterSettings.setBeginDate(beginDate);

        this.filterSettings.setSortOrder(FilterSettings.SortOrder.valueOf(
                this.spSortOrder.getSelectedItem().toString().toUpperCase()));

        Intent data = new Intent();
        data.putExtra(FILTER_SETTINGS_KEY, this.filterSettings);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
