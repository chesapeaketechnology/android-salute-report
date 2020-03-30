package com.chesapeaketechnology.salute;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chesapeaketechnology.salute.model.SaluteReport;

/**
 * The fragment that appears on clicking through to view a SALUTE report.
 */
public class ReportDetailsFragment extends Fragment
{
    private static final String LOG_TAG = ReportDetailsFragment.class.getSimpleName();

    private SaluteReport saluteReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_report_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        extractSaluteReport();

        TextView reportLabel = view.findViewById(R.id.reportLabel);
        TextView size = view.findViewById(R.id.size);
        TextView activity = view.findViewById(R.id.activity);
        TextView location = view.findViewById(R.id.location);
        TextView unit = view.findViewById(R.id.unit);
        TextView time = view.findViewById(R.id.time);
        TextView equipment = view.findViewById(R.id.equipment);
        TextView remarks = view.findViewById(R.id.remarks);
        TextView created = view.findViewById(R.id.created);

        reportLabel.setText(stringOrNA(saluteReport.getReportName()));
        size.setText(stringOrNA(saluteReport.getSize()));
        activity.setText(stringOrNA(saluteReport.getActivity()));
        location.setText(stringOrNA(saluteReport.getLocation()));
        unit.setText(stringOrNA(saluteReport.getUnit()));
        time.setText(SaluteAppUtils.formatReportTime(saluteReport));
        equipment.setText(stringOrNA(saluteReport.getEquipment()));
        remarks.setText(stringOrNA(saluteReport.getRemarks()));
        created.setText(SaluteAppUtils.formatDate(saluteReport.getReportCreationTime()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Pull the Salute Report from the fragment's arguments and set it as an instance variable.
     */
    private void extractSaluteReport()
    {
        final Bundle arguments = getArguments();
        if (arguments == null)
        {
            Log.wtf(LOG_TAG, "The arguments bundle was null when creating the Remarks Fragment (Seventh Fragment)");
        } else
        {
            saluteReport = ReportDetailsFragmentArgs.fromBundle(arguments).getSaluteReport();
        }
    }

    /**
     * If argument is empty string, return "N/A", otherwise return string
     * @param s String to check
     */
    private String stringOrNA(String s)
    {
        if (s == null || s.equals("")) return "N/A";
        return s;
    }
}
