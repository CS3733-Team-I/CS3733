package entity;

import database.DatabaseController;
import database.objects.InterpreterRequest;
import org.junit.Before;
import org.junit.Test;
import utility.Language;

import static org.junit.Assert.assertEquals;

public class TestRequestEntity {
    private RequestEntity r;

    @Before
    public void setup(){
        DatabaseController.initTests();
        r = RequestEntity.getInstance();
    }

    @Test
    public void testSubmitInterpreterRequest(){
        InterpreterRequest iR1 = new InterpreterRequest("current location","boss@hospital.com", " ", Language.ARABIC);
        r.submitInterpreterRequest(iR1.getNodeID(),iR1.getassigner(),iR1.getNote(), iR1.getLanguage());
        InterpreterRequest iR2 = r.getInterpreterRequest(iR1.getRequestID());
        assertEquals(iR1.getLanguage(),iR2.getLanguage());
        System.out.println(iR1.getRequestID());
        r.deleteRequest(iR1.getRequestID());
    }


}
