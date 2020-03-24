package com.chesapeaketechnology.salute;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chesapeaketechnology.salute.model.SaluteReport;

import java.util.Calendar;
import java.util.Date;

/**
 * This fragment allows the user to select the date and time associated with the SALUTE report.
 *
 * @since 0.1.0
 */
public class FifthFragmentTime extends Fragment
{
    private static final String LOG_TAG = FifthFragmentTime.class.getSimpleName();

    private SaluteReport saluteReport;
    private View view;
    private Button dateTimeButton;
    private CheckBox ongoingCheckBox;
    private TextView dateTimeValue;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private Date selectedTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_fifth_time, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        extractSaluteReport();

        dateTimeButton = view.findViewById(R.id.date_time_button);
        ongoingCheckBox = view.findViewById(R.id.ongoing_check_box);
        dateTimeValue = view.findViewById(R.id.date_time_value);

        ongoingCheckBox.setOnClickListener(checkBoxView -> ongoingCheckBoxToggled(((CheckBox) checkBoxView).isChecked()));
        dateTimeButton.setOnClickListener(view1 -> showDatePicker());

        view.findViewById(R.id.button_next).setOnClickListener(view1 -> updateSaluteReportAndPassOn());
    }

    /**
     * Pull the Salute Report from the fragment's arguments and set it as an instance variable.
     */
    private void extractSaluteReport()
    {
        final Bundle arguments = getArguments();
        if (arguments == null)
        {
            Log.wtf(LOG_TAG, "The arguments bundle was null when creating the Time Fragment (Fifth Fragment)");
        } else
        {
            saluteReport = FifthFragmentTimeArgs.fromBundle(arguments).getSaluteReport();
        }
    }

    /**
     * Called when interaction occurs with the Ongoing check box.
     * <p>
     * The enabled state of the date/time selection buttons will be updated accordingly.
     */
    private void ongoingCheckBoxToggled(boolean selected)
    {
        dateTimeButton.setEnabled(!selected);

        if (selected)
        {
            dateTimeValue.setText(R.string.ongoing);
        } else
        {
            dateTimeValue.setText(selectedTime == null ? "" : selectedTime.toString());
        }
    }

    /**
     * Display a date picker to the user.  After this date picker is closed, the time picker will be displayed next.
     */
    private void showDatePicker()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    month = monthOfYear;
                    this.year = year;
                    day = dayOfMonth;
                    showTimePicker();
                }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Display a time picker to the user.  Default the values to the current time.
     */
    private void showTimePicker()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, pHour, pMinute) -> {
                    hour = pHour;
                    minute = pMinute;

                    updateSelectedDateTime();
                }, hour, minute, true);

        timePickerDialog.show();
    }

    /**
     * Update the time using the latest date/time values from the user.
     */
    private void updateSelectedDateTime()
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        selectedTime = calendar.getTime();

        dateTimeValue.setText(selectedTime.toString());
    }

    /**
     * Update the Salute Report with the information the user provided in this fragment's UI, and then pass it along to
     * the next step in the creation wizard.
     */
    private void updateSaluteReportAndPassOn()
    {
        if (ongoingCheckBox.isSelected())
        {
            saluteReport.setTimeOngoing(true);
        }
        {
            saluteReport.setTime(selectedTime);
        }

        final FifthFragmentTimeDirections.ActionTimeToEquipment action =
                FifthFragmentTimeDirections.actionTimeToEquipment(saluteReport);

        NavHostFragment.findNavController(FifthFragmentTime.this).navigate(action);
    }
}
