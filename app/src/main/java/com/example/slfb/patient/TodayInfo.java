package com.example.slfb.patient;

public class TodayInfo {
    private String dateTime;
    private String insulin;
    private String glucose;
    private String medications;
    private String notes;
    public TodayInfo() {}


    // Constructor for creating a data object
    public TodayInfo(String dateTime, String insulin, String glucose, String medications, String notes) {
        this.dateTime = dateTime;
        this.insulin = insulin;
        this.glucose = glucose;
        this.medications = medications;
        this.notes = notes;
    }

    // Getter and setter methods
    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getInsulin() {
        return insulin;
    }

    public void setInsulin(String insulin) {
        this.insulin = insulin;
    }

    public String getGlucose() {
        return glucose;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}