package requests;


import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.objects.InterpreterRequest;
import entity.MapEntity;
import org.apache.derby.catalog.types.SynonymAliasInfo;
import utility.Request.Language;
import org.junit.Before;
import org.junit.Test;
import utility.Request.RequestProgressStatus;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterRequestTest {

    @Test
    public void testDefaultStatus(){
        InterpreterRequest iR1 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        assertEquals(RequestProgressStatus.TO_DO,iR1.getStatus());
    }

    @Test
    public void testIDGenerationHeader(){
        InterpreterRequest iR = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        assertEquals("Int",iR.getRequestID().substring(0,3));
    }

    @Test
    public void testIDGenerationTimeDifference(){
        InterpreterRequest iR1 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
        InterpreterRequest iR2 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        assertNotEquals(iR1.getRequestID(),iR2.getRequestID());
    }

    @Test
    public void testIDGenerationSizeLimit(){
        InterpreterRequest iR1 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        assertEquals(36, iR1.getRequestID().length());
    }

    @Test
    public void testComplete(){
        InterpreterRequest iR1 = new InterpreterRequest("currentloc","nurse","", Language.ARABIC);
        long t = iR1.getCompletedTime().getTime();
        iR1.complete();
        assertEquals(RequestProgressStatus.DONE,iR1.getStatus());
        assertTrue(t<iR1.getCompletedTime().getTime());
    }

}
