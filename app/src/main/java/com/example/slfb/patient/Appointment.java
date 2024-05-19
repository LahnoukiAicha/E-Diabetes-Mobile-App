package com.example.slfb.patient;

public class Appointment {
    private String date;
    private String time;
    private String doctorName;
    private String patientId;
    private String patientName;
    private boolean accepted;

    // Constructeur par défaut nécessaire pour Firebase
    public Appointment() {
    }



    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    // Constructeur avec paramètres
    public Appointment(String date, String time, String doctorName, String patientId, String patientName,boolean accepted) {
        this.date = date;
        this.time = time;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.accepted=accepted;
    }

    // Getters et setters
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
}
