package com.example.docappoint.Doctor;

public class Shifts {

    private String shiftDate;
    private String shiftStartTime;
    private String shiftEndTime;
    private boolean shiftCompleted, isBooked;

    public Shifts(String shiftDate, String shiftStartTime, String shiftEndTime, Boolean shiftCompleted, Boolean isBooked) {
        this.shiftDate = shiftDate;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
        this.shiftCompleted = false;
        this.isBooked = false;
    }

    // Getters
    public String getShiftDate() {
        return shiftDate;
    }

    public String getShiftStartTime() {
        return shiftStartTime;
    }
    public String getShiftEndTime() {
        return shiftEndTime;
    }

    public Boolean getShiftCompleted() {
        return shiftCompleted;
    }

    public Boolean getIsBooked() {
        return isBooked;
    }

    // Setters
    public void setShiftCompleted(boolean shiftCompleted) {
        this.shiftCompleted = shiftCompleted;
    }

    public void setIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }
}
