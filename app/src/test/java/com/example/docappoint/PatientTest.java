package com.example.docappoint;

import com.example.docappoint.Patient.Patient;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.Before;
import org.junit.Test;
//import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.*;

public class PatientTest {

    private Patient patient;

    @Before
    public void setUp() {

        patient = new Patient("John", "Doe", "johndoe@example.com", "password", "password", "1234567890", "123 Main St", 123456789);
    }

    @Test
    public void testGetFirstName() {

        assertEquals("John", patient.getFirstName());
    }

    @Test
    public void testGetLastName() {

        assertEquals("Doe", patient.getLastName());
    }

    @Test
    public void testGetEmail() {

        assertEquals("johndoe@example.com", patient.getEmail());
    }



}
