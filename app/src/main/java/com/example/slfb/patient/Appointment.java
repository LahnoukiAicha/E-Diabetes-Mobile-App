package com.example.slfb.patient;

public class Appointment {
    private String id;
    private String doctorId;
    private String date;
    private String time;
    private String doctorName;
    private String patientId;
    private String patientName;
    private boolean accepted;

    // Default constructor required for Firebase
    public Appointment() {
    }

    // Constructor with parameters
    public Appointment(String id, String doctorId, String date, String time, String doctorName, String patientId, String patientName, boolean accepted) {
        this.id = id;     // Add this line
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.accepted = accepted;
    }

    // Getters and setters
    public String getId() {
        return id;     // Add this line
    }

    public void setId(String id) {
        this.id = id;     // Add this line
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
