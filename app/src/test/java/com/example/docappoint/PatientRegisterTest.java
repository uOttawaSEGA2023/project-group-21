package com.example.docappoint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.docappoint.Patient.PatientRegister;

public class PatientRegisterTest {

    private PatientRegister patientRegister;

    @Before
    public void setUp() {
        patientRegister = new PatientRegister();
    }

    @Test
    public void testValidPhoneNumberCheck() {
        assertTrue(patientRegister.validPhoneNumberCheck("6135042211"));
        assertFalse(patientRegister.validPhoneNumberCheck("6135025562"));
    }

    @Test
    public void testValidAddressCheck() {
        assertTrue(patientRegister.validAddressCheck("64 Finch Avenue"));
        assertFalse(patientRegister.validAddressCheck("93 horse street"));
    }

    @Test
    public void testValidHealthCardCheck() {
        assertTrue(patientRegister.validHealthCardCheck("1234567890"));
        assertFalse(patientRegister.validHealthCardCheck("2434345512"));
        assertFalse(patientRegister.validHealthCardCheck("1234512345"));
    }

    @Test
    public void testValidPasswordCheck() {
        assertTrue(patientRegister.validPasswordCheck("Password1"));
        assertFalse(patientRegister.validPasswordCheck("pass2ord123"));
        assertFalse(patientRegister.validPasswordCheck("passworddd12"));
    }

}
