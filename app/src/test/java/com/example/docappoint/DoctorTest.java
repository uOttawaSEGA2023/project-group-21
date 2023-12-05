package com.example.docappoint;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.docappoint.Doctor.Doctor;

public class DoctorTest {

    private Doctor doctor;

    @Before
    public void setUp() {

        String[] specialties = {"Specialty1", "Specialty2"};
        doctor = new Doctor("John", "Doe", "johndoe@example.com", "password", "password", "1234567890", "123 Main St", 123456, specialties);
    }

    @Test
    public void testGetFirstName() {

        assertEquals("John", doctor.getFirstName());
    }

    @Test
    public void testGetLastName() {

        assertEquals("Doe", doctor.getLastName());
    }

    @Test
    public void testGetEmail() {

        assertEquals("johndoe@example.com", doctor.getEmail());
    }

    @Test
    public void testGetPhoneNumber() {

        assertEquals("1234567890", doctor.getPhoneNumber());
    }

    @Test
    public void testGetAddress() {

        assertEquals("123 Main St", doctor.getAddress());
    }








}