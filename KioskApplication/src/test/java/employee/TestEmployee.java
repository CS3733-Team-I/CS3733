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
        Employee testEmp = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxy",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        assertTrue(testEmp.validatePassword("abcdefghijklmnoppqrstuvwxy"));
    }

    //tests if it doesn't return the password when incorrect
    @Test
    public void testGetIncorrectPassword(){
        Employee testEmp = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxy",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        Employee testEmp2 = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxyz",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        System.out.println(testEmp.getPassword("abcdefghijklmnoppqrstuvwxy").length());
        System.out.println(testEmp2.getPassword("abcdefghijklmnoppqrstuvwxyz").length());
        //incorrect password
        assertEquals("123",testEmp.getPassword("123"));
    }

    @Test
    public void testSubmitEncrypted(){
        Employee testEmp = new Employee("Test","TestEmp","abcde",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        Employee testEmp2 = new Employee(testEmp.getLoginID(),testEmp.getUserName(),testEmp.getPassword("abcde"),
                testEmp.getPermission(),testEmp.getServiceAbility(),true);
        assertTrue(testEmp2.validatePassword("abcde"));
    }

    @Test
    public void testUpdatePassword(){
        Employee testEmp = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxyz",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        testEmp.updatePassword("abc","abcdefghijklmnoppqrstuvwxyz");
        assertTrue(testEmp.validatePassword("abc"));
        assertFalse(testEmp.validatePassword("abcdefghijklmnoppqrstuvwxyz"));
    }

    @Test
    public void testUpdateUserName(){
        Employee testEmp = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxyz",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        testEmp.updateUserName("UpdatedName","abcdefghijklmnoppqrstuvwxyz");
        assertEquals("UpdatedName",testEmp.getUserName());
    }
}
