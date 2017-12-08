package entity;

import com.jfoenix.controls.JFXCheckBox;
import database.DatabaseController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utility.KioskPermission;
import utility.request.Language;
import utility.request.RequestType;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static utility.KioskPermission.ADMIN;
import static utility.KioskPermission.EMPLOYEE;
import static utility.KioskPermission.SUPER_USER;
import static utility.request.Language.CHINESE;
import static utility.request.Language.SPANISH;

public class TestLoginEntity {
    private LoginEntity l;

    @Before
    public void setup(){
        l = LoginEntity.getTestInstance();
        l.addUser("boss@hospital.com","","", "123",new ArrayList<>(),
                SUPER_USER, RequestType.GENERAL);
        ArrayList<String> options = new ArrayList<>();
        options.add(Language.SPANISH.toString());
        options.add(Language.CHINESE.toString());
        l.addUser("emp@hospital.com", "","","12",
                options,EMPLOYEE,RequestType.INTERPRETER);
        becomeSuperUser();
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
    public void testValidatePermissionToAdmin(){
        l.logIn("hgskhgjh","sghvnjdkhgr");
        assertEquals(true,l.logIn("boss@hospital.com","123"));
        assertEquals("boss@hospital.com",l.getUsername());
    }

    @Test
    public void testValidatePermissionEmployee(){
        assertEquals(true, l.logIn("emp@hospital.com","12"));
        assertEquals("emp@hospital.com",l.getUsername());
    }

    @Test
    public void testValidatePermissionToNonEmployee(){
        assertEquals(false, l.logIn("nonemp@hospital.com","1"));
        assertEquals("",l.getUsername());
    }

    @Test
    public void testInvalidAdminPassword(){
        assertEquals(false,l.logIn("boss@hospital.com","12"));
        assertEquals("",l.getUsername());
    }

    @Test
    public void testInvalidEmployeePassword(){
        assertEquals(false,l.logIn("emp@hospital.com","123"));
        assertEquals("",l.getUsername());
    }

    //Employees should not delete the admin login
    @Test
    public void testDeleteLoginEmployeeDeleteAdmin(){
        l.addUser("admin","Wong","Wilson","345",new ArrayList<>(),ADMIN,RequestType.GENERAL);
        l.logIn("emp@hospital.com","12");
        l.deleteLogin("admin");
        assertEquals(true,l.logIn("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    //Employees should not delete the employee login
    @Test
    public void testDeleteLoginEmployeeDeleteEmployee(){
        l.addUser("employee","Wong","Wilson","456",new ArrayList<>(),EMPLOYEE,RequestType.FOOD);
        l.logIn("emp@hospital.com","12");
        l.deleteLogin("employee");
        assertEquals(true,l.logIn("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    //Nonemployees should not delete the admin login
    @Test
    public void testDeleteLoginNonemployeeDeleteAdmin(){
        l.addUser("admin","Wong","Wilson","345", new ArrayList<>(),ADMIN,RequestType.FOOD);
        l.logIn("emp@otherhospital.com","1");
        l.deleteLogin("admin");
        assertEquals(true,l.logIn("admin","345"));
        becomeSuperUser();
        l.deleteLogin("admin");
    }

    //Nonemployees should not delete the employee login
    @Test
    public void testDeleteLoginNonemployeeDeleteEmployee(){
        l.addUser("employee","Wong","Wilson","456",new ArrayList<>(),EMPLOYEE,RequestType.FOOD);
        l.logIn("emp@otherhospital.com","1");
        l.deleteLogin("employee");
        assertEquals(true,l.logIn("employee","456"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    //Only admins and super users should be able to add to logins
    @Test
    public void testAddLoginEmployee(){
        l.logIn("emp@hospital.com","12");
        l.addUser("test","Wong","Wilson","pass",new ArrayList<>(),EMPLOYEE,RequestType.GENERAL);
        assertEquals(false,l.logIn("test","pass"));
    }

    @Test
    public void testAddLoginNonemployee(){
        l.logIn("emp@otherhospital.com","1");
        l.addUser("test","Wong","Wilson","pass",new ArrayList<>(),EMPLOYEE,RequestType.FOOD);
        assertEquals(false,l.logIn("test","pass"));
    }

    @Test
    public void testUpdatePassword(){
        l.addUser("employee","Wong","Wilson","456",new ArrayList<>(),EMPLOYEE,RequestType.FOOD);
        l.logIn("employee","456");
        l.updatePassword("789","456");
        assertEquals(true,l.logIn("employee","789"));
        becomeSuperUser();
        l.deleteLogin("employee");
    }

    @Test
    public void testUpdateUserName(){
        l.addUser("employee","Wong","Wilson","456",new ArrayList<>(),EMPLOYEE,RequestType.FOOD);
        l.logIn("employee","456");
        l.updateUsername("emp1","456");
        assertEquals("emp1",l.getUsername());
        becomeSuperUser();
        l.deleteLogin("emp1");
    }

    /**
     * Tests that the method returns a list of Languages containing Spanish (1) and Chinese (2)
     */
    @Test
    public void testGetInterpreterLanguages(){
        l.logIn("emp@hospital.com","12");
        ArrayList<Language> languages = l.getInterpreterLanguages();
        assertEquals(SPANISH,languages.get(0));
        assertEquals(CHINESE,languages.get(1));
    }
}
