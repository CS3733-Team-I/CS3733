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
        l.deleteLogin("emp@hospital.com", "12");
        l.deleteLogin("boss@hospital.com", "123");
    }

    @Test
    public void testInitialPermission(){
        assertEquals(KioskPermission.ADMIN,l.getPermission());
        assertEquals("",l.getLoginName());
    }

    @Test
    public void testValidatePermissionToAdmin(){
        l.validate("hgskhgjh","sghvnjdkhgr");
        assertEquals(KioskPermission.ADMIN,l.validate("boss@hospital.com","123"));
        assertEquals("boss@hospital.com",l.getLoginName());
    }

    @Test
    public void testValidatePermissionEmployee(){
        assertEquals(KioskPermission.EMPLOYEE, l.validate("emp@hospital.com","12"));
        assertEquals("emp@hospital.com",l.getLoginName());
    }

    @Test
    public void testValidatePermissionToNonEmployee(){
        assertEquals(KioskPermission.NONEMPLOYEE, l.validate("nonemp@hospital.com","1"));
        assertEquals("",l.getLoginName());
    }

    @Test
    public void testInvalidAdminPassword(){
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("boss@hospital.com","12"));
        assertEquals("",l.getLoginName());
    }

    @Test
    public void testInvalidEmployeePassword(){
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("emp@hospital.com","123"));
        assertEquals("",l.getLoginName());
    }

    //Employees should not delete the admin login
    @Test
    public void testDeleteLoginEmployeeDeleteAdmin(){
        l.addLogin("admin","345",true);
        l.validate("emp@hospital.com","12");
        l.deleteLogin("admin","345");
        assertEquals(KioskPermission.ADMIN,l.validate("admin","345"));
        l.deleteLogin("admin","345");
    }

    //Employees should not delete the employee login
    @Test
    public void testDeleteLoginEmployeeDeleteEmployee(){
        l.addLogin("employee","456",false);
        l.validate("emp@hospital.com","12");
        l.deleteLogin("employee","456");
        assertEquals(KioskPermission.EMPLOYEE,l.validate("employee","456"));
        l.validate("boss@hospital.com","123");
        l.deleteLogin("employee","456");
    }

    //Nonemployees should not delete the admin login
    @Test
    public void testDeleteLoginNonemployeeDeleteAdmin(){
        l.addLogin("admin","345",true);
        l.validate("emp@otherhospital.com","1");
        l.deleteLogin("admin","345");
        assertEquals(KioskPermission.ADMIN,l.validate("admin","345"));
        l.deleteLogin("admin","345");
    }

    //Nonemployees should not delete the employee login
    @Test
    public void testDeleteLoginNonemployeeDeleteEmployee(){
        l.addLogin("employee","456",false);
        l.validate("emp@otherhospital.com","1");
        l.deleteLogin("employee","456");
        assertEquals(KioskPermission.EMPLOYEE,l.validate("employee","456"));
        l.validate("boss@hospital.com","123");
        l.deleteLogin("employee","456");
    }

    @Test
    public void testDeleteLoginWrongAdminPassword(){
        l.addLogin("admin","345",true);
        l.deleteLogin("admin","12");
        assertEquals(KioskPermission.ADMIN,l.validate("admin","345"));
        l.deleteLogin("admin","345");
    }

    @Test
    public void testDeleteLoginWrongEmployeePassword(){
        l.addLogin("employee","456",false);
        l.deleteLogin("employee","12");
        assertEquals(KioskPermission.EMPLOYEE,l.validate("employee","456"));
        l.validate("boss@hospital.com","123");
        l.deleteLogin("employee","456");
    }

    //Only admins should be able to add to logins
    @Test
    public void testAddLoginEmployee(){
        l.validate("emp@hospital.com","12");
        l.addLogin("test","pass",false);
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("test","pass"));
    }

    @Test
    public void testAddLoginNonemployee(){
        l.validate("emp@otherhospital.com","1");
        l.addLogin("test","pass",false);
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("test","pass"));
    }
}
