package requests;


import database.objects.InterpreterRequest;
import utility.request.Language;
import org.junit.Test;
import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static utility.request.RequestProgressStatus.DONE;
import static utility.request.RequestProgressStatus.IN_PROGRESS;
import static utility.request.RequestProgressStatus.TO_DO;

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
