package com.example.docappoint;

public class User {

    // User attributes
    private String firstName, lastName, email, password, confirmPassword, address, phoneNumber;

    // Constructor for User
    public User(String firstName, String lastName, String email, String password, String confirmPassword, String phoneNumber, String
            address){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phoneNumber = phoneNumber;
        this.address = address;

    }

    // Constructor for User for Administrator
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public boolean checkPassword (String password, String checkPassword){
        return password.equals(checkPassword);
    }

    // Getters and setters methods

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    // Main method
    public static void main (String [] args){

    }
}
