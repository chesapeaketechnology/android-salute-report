package com.chesapeaketechnology.salute;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chesapeaketechnology.salute.model.SaluteReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main fragment that lists all the existing SALUTE reports, and allows the user to create a new SALUTE report.
 *
 * @since 0.1.0
 */
public class HomeFragment extends Fragment implements SaluteReportInteractionListener
{
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    private final List<SaluteReport> saluteReports = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        findAndAddSaluteReports();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.list);
        final Context context = view.getContext();

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new MySaluteReportRecyclerViewAdapter(saluteReports, this));

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> startSaluteReportWizard(null));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        final Bundle arguments = getArguments();
        if (arguments == null)
        {
            Log.d(LOG_TAG, "The arguments bundle was null when creating the Home Fragment");
        } else
        {
            createSaluteReport(arguments, view);
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        Bundle arguments = getArguments();
        if (arguments != null) arguments.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_main, menu);

        MySaluteReportRecyclerViewAdapter adapter
                = (MySaluteReportRecyclerViewAdapter) recyclerView.getAdapter();

        menu.findItem(R.id.action_deselect).setOnMenuItemClickListener(m -> {
            if (adapter != null) adapter.setSelectionModeActive(false);
            return true;
        });

        menu.findItem(R.id.action_select).setOnMenuItemClickListener(m -> {
            if (adapter != null) adapter.setSelectionModeActive(true);
            return true;
        });

        menu.findItem(R.id.action_delete).setOnMenuItemClickListener(m -> {
            if (adapter != null) adapter.deleteAllSelectedReports(requireContext());
            return true;
        });

        menu.findItem(R.id.action_share).setOnMenuItemClickListener(m -> {
            if (adapter != null) adapter.shareAllSelectedReports(requireContext());
            return true;
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onReportSelected(SaluteReport report)
    {
        final HomeFragmentDirections.ActionViewSaluteReport actionViewSaluteReport =
                HomeFragmentDirections.actionViewSaluteReport(report);
        NavHostFragment.findNavController(HomeFragment.this)
                .navigate(actionViewSaluteReport);
    }

    @Override
    public void onReportsSelectionModeChanged(boolean selectionModeActive)
    {
        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu)
    {
        MySaluteReportRecyclerViewAdapter adapter
                = (MySaluteReportRecyclerViewAdapter) recyclerView.getAdapter();

        if (adapter != null)
        {
            final boolean selectionModeActive = adapter.isSelectionModeActive();
            menu.findItem(R.id.action_delete).setVisible(selectionModeActive);
            menu.findItem(R.id.action_share).setVisible(selectionModeActive);
            menu.findItem(R.id.action_deselect).setVisible(selectionModeActive);
            menu.findItem(R.id.action_select).setVisible(!selectionModeActive);
        } else
        {
            Log.wtf(LOG_TAG, "RecyclerViewAdapter is null");
        }

        super.onPrepareOptionsMenu(menu);
    }

    /**
     * Scan the file system for salute report files and add them to the list of reports.
     */
    private synchronized void findAndAddSaluteReports()
    {
        saluteReports.clear();
        final File[] saluteFiles = getPrivateSaluteReportDirectory().listFiles();

        if (saluteFiles != null)
        {
            for (File saluteFile : saluteFiles)
            {
                final Gson gson = new GsonBuilder().create();
                try (final FileReader fileReader = new FileReader(saluteFile))
                {
                    final SaluteReport saluteReport = gson.fromJson(fileReader, SaluteReport.class);

                    if (saluteReport == null)
                    {
                        Log.e(LOG_TAG, "Found a null salute report when scanning the app's private report directory");
                    } else
                    {
                        saluteReports.add(saluteReport);
                        saluteReport.setFile(saluteFile);
                    }
                } catch (Exception e)
                {
                    Log.wtf(LOG_TAG, "Could not read the salute report from a file");
                }
            }
        }

        Collections.sort(saluteReports);
    }

    /**
     * Starts the wizard that allows the user to create a Salute report.
     * <p>
     * First, the user is asked what they want to name the salute report.  After that the wizard steps are walked
     * through.
     */
    private void startSaluteReportWizard(String errorText)
    {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(requireContext());

        final LayoutInflater inflater = requireActivity().getLayoutInflater();

        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.dialog_salute_report_name, null);
        alertBuilder.setView(dialogView);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getString(R.string.name_dialog_title));

        if (errorText != null) alertBuilder.setMessage(errorText);

        alertBuilder.setPositiveButton(android.R.string.ok, (dialog, which) -> onNameSelected(dialogView, dialog));

        final AlertDialog nameDialog = alertBuilder.create();
        //noinspection ConstantConditions
        nameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        nameDialog.show();
    }

    /**
     * Extracts the user entered SALUTE report name, validates that it is not empty, and then triggers the start
     * of the SALUTE report wizard.
     * <p>
     * If the user entered report name is empty, then the wizard is not started, and the name dialog is redisplayed
     * with an error message to the user.
     *
     * @param dialogView the view that contains the user entered name.
     * @param dialog     The dialog that the user clicked ok on.
     */
    private void onNameSelected(View dialogView, DialogInterface dialog)
    {
        final EditText reportNameEditText = dialogView.findViewById(R.id.salute_report_name);
        final String reportName = reportNameEditText.getText().toString().trim();

        if (reportName.isEmpty())
        {
            dialog.cancel();
            startSaluteReportWizard("The SALUTE Report name cannot be empty");
            return;
        }

        final SaluteReport saluteReport = new SaluteReport(System.currentTimeMillis(), reportName);

        final HomeFragmentDirections.ActionCreateSaluteReport actionCreateSaluteReport =
                HomeFragmentDirections.actionCreateSaluteReport(saluteReport);
        NavHostFragment.findNavController(HomeFragment.this).navigate(actionCreateSaluteReport);
    }

    /**
     * Pull out the salute report from the arguments bundle and then serialize it to JSON, write it to disk, update the
     * recycler view, and pass it off to Sync Monkey so it can send it off to Azure.
     *
     * @param arguments The bundle that contains the Salute Report.
     * @param view      The view to use when showing a toast to the user about the newly created report.
     */
    private synchronized void createSaluteReport(Bundle arguments, View view)
    {
        final SaluteReport saluteReport = HomeFragmentArgs.fromBundle(arguments).getSaluteReport();

        // The salute report will be null if this fragment is being displayed on app opening, or if the user canceled the wizard
        if (saluteReport == null) return;

        boolean error = false;
        String snackbarMessage = getString(R.string.created_report_toast_message);

        saluteReports.add(saluteReport);
        Collections.sort(saluteReports);

        Log.d(LOG_TAG, "Serializing a SALUTE report");

        final File uniqueReportFile = createUniqueFile(saluteReport.getReportName() + SaluteAppConstants.SALUTE_REPORT_FILE_EXTENSION);
        saluteReport.setFile(uniqueReportFile);

        try (final FileWriter writer = new FileWriter(uniqueReportFile))
        {
            new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(saluteReport, writer);

            Log.i(LOG_TAG, saluteReport.toString());
        } catch (Exception e)
        {
            error = true;
            snackbarMessage = getString(R.string.error_creating_report_toast_message);
            Log.e(LOG_TAG, "Failed to write the Salute report to disk", e);
        }

        if (!error)
        {
            error = !sendReportToSyncMonkey(uniqueReportFile);
            if (error) snackbarMessage = getString(R.string.sync_monkey_error_toast_message);
        }

        Snackbar.make(view, snackbarMessage, error ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * Given a starting file name, create a file name that is unique in the report directory.  If the provided
     * file name is already unique, then use it.  If it is not unique, then keep counting up and adding that number to
     * the file name until a unique file name is found.
     *
     * @param fileName The file name to start with.
     * @return A unique file mapped to the app's private report directory.
     */
    private File createUniqueFile(String fileName)
    {
        final File privateAppFilesSyncDirectory = getPrivateSaluteReportDirectory();
        File potentialNewFile = new File(privateAppFilesSyncDirectory, fileName);

        if (potentialNewFile.exists())
        {
            final String nameWithoutExtension = SaluteAppUtils.getNameWithoutExtension(fileName);
            final String ext = SaluteAppUtils.getExtension(fileName);
            final String extension = ext == null ? "" : ext;

            int i = 1;

            while (potentialNewFile.exists())
            {
                fileName = nameWithoutExtension + "(" + i++ + ")" + extension;
                potentialNewFile = new File(privateAppFilesSyncDirectory, fileName);
            }
        }

        return potentialNewFile;
    }

    /**
     * @return The File object representing the app's private storage directory where the Salute Report JSON files are
     * stored.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getPrivateSaluteReportDirectory()
    {
        @SuppressWarnings("ConstantConditions") final File privateAppFilesSyncDirectory = new File(getContext().getFilesDir(), SaluteAppConstants.PRIVATE_REPORT_DIRECTORY);
        if (!privateAppFilesSyncDirectory.exists()) privateAppFilesSyncDirectory.mkdir();
        return privateAppFilesSyncDirectory;
    }

    /**
     * Shares the provided file directly to the Sync Monkey app so it can be uploaded to Azure (or another cloud
     * provider).  If the app is not found then false is returned.
     *
     * @param reportFile The file to share to Sync Monkey.
     * @return True if the Sync Monkey app is found on this device.  False if the Sync Monkey app could not be found.
     */
    private boolean sendReportToSyncMonkey(File reportFile)
    {
        final Intent sendFileToSyncMonkey = new Intent(SaluteAppConstants.ACTION_SEND_FILE_NO_UI);
        sendFileToSyncMonkey.addCategory(Intent.CATEGORY_DEFAULT);
        sendFileToSyncMonkey.setComponent(new ComponentName("com.chesapeaketechnology.syncmonkey", "com.chesapeaketechnology.syncmonkey.SharingActivity"));

        //noinspection ConstantConditions
        final Uri reportUri = FileProvider.getUriForFile(getContext(), SaluteAppConstants.AUTHORITY, reportFile);
        sendFileToSyncMonkey.setDataAndType(reportUri, "application/json");
        sendFileToSyncMonkey.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sendFileToSyncMonkey.putExtra(Intent.EXTRA_STREAM, reportUri);

        try
        {
            startActivity(sendFileToSyncMonkey);
        } catch (ActivityNotFoundException e)
        {
            Log.e(LOG_TAG, "Could not find the Sync Monkey SharingActivity.  Only saving the file locally.", e);
            return false;
        }

        return true;
    }
}
