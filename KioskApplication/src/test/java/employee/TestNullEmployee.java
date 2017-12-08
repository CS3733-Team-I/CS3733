package employee;

import database.objects.NullEmployee;
import org.junit.Before;
import org.junit.Test;
import utility.KioskPermission;
import utility.request.RequestType;

import static org.junit.Assert.*;

/**
 * Intent of these tests are to ensure that the null employee is acting like the failed states for Employee
 */
public class TestNullEmployee {
    NullEmployee nullEmployee;

    @Before
    public void initialize(){
        nullEmployee=NullEmployee.getInstance();
    }

    @Test
    public void testGetFirstName(){
        assertEquals("",nullEmployee.getFirstName());
    }

    @Test
    public void testGetLastName(){
        assertEquals("",nullEmployee.getLastName());
    }

    @Test
    public void testValidatePassword(){
        assertFalse(nullEmployee.validatePassword(""));
        assertFalse(nullEmployee.validatePassword("12345"));
        assertFalse(nullEmployee.validatePassword("abcdef"));
        assertFalse(nullEmployee.validatePassword("aghvudjksvhnuietyhjuksjdlhngbvbujdkshjthuiwejqyhgjvudskjlhgnujews"));
    }

    @Test
    public void testGetPassword(){
        assertEquals("",nullEmployee.getPassword(""));
        assertEquals("",nullEmployee.getPassword("12345"));
        assertEquals("",nullEmployee.getPassword("abcdef"));
        assertEquals("",nullEmployee.getPassword("agsdjigjkblhduirkejlgtnudskjgaasdgeretyyfhndsh"));
    }

    @Test
    public void testGetUsername(){
        assertEquals("",nullEmployee.getUsername());
    }

    @Test
    public void testGetLoginID(){
        assertEquals(-1,nullEmployee.getID());
    }

    @Test
    public void testGetPermission(){
        assertEquals(KioskPermission.NONEMPLOYEE,nullEmployee.getPermission());
    }

    @Test
    public void testGetServiceAbility(){
        assertEquals(RequestType.GENERAL,nullEmployee.getServiceAbility());
    }

    @Test
    public void testUpdatePassword(){
        assertFalse(nullEmployee.updatePassword("",""));
    }

    @Test
    public void testUpdateUsername(){
        assertFalse(nullEmployee.updateUsername("",""));
    }

    @Test
    public void testGetOptions(){
        assertEquals("",nullEmployee.getOptions());
    }
}
