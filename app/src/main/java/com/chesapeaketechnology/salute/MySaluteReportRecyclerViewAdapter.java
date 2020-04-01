package com.chesapeaketechnology.salute;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chesapeaketechnology.salute.model.SaluteReport;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SaluteReport} and makes a call to the
 * specified {@link SaluteReportInteractionListener}.
 *
 * @since 0.1.0
 */
public class MySaluteReportRecyclerViewAdapter extends RecyclerView.Adapter<MySaluteReportRecyclerViewAdapter.ViewHolder>
{
    public static final int ACTIVITY_MAX_LENGTH = 100;
    private static final String LOG_TAG = MySaluteReportRecyclerViewAdapter.class.getSimpleName();

    private final List<SaluteReport> mValues;
    private final SaluteReportInteractionListener mListener;

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

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) mListener.onReportSelected(holder.saluteReport);
        });

        holder.mDeleteButton.setOnClickListener(v -> deleteReport(holder, position));
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
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
     * Delete report from list and filesystem.
     *
     * @param position index of report
     */
    private void deleteReport(ViewHolder holder, int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.mView.getContext());
        builder.setTitle(R.string.delete_report_dialog_title)
                .setMessage(R.string.delete_report_dialog_message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    File file = mValues.get(position).getFile();
                    if (file != null)
                    {
                        file.delete();
                    } else
                    {
                        Log.wtf(LOG_TAG, "Salute report has a null file.");
                    }
                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
        final ImageButton mDeleteButton;
        SaluteReport saluteReport;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.report_name);
            mContentView = view.findViewById(R.id.content);
            mTimeView = view.findViewById(R.id.time);
            mCreatedView = view.findViewById(R.id.created);
            mDeleteButton = view.findViewById(R.id.deleteButton);
        }

        @NonNull
        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
