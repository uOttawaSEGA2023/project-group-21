package com.example.testapplication;

public class Doctor extends Person {
    int employeeNum, numSpecialties;
    String [] specialties;
    public Doctor(byte age, String firstName, String lastName, String email, String password, int phoneNumber, String
            address, int employeeNum, String specialties, int numSpecialties){
        super(firstName, lastName, email, password, phoneNumber, address);
        this.employeeNum = employeeNum;
        this.specialties = new String[numSpecialties];
    }

    public static void main (String [] args){

    }

}