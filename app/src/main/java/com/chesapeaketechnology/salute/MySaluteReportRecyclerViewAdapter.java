package com.chesapeaketechnology.salute;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chesapeaketechnology.salute.model.SaluteReport;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SaluteReport} and makes a call to the
 * specified {@link SaluteReportInteractionListener}.
 *
 * @since 0.1.0
 */
public class MySaluteReportRecyclerViewAdapter extends RecyclerView.Adapter<MySaluteReportRecyclerViewAdapter.ViewHolder>
{
    private final List<SaluteReport> mValues;
    private final SaluteReportInteractionListener mListener;

    MySaluteReportRecyclerViewAdapter(List<SaluteReport> items, SaluteReportInteractionListener listener)
    {
        mValues = items;
        mListener = listener;
    }

    @NotNull
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
        holder.mContentView.setText(truncateDescription(holder.saluteReport.getActivity(),100));
        holder.mCreatedView.setText(formatDate(holder.saluteReport.getReportCreationTime()));

        String time = formatDate(holder.saluteReport.getTime());;
        if (holder.saluteReport.getTimeOngoing() != null && holder.saluteReport.getTimeOngoing())
        {
            time = "Ongoing";
        }
        holder.mTimeView.setText(time);

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
     * Returns a date formatted according to yyyy/MM/dd HHMM zzz.
     * @param d The Date object
     * @return Formatted string
     */
    private String formatDate(Date d)
    {
        if (d == null) return "N/A";

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat militaryFormat = new SimpleDateFormat("yyyy/MM/dd HHMM zzz");
        return militaryFormat.format(d);
    }

    /**
     * Cuts off string at given length with ellipsis
     * @param desc
     * @return Truncated string
     */
    private String truncateDescription(String desc, int len)
    {
        if (desc.length() <= len)  return desc;
        return desc.substring(0, len) + "...";
    }

    /**
     * Delete report from list and filesystem.
     * @param position index of report
     */
    private void deleteReport(ViewHolder holder, int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.mView.getContext());
        builder.setTitle("Delete report?")
                .setMessage("Are you sure you want to delete this SALUTE report?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mValues.get(position).getFile().delete();
                        mValues.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, mValues.size());
                    }
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

        @NotNull
        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
