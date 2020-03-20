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
}
