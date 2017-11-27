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
import static utility.Request.RequestProgressStatus.DONE;
import static utility.Request.RequestProgressStatus.IN_PROGRESS;
import static utility.Request.RequestProgressStatus.TO_DO;

public class InterpreterRequestTest {

    @Test
    public void testMarkInProgress(){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        InterpreterRequest iR1 = new InterpreterRequest("test","currentloc","nurse","",
                "note",submittedTime, startedTime, completedTime, TO_DO, Language.ARABIC);
        assertTrue(iR1.markInProgress("emp"));
        assertEquals(IN_PROGRESS, iR1.getStatus());
        assertEquals("emp", iR1.getCompleter());
    }

    @Test
    public void testComplete(){
        long currTime = System.currentTimeMillis();
        Timestamp submittedTime = new Timestamp(currTime);
        Timestamp startedTime = new Timestamp(currTime-1);
        Timestamp completedTime = new Timestamp(currTime-1);
        InterpreterRequest iR1 = new InterpreterRequest("test","currentloc","nurse","",
                "note",submittedTime, startedTime, completedTime, TO_DO, Language.ARABIC);
        iR1.markInProgress("emp");
        assertTrue(iR1.complete());
        assertEquals(DONE, iR1.getStatus());
    }
}
