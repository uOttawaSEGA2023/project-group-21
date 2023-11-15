package com.example.docappoint;

public class Appointment {
    private String appointmentDate;
    private String appointmentTime;
    private float appointmentRating;
    private String patientUID;
    private String doctorUID;
    private boolean hasHappened;

    public Appointment(String appointmentDate, String appointmentTime,String patientUID, String doctorUID){
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentRating = 0.0f;
        this.hasHappened = false;
        this.patientUID = patientUID;
        this.doctorUID = doctorUID;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getAppointmentTime(){
        return appointmentTime;
    }

    public String getAppointmentPatientFirstName() {
        return appointmentPatientFirstName;
    }

    public String getAppointmentPatientLastName() {
        return appointmentPatientLastName;
    }

    public float getAppointmentRating() {
        return appointmentRating;
    }

    public void setAppointmentRating(float appointmentRating) {
        this.appointmentRating = appointmentRating;
    }

    public void setIsHappened(boolean hasHappened) {
         this.hasHappened = hasHappened;
    }
}
