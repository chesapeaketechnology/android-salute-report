package com.chesapeaketechnology.salute;

import android.annotation.SuppressLint;

import com.chesapeaketechnology.salute.model.SaluteReport;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A collection of utilities for use throughout the Sync Monkey app.
 *
 * @since 0.1.0
 */
@SuppressWarnings("WeakerAccess")
public final class SaluteAppUtils
{
    // TODO: change to DateTimeFormatter when minimum API is 26
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat militaryFormat = new SimpleDateFormat("yyyy/MM/dd HHmm zzz");

    /**
     * Get the name of a file without the file extension or period.
     *
     * @param fileName File name to work on
     * @return file name without extension
     */
    public static String getNameWithoutExtension(String fileName)
    {
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1)
        {
            return fileName.substring(0, i);
        }
        return fileName;
    }

    /**
     * Extract the extension (with the period) from the given file name.
     *
     * @param fileName File name to process
     * @return file extension with the period, or null if no period or nothing after the period
     */
    public static String getExtension(String fileName)
    {
        String ext = null;
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1)
        {
            ext = fileName.substring(i).toLowerCase();
        }
        return ext;
    }

    /**
     * Returns a date formatted according to yyyy/MM/dd HHmm zzz.
     * Ex: 2020/02/30 1345 PST
     *
     * @param d The Date object
     * @return Formatted string
     */
    public static String formatDate(Date d)
    {
        if (d == null) return "N/A";
        return militaryFormat.format(d);
    }

    /**
     * Formats date in the same style of formatDate, or returns "Ongoing"
     *
     * @param report Salute report object
     * @return Formatted string
     */
    public static String formatReportTime(SaluteReport report)
    {
        if (report.getTimeOngoing() != null && report.getTimeOngoing())
        {
            return "Ongoing";
        }
        return formatDate(report.getTime());
    }
}
