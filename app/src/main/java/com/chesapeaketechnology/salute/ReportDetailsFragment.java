package com.chesapeaketechnology.salute;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chesapeaketechnology.salute.model.SaluteReport;

/**
 * The fragment that appears on clicking through to view a SALUTE report.
 *
 * @since 0.1.0
 */
public class ReportDetailsFragment extends Fragment
{
    private static final String LOG_TAG = ReportDetailsFragment.class.getSimpleName();

    private SaluteReport saluteReport;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

        reportLabel.setText(SaluteAppUtils.stringOrNa(saluteReport.getReportName()));
        size.setText(SaluteAppUtils.stringOrNa(saluteReport.getSize()));
        activity.setText(SaluteAppUtils.stringOrNa(saluteReport.getActivity()));
        location.setText(SaluteAppUtils.stringOrNa(saluteReport.getLocationString()));
        unit.setText(SaluteAppUtils.stringOrNa(saluteReport.getUnit()));
        time.setText(SaluteAppUtils.formatReportTime(saluteReport));
        equipment.setText(SaluteAppUtils.stringOrNa(saluteReport.getEquipment()));
        remarks.setText(SaluteAppUtils.stringOrNa(saluteReport.getRemarks()));
        created.setText(SaluteAppUtils.formatDate(saluteReport.getReportCreationTime()));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_report_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.share_button)
        {
            SaluteAppUtils.openShareSaluteReportDialog(saluteReport, requireContext());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Pull the Salute Report from the fragment's arguments and set it as an instance variable.
     */
    private void extractSaluteReport()
    {
        final Bundle arguments = getArguments();
        if (arguments == null)
        {
            Log.wtf(LOG_TAG, "The arguments bundle was null when creating the Report Details Fragment");
        } else
        {
            saluteReport = ReportDetailsFragmentArgs.fromBundle(arguments).getSaluteReport();
        }
    }
}
