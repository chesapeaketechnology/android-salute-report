package com.chesapeaketechnology.salute;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chesapeaketechnology.salute.model.SaluteReport;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;

/**
 * The main fragment that lists all the existing SALUTE reports, and allows the user to create a new SALUTE report.
 *
 * @since 0.1.0
 */
public class HomeFragment extends Fragment
{
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
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
        final String reportName = reportNameEditText.getText().toString();

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
    private void createSaluteReport(Bundle arguments, View view)
    {
        final SaluteReport saluteReport = HomeFragmentArgs.fromBundle(arguments).getSaluteReport();

        // The salute report will be null if this fragment is being displayed on app opening, or if the user canceled the wizard
        if (saluteReport == null) return;

        Log.d(LOG_TAG, "Serializing a SALUTE report");

        final GsonBuilder gsonBuilder = new GsonBuilder();
        final String saluteReportJsonString = gsonBuilder.create().toJson(saluteReport);

        Log.i(LOG_TAG, saluteReportJsonString);

        // TODO update the recycler view and write the salute report to disk

        Snackbar.make(view, "Created the SALUTE Report", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
