package employee;

import database.objects.Employee;
import org.junit.Test;
import utility.KioskPermission;
import utility.request.RequestType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEmployee {

    @Test
    public void testValidatePassword(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxy","1:2:",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        assertTrue(testEmp.validatePassword("abcdefghijklmnoppqrstuvwxy"));
    }

    //tests if it doesn't return the password when incorrect
    @Test
    public void testGetIncorrectPassword(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxy","1:2:",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        Employee testEmp2 = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxyz","1:2:",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        System.out.println(testEmp.getPassword("abcdefghijklmnoppqrstuvwxy").length());
        System.out.println(testEmp2.getPassword("abcdefghijklmnoppqrstuvwxyz").length());
        //incorrect password
        assertEquals("",testEmp.getPassword("123"));
    }

    @Test
    public void testSubmitEncrypted(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcde","1:2:",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        Employee testEmp2 = new Employee(1,testEmp.getUsername(),testEmp.getLastName(),
                testEmp.getFirstName(),testEmp.getPassword("abcde"),testEmp.getOptions(),testEmp.getPermission(),
                testEmp.getServiceAbility());
        assertTrue(testEmp2.validatePassword("abcde"));
    }

    @Test
    public void testUpdatePassword(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxyz","1:2:",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        testEmp.updatePassword("abc","abcdefghijklmnoppqrstuvwxyz");
        assertTrue(testEmp.validatePassword("abc"));
        assertFalse(testEmp.validatePassword("abcdefghijklmnoppqrstuvwxyz"));
    }

    @Test
    public void testUpdateUserName(){
        Employee testEmp = new Employee("TestEmp","Wong","Wilson","abcdefghijklmnoppqrstuvwxyz","1:2:",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        testEmp.updateUsername("UpdatedName","abcdefghijklmnoppqrstuvwxyz");
        assertEquals("UpdatedName",testEmp.getUsername());
    }
}
