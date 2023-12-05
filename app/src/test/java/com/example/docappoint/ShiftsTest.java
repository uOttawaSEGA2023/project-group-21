package com.example.docappoint;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.docappoint.Doctor.Shifts;

public class ShiftsTest {

    @Test
    public void testGetShiftDate() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        assertEquals("2023-12-04", shifts.getShiftDate());
    }

    @Test
    public void testGetShiftStartTime() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        assertEquals("09:00", shifts.getShiftStartTime());
    }

    @Test
    public void testGetShiftEndTime() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        assertEquals("17:00", shifts.getShiftEndTime());
    }

    @Test
    public void testGetShiftCompleted() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        assertFalse(shifts.getShiftCompleted());
    }

    @Test
    public void testGetIsBooked() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        assertFalse(shifts.getIsBooked());
    }

    @Test
    public void testSetShiftCompleted() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        shifts.setShiftCompleted(true);
        assertTrue(shifts.getShiftCompleted());
    }

    @Test
    public void testSetIsBooked() {

        Shifts shifts = new Shifts("2023-12-04", "09:00", "17:00", false, false);


        shifts.setIsBooked(true);
        assertTrue(shifts.getIsBooked());
    }
}