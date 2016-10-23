package com.huyentran.nytsearch.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.huyentran.nytsearch.R;
import com.huyentran.nytsearch.model.FilterSettings;
import com.huyentran.nytsearch.utils.DateUtils;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.HashSet;

import static com.huyentran.nytsearch.utils.Constants.DATE_PICKER_KEY;
import static com.huyentran.nytsearch.utils.Constants.EMPTY;
import static com.huyentran.nytsearch.utils.Constants.FILTER_SETTINGS_KEY;

/**
 * Dialog Fragment for setting advanced icon_search filter options.
 */
public class FilterOptionsDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private static final int DATE_PICKER_REQUEST = 500;
    private static final String NEWS_DESK_ARTS = "Arts";
    private static final String NEWS_DESK_FASHION = "Fashion & Style";
    private static final String NEWS_DESK_SPORTS = "Sports";

    private EditText etBeginDate;
    private Spinner spSortOrder;
    private CheckBox cbArts;
    private CheckBox cbFashion;
    private CheckBox cbSports;

    private FilterSettings filterSettings;
    private FilterOptionsFragmentListener listener;

    public interface FilterOptionsFragmentListener {
        void onFinishFilterDialog(FilterSettings filterSettings);
    }

    public FilterOptionsDialogFragment() {
        // empty constructor
    }

    public static FilterOptionsDialogFragment newInstance(FilterSettings filterSettings) {
        FilterOptionsDialogFragment fragment = new FilterOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(FILTER_SETTINGS_KEY, Parcels.wrap(filterSettings));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_options, container);
        this.filterSettings = Parcels.unwrap(getArguments().getParcelable(FILTER_SETTINGS_KEY));
        this.listener = (FilterOptionsFragmentListener) getActivity();
        setupViews(view);
        getDialog().setTitle(getResources().getString(R.string.filter_name));
        return view;
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews(View view) {
        setupBeginDate(view);
        setupSortSpinner(view);
        setupNewsDeskCheckboxes(view);
        setupButtons(view);
    }

    private void setupBeginDate(View view) {
        this.etBeginDate = (EditText) view.findViewById(R.id.etBeginDate);
        String beginDate = this.filterSettings.getBeginDate();
        if (!TextUtils.isEmpty(beginDate)) {
            this.etBeginDate.setText(beginDate);
        }
        this.etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        ImageButton btnClearBeginDate = (ImageButton) view.findViewById(R.id.btnClearBeginDate);
        btnClearBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear begin date text
                etBeginDate.setText(EMPTY);
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerFragment dpFragment =
                DatePickerFragment.newInstance(this.etBeginDate.getText().toString());
        dpFragment.setTargetFragment(FilterOptionsDialogFragment.this, DATE_PICKER_REQUEST);
        dpFragment.show(getFragmentManager(), DATE_PICKER_KEY);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        this.etBeginDate.setText(DateUtils.getStringFromDate(c));
    }

    private void setupSortSpinner(View view) {
        this.spSortOrder = (Spinner) view.findViewById(R.id.spSortOrder);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_order_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spSortOrder.setAdapter(adapter);
        FilterSettings.SortOrder sortSetting = this.filterSettings.getSortOrder();
        this.spSortOrder.setSelection(sortSetting.ordinal());
    }

    private void setupNewsDeskCheckboxes(View view) {
        this.cbArts = (CheckBox) view.findViewById(R.id.cbArts);
        this.cbFashion = (CheckBox) view.findViewById(R.id.cbFashion);
        this.cbSports = (CheckBox) view.findViewById(R.id.cbSports);

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
                if (cbFashion.isChecked()) {
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

    private void setupButtons(View view) {
        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterSettings.setBeginDate(etBeginDate.getText().toString());

                filterSettings.setSortOrder(FilterSettings.SortOrder.valueOf(
                        spSortOrder.getSelectedItem().toString().toUpperCase()));

                listener.onFinishFilterDialog(filterSettings);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
