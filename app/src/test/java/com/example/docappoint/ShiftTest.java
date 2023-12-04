package com.example.docappoint;

import org.junit.Before;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.example.docappoint.Doctor.SetShift;

public class ShiftTest {

    private SetShift setShift;

    @Before
    public void setUp() {
        setShift = new SetShift();
    }

    @Test
    public void testOverlappingShift_NoOverlap() {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(2023, Calendar.JANUARY, 1);

        assertFalse(setShift.overlappingShift(
                selectedDate,
                "9:00 AM",
                "11:00 AM",
                "12:00 PM",
                "2:00 PM"
        ));
    }

    @Test
    public void testOverlappingShift_Overlap() {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(2023, Calendar.JANUARY, 1);

        assertTrue(setShift.overlappingShift(
                selectedDate,
                "9:00 AM",
                "11:00 AM",
                "10:30 AM",
                "12:00 PM"
        ));
    }
}
