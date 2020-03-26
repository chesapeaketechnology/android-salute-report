package com.chesapeaketechnology.salute;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        holder.mTimeView.setText(formatDate(holder.saluteReport.getTime()));
        holder.mCreatedView.setText(formatDate(holder.saluteReport.getReportCreationTime()));

        holder.mView.setOnClickListener(v -> {
            if (null != mListener) mListener.onReportSelected(holder.saluteReport);
        });
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
        SaluteReport saluteReport;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.report_name);
            mContentView = view.findViewById(R.id.content);
            mTimeView = view.findViewById(R.id.time);
            mCreatedView = view.findViewById(R.id.created);
        }

        @NotNull
        @Override
        public String toString()
        {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
