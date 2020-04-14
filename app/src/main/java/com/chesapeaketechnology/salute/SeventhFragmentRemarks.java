package com.chesapeaketechnology.salute;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chesapeaketechnology.salute.model.SaluteReport;

/**
 * This is the final fragment in the SALUTE report wizard.  It handles taking any additional information from the user
 * and then submitting it to the {@link HomeFragment}.
 *
 * @since 0.1.0
 */
public class SeventhFragmentRemarks extends Fragment
{
    private static final String LOG_TAG = SeventhFragmentRemarks.class.getSimpleName();

    private SaluteReport saluteReport;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_seventh_remarks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        extractSaluteReport();

        view.findViewById(R.id.button_next).setOnClickListener(view1 -> sendSaluteReportToHomeFragment(view));
    }

    /**
     * Pull the Salute Report from the fragment's arguments and set it as an instance variable.
     */
    private void extractSaluteReport()
    {
        final Bundle arguments = getArguments();
        if (arguments == null)
        {
            Log.wtf(LOG_TAG, "The arguments bundle was null when creating the Remarks Fragment (Seventh Fragment)");
        } else
        {
            saluteReport = SeventhFragmentRemarksArgs.fromBundle(arguments).getSaluteReport();
        }
    }

    /**
     * Pass off the Salute Report to the home fragment so that it can process the newly created report.
     */
    private void sendSaluteReportToHomeFragment(View view)
    {
        final String remarks = ((EditText) view.findViewById(R.id.editText)).getText().toString();
        saluteReport.setRemarks(remarks);

        final Context context = getContext();
        if (context != null)
        {
            final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null)
            {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        NavHostFragment.findNavController(SeventhFragmentRemarks.this)
                .navigate(SeventhFragmentRemarksDirections.actionSubmit(saluteReport));
    }
}
