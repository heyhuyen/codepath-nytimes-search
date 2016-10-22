package com.huyentran.nytsearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
 * Dialog Fragment for setting advanced search filter options.
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.filterSettings = Parcels.unwrap(getArguments().getParcelable(FILTER_SETTINGS_KEY));
        this.listener = (FilterOptionsFragmentListener) getActivity();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_filter_options, null);
        setupViews(view);
        alertDialogBuilder.setTitle(getResources().getString(R.string.filter_name));
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // on success
                        String beginDate = etBeginDate.getText().toString();
                        filterSettings.setBeginDate(beginDate);

                        filterSettings.setSortOrder(FilterSettings.SortOrder.valueOf(
                                spSortOrder.getSelectedItem().toString().toUpperCase()));

                        listener.onFinishFilterDialog(filterSettings);
                        dismiss();
                    }
                }
        );
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        return alertDialogBuilder.create();
    }

    /**
     * Wiring and setup of view and view-related components.
     */
    private void setupViews(View view) {
        setupBeginDate(view);
        setupSortSpinner(view);
        setupNewsDeskCheckboxes(view);
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

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }
}
