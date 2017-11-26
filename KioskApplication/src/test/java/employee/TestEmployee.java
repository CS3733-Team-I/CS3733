package employee;

import database.objects.Employee;
import org.junit.Test;
import utility.KioskPermission;
import utility.Request.RequestType;

import static org.junit.Assert.assertEquals;

public class TestEmployee {

    //tests basic-bitch encryption
    @Test
    public void testGetPassword(){
        Employee testEmp = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxy",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        //correct password
        assertEquals("bcdefghijklmnoppqrstuvwxyz",testEmp.getPassword("abcdefghijklmnoppqrstuvwxy"));
        //incorrect password
        assertEquals("bcdefghijklmnoppqrstuvwx",testEmp.getPassword("bcdefghijklmnoppqrstuvwx"));
    }

    @Test
    public void testUpdateUserName(){
        Employee testEmp = new Employee("Test","TestEmp","abcdefghijklmnoppqrstuvwxyz",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER,false);
        testEmp.updateUserName("UpdatedName","abcdefghijklmnoppqrstuvwxyz");
        assertEquals("UpdatedName",testEmp.getLoginName());

    }
}
