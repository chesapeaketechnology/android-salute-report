package com.chesapeaketechnology.salute;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chesapeaketechnology.salute.model.SaluteReport;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SaluteReport} and makes a call to the
 * specified {@link SaluteReportInteractionListener}.
 *
 * @since 0.1.0
 */
public class MySaluteReportRecyclerViewAdapter extends RecyclerView.Adapter<MySaluteReportRecyclerViewAdapter.ViewHolder>
{
    private static final int ACTIVITY_MAX_LENGTH = 100;
    private static final String LOG_TAG = MySaluteReportRecyclerViewAdapter.class.getSimpleName();

    private final List<SaluteReport> mValues;
    private final SaluteReportInteractionListener mListener;

    /**
     * Indicates if the list of reports is in selection mode
     */
    private boolean selectionModeActive = false;

    MySaluteReportRecyclerViewAdapter(List<SaluteReport> items, SaluteReportInteractionListener listener)
    {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_salute_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.saluteReport = mValues.get(position);
        holder.mIdView.setText(holder.saluteReport.getReportName());
        holder.mContentView.setText(truncateDescription(holder.saluteReport.getActivity(), ACTIVITY_MAX_LENGTH));
        holder.mCreatedView.setText(
                SaluteAppUtils.formatDate(holder.saluteReport.getReportCreationTime()));
        holder.mTimeView.setText(SaluteAppUtils.formatReportTime(holder.saluteReport));

        holder.mCheckBox.setVisibility(selectionModeActive ? View.VISIBLE : View.INVISIBLE);

        // De-select all reports once on exit selection mode
        if (!selectionModeActive) setItemSelected(holder, false);

        holder.mView.setOnClickListener(v -> {
            if (selectionModeActive)
            {
                toggleItemSelected(holder);
            } else if (mListener != null)
            {
                mListener.onReportSelected(holder.saluteReport);
            }
        });

        holder.mView.setOnLongClickListener(v -> {
            setSelectionModeActive(true);
            toggleItemSelected(holder);
            return true;
        });

        holder.mCheckBox.setOnClickListener(v -> toggleItemSelected(holder));
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    /**
     * Allows the user to select multiple reports, or turns off selection,
     * and updates styles and state accordingly.
     *
     * @param active State to set selection mode
     */
    public void setSelectionModeActive(boolean active)
    {
        selectionModeActive = active;
        notifyDataSetChanged();

        if (mListener != null) mListener.onReportsSelectionModeChanged(active);
    }

    /**
     * Indicates if selection mode is active or not.
     *
     * @return Bool indicating if selection mode is on
     */
    public boolean getSelectionModeActive()
    {
        return selectionModeActive;
    }

    /**
     * Updates fields and styles when the user selects a report
     * in multiple-selection mode.
     *
     * @param holder   ViewHolder of the report
     * @param selected Indicates if report is selected or not
     */
    private void setItemSelected(ViewHolder holder, boolean selected)
    {
        holder.saluteReport.setSelected(selected);

        if (selected)
        {
            holder.mView.setBackgroundColor(Color.LTGRAY);
            holder.mCheckBox.setChecked(true);
        } else
        {
            holder.mView.setBackgroundColor(Color.TRANSPARENT);
            holder.mCheckBox.setChecked(false);
        }
    }

    /**
     * Toggles selection of a report.
     *
     * @param holder ViewHolder for report
     */
    private void toggleItemSelected(ViewHolder holder)
    {
        setItemSelected(holder, !holder.saluteReport.getSelected());
    }

    /**
     * Prompts and then deletes every report currently selected by the user.
     * Will turn  off selection mode after the report(s) have been deleted.
     *
     * @param context Android application context
     */
    public void deleteAllSelectedReports(Context context)
    {
        List<SaluteReport> selectedReports = getSelectedReports();
        if (selectedReports.size() <= 0)
        {
            warnNoSelectedReports(context);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_report_dialog_title)
                .setMessage(R.string.delete_report_dialog_message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Iterator<SaluteReport> iterator = selectedReports.iterator();
                    while (iterator.hasNext())
                    {
                        SaluteReport report = iterator.next();
                        File file = report.getFile();
                        if (file != null)
                        {
                            file.delete();
                        } else
                        {
                            Log.wtf(LOG_TAG, "Salute report has a null file.");
                        }
                        mValues.remove(report);
                    }

                    setSelectionModeActive(false);
                    notifyDataSetChanged();
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Serializes the provided salute reports objects as
     * JSON and opens the standard Android share dialog.
     *
     * @param context Android application context
     */
    public void shareAllSelectedReports(Context context)
    {
        List<SaluteReport> selectedReports = getSelectedReports();

        if (selectedReports.size() > 0)
        {
            SaluteAppUtils.openShareSaluteReportDialog(selectedReports, context);
            notifyDataSetChanged();
        } else
        {
            warnNoSelectedReports(context);
        }
    }

    /**
     * Shows alert warning user that they attempted
     * to perform action without any selected reports
     *
     * @param context Android application context
     */
    private void warnNoSelectedReports(Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.none_selected_dialog_title)
                .setMessage(R.string.none_selected_dialog_message)
                .setPositiveButton(android.R.string.yes, null)
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Cuts off string at given length with ellipsis
     *
     * @param description String to truncate
     * @param length      Number of characters to leave before truncating with '...'
     * @return Truncated string
     */
    private String truncateDescription(String description, int length)
    {
        if (description.length() <= length) return description;
        return description.substring(0, length) + "...";
    }

    /**
     * Returns list of selected reports.
     *
     * @return List of selected reports.
     */
    private List<SaluteReport> getSelectedReports()
    {
        List<SaluteReport> selectedReports = new ArrayList<>(mValues);
        selectedReports.removeIf(r -> !r.getSelected());
        return selectedReports;
    }

    /**
     * The representation for a single salute report in the recycler view.  This class holds the view elements for the
     * report.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        final TextView mTimeView;
        final TextView mCreatedView;
        final CheckBox mCheckBox;
        SaluteReport saluteReport;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.report_name);
            mContentView = view.findViewById(R.id.content);
            mTimeView = view.findViewById(R.id.time);
            mCreatedView = view.findViewById(R.id.created);
            mCheckBox = view.findViewById(R.id.selected_checkbox);
        }

        @NonNull
        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
