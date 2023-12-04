package com.example.docappoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.docappoint.Doctor.DoctorRegister;

public class DoctorRegisterTest {


    private DoctorRegister doctorRegister;

    @Before
    public void setUp() {
        doctorRegister = new DoctorRegister();
    }

    @Test
    public void testValidPhoneNumberCheck() {
        assertTrue(doctorRegister.validPhoneNumberCheck("6135042211"));
        assertFalse(doctorRegister.validPhoneNumberCheck("6135025562"));
    }

    @Test
    public void testValidAddressCheck() {
        assertTrue(doctorRegister.validAddressCheck("64 Finch Avenue"));
        assertFalse(doctorRegister.validAddressCheck("93 horse street"));
    }

    @Test
    public void testValidEmployeeNumCheck() {
        assertTrue(doctorRegister.validEmployeeNumCheck("1234"));
        assertFalse(doctorRegister.validEmployeeNumCheck("5678"));
    }

    @Test
    public void testValidPasswordCheck() {
        assertTrue(doctorRegister.validPasswordCheck("Password1"));
        assertFalse(doctorRegister.validPasswordCheck("pass2ord123"));
        assertFalse(doctorRegister.validPasswordCheck("passworddd12"));
    }

}