package entity;

import database.DatabaseController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utility.KioskPermission;
import utility.request.RequestType;

import static org.junit.Assert.assertEquals;
import static utility.KioskPermission.ADMIN;
import static utility.KioskPermission.EMPLOYEE;
import static utility.KioskPermission.SUPER_USER;

public class TestLoginEntity {
    private LoginEntity l;
    private DatabaseController db;

    @Before
    public void setup(){
        db = DatabaseController.getInstance();
        l = LoginEntity.getTestInstance();
        l.addUser("boss@hospital.com", "123",SUPER_USER, RequestType.GENERAL);
        l.addUser("emp@hospital.com", "12",EMPLOYEE,RequestType.INTERPRETER);
    }

    @After
    public void cleanup(){
        becomeSuperUser();
        l.deleteLogin("emp@hospital.com");
        l.deleteLogin("boss@hospital.com");
    }

    private void becomeSuperUser(){
        l.logIn("boss@hospital.com","123");
    }

    @Test
    public void testInitialPermission(){
        assertEquals(KioskPermission.SUPER_USER,l.getPermission());
        assertEquals("",l.getUserName());
    }

    @Test
    public void testValidatePermissionToAdmin(){
        l.logIn("hgskhgjh","sghvnjdkhgr");
        assertEquals(KioskPermission.SUPER_USER,l.logIn("boss@hospital.com","123"));
        assertEquals("boss@hospital.com",l.getUserName());
    }

    @Test
    public void testValidatePermissionEmployee(){
        assertEquals(KioskPermission.EMPLOYEE, l.logIn("emp@hospital.com","12"));
        assertEquals("emp@hospital.com",l.getUserName());
    }

    @Test
    public void testValidatePermissionToNonEmployee(){
        assertEquals(KioskPermission.NONEMPLOYEE, l.logIn("nonemp@hospital.com","1"));
        assertEquals("",l.getUserName());
    }

    @Test
    public void testInvalidAdminPassword(){
        assertEquals(KioskPermission.NONEMPLOYEE,l.logIn("boss@hospital.com","12"));
        assertEquals("",l.getUserName());
    }

    @Test
    public void testInvalidEmployeePassword(){
        assertEquals(KioskPermission.NONEMPLOYEE,l.logIn("emp@hospital.com","123"));
        assertEquals("",l.getUserName());
    }

    //Employees should not delete the admin login
    @Test
    public void testDeleteLoginEmployeeDeleteAdmin(){
        l.addUser("admin","345",ADMIN,RequestType.GENERAL);
        l.logIn("emp@hospital.com","12");
        l.deleteLogin("admin");
        assertEquals(KioskPermission.ADMIN,l.logIn("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    //Employees should not delete the employee login
    @Test
    public void testDeleteLoginEmployeeDeleteEmployee(){
        l.addUser("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.logIn("emp@hospital.com","12");
        l.deleteLogin("employee");
        assertEquals(KioskPermission.EMPLOYEE,l.logIn("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    //Nonemployees should not delete the admin login
    @Test
    public void testDeleteLoginNonemployeeDeleteAdmin(){
        l.addUser("admin","345",ADMIN,RequestType.GENERAL);
        l.logIn("emp@otherhospital.com","1");
        l.deleteLogin("admin");
        assertEquals(KioskPermission.ADMIN,l.logIn("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    //Nonemployees should not delete the employee login
    @Test
    public void testDeleteLoginNonemployeeDeleteEmployee(){
        l.addUser("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.logIn("emp@otherhospital.com","1");
        l.deleteLogin("employee");
        assertEquals(KioskPermission.EMPLOYEE,l.logIn("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    //Only admins and super users should be able to add to logins
    @Test
    public void testAddLoginEmployee(){
        l.logIn("emp@hospital.com","12");
        l.addUser("test","pass",EMPLOYEE,RequestType.GENERAL);
        assertEquals(KioskPermission.NONEMPLOYEE,l.logIn("test","pass"));
    }

    @Test
    public void testAddLoginNonemployee(){
        l.logIn("emp@otherhospital.com","1");
        l.addUser("test","pass",EMPLOYEE,RequestType.INTERPRETER);
        assertEquals(KioskPermission.NONEMPLOYEE,l.logIn("test","pass"));
    }

    @Test
    public void testUpdatePassword(){
        l.addUser("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.logIn("employee","456");
        l.updatePassword("789","456");
        assertEquals(EMPLOYEE,l.logIn("employee","789"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    @Test
    public void testUpdateUserName(){
        l.addUser("employee","456",EMPLOYEE,RequestType.INTERPRETER);
        l.logIn("employee","456");
        l.updateUserName("emp1","456");
        assertEquals("emp1",l.getUserName());
        becomeSuperUser();
        l.deleteLogin("emp1");
    }
}
