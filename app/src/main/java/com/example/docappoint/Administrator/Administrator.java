package com.example.docappoint.Administrator;

import com.example.docappoint.User;

public class Administrator extends User {

    // Credentials for admin log in
    public Administrator(String email, String password){
        super(email, password);
    }

    // Main method
    public static void main (String [] args){
        Administrator administrator = new Administrator("admin_docappoint@gmail.com", "Admin1@docappoint");
    }
}

