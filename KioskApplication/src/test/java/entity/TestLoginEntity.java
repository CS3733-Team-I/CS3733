package entity;

import database.DatabaseController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utility.KioskPermission;
import utility.Request.RequestType;

import static org.junit.Assert.assertEquals;
import static utility.KioskPermission.ADMIN;
import static utility.KioskPermission.EMPLOYEE;
import static utility.KioskPermission.SUPER_USER;

public class TestLoginEntity {
    private LoginEntity l;
    private DatabaseController db;

    @Before
    public void setup(){
        db = DatabaseController.getTestInstance();
        l = LoginEntity.getTestInstance();
        l.addLogin("boss@hospital.com", "123",SUPER_USER, RequestType.GENERAL);
        l.addLogin("emp@hospital.com", "12",EMPLOYEE,RequestType.INTERPRETER);
    }

    @After
    public void cleanup(){
        becomeSuperUser();
        l.deleteLogin("emp@hospital.com");
        l.deleteLogin("boss@hospital.com");
    }

    private void becomeSuperUser(){
        l.validate("boss@hospital.com","123");
    }

    @Test
    public void testInitialPermission(){
        assertEquals(KioskPermission.SUPER_USER,l.getPermission());
        assertEquals("",l.getLoginName());
    }

    @Test
    public void testValidatePermissionToAdmin(){
        l.validate("hgskhgjh","sghvnjdkhgr");
        assertEquals(KioskPermission.SUPER_USER,l.validate("boss@hospital.com","123"));
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
        l.addLogin("admin","345",ADMIN,RequestType.GENERAL);
        l.validate("emp@hospital.com","12");
        l.deleteLogin("admin");
        assertEquals(KioskPermission.ADMIN,l.validate("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    //Employees should not delete the employee login
    @Test
    public void testDeleteLoginEmployeeDeleteEmployee(){
        l.addLogin("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.validate("emp@hospital.com","12");
        l.deleteLogin("employee");
        assertEquals(KioskPermission.EMPLOYEE,l.validate("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    //Nonemployees should not delete the admin login
    @Test
    public void testDeleteLoginNonemployeeDeleteAdmin(){
        l.addLogin("admin","345",ADMIN,RequestType.GENERAL);
        l.validate("emp@otherhospital.com","1");
        l.deleteLogin("admin");
        assertEquals(KioskPermission.ADMIN,l.validate("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    //Nonemployees should not delete the employee login
    @Test
    public void testDeleteLoginNonemployeeDeleteEmployee(){
        l.addLogin("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.validate("emp@otherhospital.com","1");
        l.deleteLogin("employee");
        assertEquals(KioskPermission.EMPLOYEE,l.validate("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    @Test
    public void testDeleteLoginWrongAdminPassword(){
        l.addLogin("admin","345",ADMIN,RequestType.GENERAL);
        l.deleteLogin("admin");
        assertEquals(KioskPermission.ADMIN,l.validate("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    @Test
    public void testDeleteLoginWrongEmployeePassword(){
        l.addLogin("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.deleteLogin("employee");
        assertEquals(KioskPermission.EMPLOYEE,l.validate("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    //Only admins should be able to add to logins
    @Test
    public void testAddLoginEmployee(){
        l.validate("emp@hospital.com","12");
        l.addLogin("test","pass",EMPLOYEE,RequestType.GENERAL);
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("test","pass"));
    }

    @Test
    public void testAddLoginNonemployee(){
        l.validate("emp@otherhospital.com","1");
        l.addLogin("test","pass",EMPLOYEE,RequestType.INTERPRETER);
        assertEquals(KioskPermission.NONEMPLOYEE,l.validate("test","pass"));
    }
}
