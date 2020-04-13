package com.chesapeaketechnology.salute;

import com.chesapeaketechnology.salute.model.SaluteReport;

/**
 * Listener interface for interactions with a salute report.
 *
 * @since 0.1.0
 */
public interface SaluteReportInteractionListener
{
    /**
     * Notification when a salute report is clicked on.
     */
    void onReportSelected(SaluteReport report);

    /**
     * Notification when selection mode is activated or deactivated
     * for the list of reports.
     *
     * @param selectionModeActive Indicates whether selection mode was turn on or off.
     */
    void onReportsSelectionModeChanged(boolean selectionModeActive);
}
