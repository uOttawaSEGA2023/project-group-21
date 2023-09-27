package com.example.testapplication;

public class Patient extends Person {

    int healthCardNum;
    public Patient(String firstName, String lastName, String email, String password, int phoneNumber, String
            address, int healthCardNum){
        super(firstName, lastName, email, password, phoneNumber, address);
        this.healthCardNum = healthCardNum;
    }

    public static void main (String [] args){

    }

}