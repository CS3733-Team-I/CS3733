package entity;

import database.DatabaseController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utility.KioskPermission;

import static org.junit.Assert.assertEquals;

public class TestLoginEntity {
    private LoginEntity l;
    private DatabaseController db;

    @Before
    public void setup(){
        db = DatabaseController.getTestInstance();
        l = LoginEntity.getTestInstance();
        l.addLogin("boss@hospital.com","123",true);
        l.addLogin("emp@hospital.com", "12", false);
    }

    @After
    public void cleanup(){
        l.validate("boss@hospital.com","123");
        l.deleteLogin("emp@hospital.com");
        l.deleteLogin("boss@hospital.com");
    }

    @Test
    public void testInitialPermission(){
        assertEquals(KioskPermission.ADMIN,l.getPermission());
        assertEquals("",l.getUserName());
    }

    @Test
    public void testChangePermissionToAdmin(){
        l.validate("hgskhgjh","sghvnjdkhgr");
        assertEquals(KioskPermission.ADMIN,l.validate("boss@hospital.com","123"));
        assertEquals("boss@hospital.com",l.getUserName());
    }

    @Test
    public void testChangePermissionToEmployee(){
        assertEquals(KioskPermission.EMPLOYEE, l.validate("emp@hospital.com","12"));
        assertEquals("emp@hospital.com",l.getUserName());
    }

    @Test
    public void testChangePermissionToNonEmployee(){
        assertEquals(KioskPermission.NONEMPLOYEE, l.validate("nonemp@hospital.com","1"));
        assertEquals("",l.getUserName());
    }

    @Test
    public void testInvalidAdminPassword(){
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("boss@hospital.com","12"));
        assertEquals("",l.getUserName());
    }

    @Test
    public void testInvalidEmployeePassword(){
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("emp@hospital.com","123"));
        assertEquals("",l.getUserName());
    }
}
