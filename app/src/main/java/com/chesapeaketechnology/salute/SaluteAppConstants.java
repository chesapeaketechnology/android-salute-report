package com.chesapeaketechnology.salute;

/**
 * Some constants used in the App.
 *
 * @since 0.1.0
 */
@SuppressWarnings("WeakerAccess")
public class SaluteAppConstants
{
    private SaluteAppConstants()
    {
    }

    /**
     * Need to stay in sync with the directory name found in file_provider_paths.xml
     */
    public static final String PRIVATE_REPORT_DIRECTORY = "salutereports";
    public static final String SALUTE_REPORT_FILE_EXTENSION = ".json";

    // The authority for the file provider
    public static final String AUTHORITY = "com.chesapeaketechnology.salute.fileprovider";
    public static final String ACTION_SEND_FILE_NO_UI = "com.chesapeaketechnology.sycnmonkey.action.SEND_FILE_NO_UI";
}
