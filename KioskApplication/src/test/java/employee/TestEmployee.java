package employee;

import database.objects.Employee;
import org.junit.Test;
import utility.KioskPermission;
import utility.request.RequestType;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEmployee {

    @Test
    public void testValidatePassword(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxy",
                new ArrayList<>(), KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        assertTrue(testEmp.validatePassword("abcdefghijklmnoppqrstuvwxy"));
    }

    //tests if it doesn't return the password when incorrect
    @Test
    public void testGetIncorrectPassword(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxy",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        //incorrect password
        assertEquals("",testEmp.getPassword("123"));
    }

    @Test
    public void testSubmitEncrypted(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcde",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        Employee testEmp2 = new Employee(1,testEmp.getUsername(),testEmp.getLastName(),
                testEmp.getFirstName(),testEmp.getPassword("abcde"),testEmp.getOptionsForDatabase(),testEmp.getPermission(),
                testEmp.getServiceAbility());
        assertTrue(testEmp2.validatePassword("abcde"));
    }

    @Test
    public void testSetPassword(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxyz",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        testEmp.setPassword("abc","abcdefghijklmnoppqrstuvwxyz");
        assertTrue(testEmp.validatePassword("abc"));
        assertFalse(testEmp.validatePassword("abcdefghijklmnoppqrstuvwxyz"));
    }

    @Test
    public void testSetUserName(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxyz",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        testEmp.setUsername("UpdatedName","abcdefghijklmnoppqrstuvwxyz");
        assertEquals("UpdatedName",testEmp.getUsername());
    }

    @Test
    public void testGetOptionsForDatabase(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","a",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        ArrayList<String> options = new ArrayList<>();
        options.add("1");
        options.add("2");
        testEmp.setOptions(options);
        assertEquals("1:2:",testEmp.getOptionsForDatabase());

    }
}
