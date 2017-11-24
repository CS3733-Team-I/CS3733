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
    }

    @After
    public void cleanup(){

    }

    @Test
    public void testInitialPermission(){
        assertEquals(KioskPermission.ADMIN,l.getPermission());
    }

    @Test
    public void testChangePermission(){

    }
}
