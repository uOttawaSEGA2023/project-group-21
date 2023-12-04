package com.example.docappoint;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.docappoint.Patient.BookAppointment;

public class BookAppointmentTest {

    private BookAppointment bookAppointment;

    @Before
    public void setUp() {
        bookAppointment = new BookAppointment();
    }

    @Test
    public void testIsDateTimeInPast() {
        assertFalse(bookAppointment.isDateTimeInPast(Calendar.getInstance(), "9:00 AM"));
        assertTrue(bookAppointment.isDateTimeInPast(Calendar.getInstance(), "8:00 AM"));
    }

    @Test
    public void testGenerateDefaultTimes() {
        assertEquals(17, bookAppointment.generateDefaultTimes().size());
    }

    @Test
    public void testUpdateEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        String startTime = "9:00 AM";
        bookAppointment.onItemSelected(null, null, 0, 0);
        bookAppointment.updateEndTime();
        assertEquals("9:30 AM", bookAppointment.bookAppointmentEndTime.getText().toString());
    }

    @Test
    public void testFindUnavailableTimes() {
        String doctorUID = "mockDoctorUID";
        Calendar selectedDate = Calendar.getInstance();
        bookAppointment.findUnavailableTimes(doctorUID, selectedDate);

    }

}
