package com.chesapeaketechnology.salute.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single Salute Report.  This class can be serialized and deserialized to/from JSON.
 */
public class SaluteReport implements Serializable
{
    @SerializedName("To")
    private String to;

    @SerializedName("From")
    private String from;

    @SerializedName("ReportCreationTime")
    private Date reportCreationTime;

    @SerializedName("ReportName")
    private String reportName;

    @SerializedName("Size")
    private String size;

    @SerializedName("Activity")
    private String activity;

    @SerializedName("Location")
    private String location;

    @SerializedName("Unit")
    private String unit;

    @SerializedName("Time")
    private Date time;

    @SerializedName("Equipment")
    private String equipment;

    /**
     * Constructor for GSON to create this class from the JSON string.
     */
    public SaluteReport()
    {

    }

    public SaluteReport(long reportCreationTime, String reportName)
    {
        this.reportCreationTime = new Date(reportCreationTime);
        this.reportName = reportName;
    }

    public void setTo(String to)
    {
        this.to = to;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    public void setActivity(String activity)
    {
        this.activity = activity;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public void setEquipment(String equipment)
    {
        this.equipment = equipment;
    }

    public String getTo()
    {
        return to;
    }

    public String getFrom()
    {
        return from;
    }

    public Date getReportCreationTime()
    {
        return reportCreationTime;
    }

    public String getReportName()
    {
        return reportName;
    }

    public String getSize()
    {
        return size;
    }

    public String getActivity()
    {
        return activity;
    }

    public String getLocation()
    {
        return location;
    }

    public String getUnit()
    {
        return unit;
    }

    public Date getTime()
    {
        return time;
    }

    public String getEquipment()
    {
        return equipment;
    }
}
