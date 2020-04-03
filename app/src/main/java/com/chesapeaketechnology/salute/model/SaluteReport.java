package com.chesapeaketechnology.salute.model;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single Salute Report.  This class can be serialized and deserialized to/from JSON.
 * <p>
 * Most of these fields are not required for a SALUTE report.  It is only necessary to fill out the fields that the
 * reporter finds meaningful.
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

    @SerializedName("Latitude")
    private Double latitude;

    @SerializedName("Longitude")
    private Double longitude;

    @SerializedName("Unit")
    private String unit;

    @SerializedName("TimeOngoing")
    private Boolean timeOngoing;

    @SerializedName("Time")
    private Date time;

    @SerializedName("Equipment")
    private String equipment;

    @SerializedName("Remarks")
    private String remarks;

    private transient File file;

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

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public void setTimeOngoing(Boolean ongoing)
    {
        timeOngoing = ongoing;
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

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
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

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    /**
     * Either returns location string user entered or formats (lat, lon) as a string
     * @return Formatted string
     */
    public String getLocationString()
    {
        if (location != null && !location.isEmpty())
        {
            return location;
        } else if (latitude != null && longitude != null)
        {
            return latitude + ", " + longitude;
        }
        return null;
    }

    public String getUnit()
    {
        return unit;
    }

    public Boolean getTimeOngoing()
    {
        return timeOngoing;
    }

    public Date getTime()
    {
        return time;
    }

    public String getEquipment()
    {
        return equipment;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }
}
