package com.example.docappoint;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.docappoint.Administrator.Administrator;

public class AdministratorTest {

    @Test
    public void testGetEmail() {

        Administrator administrator = new Administrator("admin_docappoint@gmail.com", "Admin1@docappoint");


        assertEquals("admin_docappoint@gmail.com", administrator.getEmail());
    }

    @Test
    public void testGetPassword() {

        Administrator administrator = new Administrator("admin_docappoint@gmail.com", "Admin1@docappoint");


        assertEquals("Admin1@docappoint", administrator.getPassword());
    }
}