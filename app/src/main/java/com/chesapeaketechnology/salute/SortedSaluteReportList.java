package com.chesapeaketechnology.salute;

import androidx.annotation.NonNull;

import com.chesapeaketechnology.salute.model.SaluteReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Always-sorted list of {@link SaluteReport}.
 * It's just a wrapper around ArrayList, and aside from the sorting behavior, behaves exactly like one.
 *
 * @since 0.1.1
 */
public class SortedSaluteReportList extends ArrayList<SaluteReport>
{
    @Override
    public boolean add(SaluteReport saluteReport)
    {
        boolean b = super.add(saluteReport);
        Collections.sort(this);
        return b;
    }

    @Override
    public void add(int index, SaluteReport element)
    {
        super.add(index, element);
        Collections.sort(this);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends SaluteReport> c)
    {
        boolean listChanged = super.addAll(c);
        Collections.sort(this);
        return listChanged;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends SaluteReport> c)
    {
        boolean listChanged = super.addAll(index, c);
        Collections.sort(this);
        return listChanged;
    }
}
