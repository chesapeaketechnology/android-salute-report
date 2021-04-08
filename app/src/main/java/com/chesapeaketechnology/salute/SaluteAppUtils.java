package com.chesapeaketechnology.salute;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.chesapeaketechnology.salute.model.SaluteReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A collection of utilities for use throughout the Sync Monkey app.
 *
 * @since 0.1.0
 */
@SuppressWarnings("WeakerAccess")
public final class SaluteAppUtils
{
    private static final String LOG_TAG = SaluteAppUtils.class.getSimpleName();

    // TODO: change to DateTimeFormatter when minimum API is 26
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat militaryFormat = new SimpleDateFormat("yyyy/MM/dd HHmm zzz");

    private static final String shareMimeType = "application/json";

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

    /**
     * If argument is empty string, return "N/A", otherwise return string
     *
     * @param s String to check
     * @return String passed in or "N/A"
     */
    public static String stringOrNa(String s)
    {
        return (s == null || s.isEmpty()) ? "N/A" : s;
    }

    private static void openShareSaluteReportDialog(Intent sharingIntent, Context context)
    {
        sharingIntent.setType(shareMimeType);
        context.startActivity(Intent.createChooser(sharingIntent, null));
    }

    /**
     * @return The File object representing the app's directory where temp .txt files are stored.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getTempShareFilesDir(Context context)
    {
        @SuppressWarnings("ConstantConditions") final File sharedFilesTempDirectory = new File(context.getFilesDir(), SaluteAppConstants.SHARE_FILES_TEMP_DIRECTORY);
        if (!sharedFilesTempDirectory.exists()) sharedFilesTempDirectory.mkdir();
        return sharedFilesTempDirectory;
    }

    /**
     * Returns the Uri for a file, granting permissions if necessary.
     *
     * @param file    The File object
     * @param context Android application context
     * @return File Uri object
     */
    private static Uri getFileUri(File file, Context context)
    {
        Uri uri = FileProvider.getUriForFile(context, SaluteAppConstants.AUTHORITY, file);
        context.grantUriPermission(context.getPackageName(), uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return uri;
    }

    /**
     * Formats the SALUTE report as human-readable plaintext and saves it to a file.
     *
     * @return The text file's File object
     * @since 0.1.1
     */
    public static File formatAndSaveAsTextFile(SaluteReport report, Context context)
    {
        File reportFile = report.getFile();
        if (reportFile == null)
        {
            Log.wtf(LOG_TAG, "File does not exist");
        }

        final String filenameWithoutExtension
                = SaluteAppUtils.getNameWithoutExtension(reportFile.getName());
        final File txtFile = new File(SaluteAppUtils.getTempShareFilesDir(context), filenameWithoutExtension + ".txt");

        try (final FileOutputStream stream = new FileOutputStream(txtFile))
        {
            stream.write(report.formatAsHumanReadableString().getBytes());
        } catch (IOException e)
        {
            Log.e(LOG_TAG, "Error writing to text file:", e);
        }

        return txtFile;
    }

    /**
     * Serializes a provided salute report object as JSON and opens
     * the standard Android share dialog. Alias for openShareSaluteReportDialog
     * called with a list of one report.
     *
     * @param report  SaluteReport object to share
     * @param context Android application context
     */
    public static void openShareSaluteReportDialog(SaluteReport report, Context context)
    {
        openShareSaluteReportDialog(Collections.singletonList(report), context);
    }

    /**
     * Serializes a provided list of salute report objects
     * as JSON and opens the standard Android share dialog.
     *
     * @param reports List of SaluteReport objects to share
     * @param context Android application context
     */
    public static void openShareSaluteReportDialog(List<SaluteReport> reports, Context context)
    {
        Intent sharingIntent = new Intent();
        sharingIntent.setType(shareMimeType);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (reports.size() > 1)
        {
            ArrayList<Uri> reportUris = reports
                    .stream()
                    .map(report -> getFileUri(formatAndSaveAsTextFile(report, context), context))
                    .collect(Collectors.toCollection(ArrayList::new));

            sharingIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, reportUris);
        } else
        {
            sharingIntent.setAction(Intent.ACTION_SEND);
            sharingIntent.putExtra(Intent.EXTRA_STREAM,
                    getFileUri(formatAndSaveAsTextFile(reports.get(0), context), context));
        }

        context.startActivity(Intent.createChooser(sharingIntent, null));
    }
}
