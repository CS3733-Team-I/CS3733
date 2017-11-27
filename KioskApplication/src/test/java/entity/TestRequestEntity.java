package entity;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Node;
import database.utility.DatabaseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import utility.request.Language;
import utility.node.NodeFloor;
import utility.request.RequestProgressStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestRequestEntity {
    private RequestEntity r;
    private DatabaseController db;
    private Node n1,n2,n3,n4;
    private Edge e1,e2,e3;
    private InterpreterRequest presetIR;

    @Before
    public void setup() throws DatabaseException {
        db = DatabaseController.getInstance();
        r = RequestEntity.getTestInstance();

        n1 = new Node("NODE1", NodeFloor.GROUND);
        n2 = new Node("NODE2", NodeFloor.LOWERLEVEL_1);
        n3 = new Node("NODE3", NodeFloor.LOWERLEVEL_2);
        n4 = new Node("NODE4", NodeFloor.FIRST);

        e1 = new Edge("EDGE1", "NODE1", "NODE2");
        e2 = new Edge("EDGE2", "NODE3", "NODE4");
        e3 = new Edge("EDGE3", "NODE1", "NODE4");

        db.addNode(n1);
        db.addNode(n2);
        db.addNode(n3);

        String pIR = r.submitInterpreterRequest("NODE3","Hank","Is Chinese or Japanese",Language.CHINESE);

        presetIR = r.getInterpreterRequest(pIR);
    }

    @After
    public void cleanUp() throws DatabaseException {
        //deletes request
        r.deleteRequest(presetIR.getRequestID());
        //removes node
        db.removeNode(n1);
        db.removeNode(n2);
        db.removeNode(n3);
    }

    @Test
    public void testGetNonexistentInterpreterRequest(){
        InterpreterRequest iR1 = new InterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        try{
            r.getInterpreterRequest(iR1.getRequestID());
            fail("Expected to not find the request in the database");
        } catch (NullPointerException e){
            assertEquals("Unable to find InterpreterRequest in database",e.getMessage());
        }
    }

    @Test
    public void testGetDeletedInterpreterRequest(){
        //adds interpreter request to database and hashmap
        String testIRID = r.submitInterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        //retrieves interpreter request from the hashmap
        InterpreterRequest iR = r.getInterpreterRequest(testIRID);
        //removes the request from the database
        r.deleteRequest(testIRID);
        //tries to get the requestID from the hashmap
        try{
            r.getInterpreterRequest(testIRID);
            fail("Expected to not find the request in the database");
        } catch (NullPointerException e){
            assertEquals("Unable to find InterpreterRequest in database",e.getMessage());
        }
    }

    @Test
    public void testGetInterpreterRequest(){
        //adds interpreter request to database and hashmap
        String testIRID = r.submitInterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        //retrieves interpreter request from the hashmap
        InterpreterRequest iR = r.getInterpreterRequest(testIRID);
        assertEquals("NODE1",iR.getNodeID());
        assertEquals("boss@hospital.com",iR.getAssigner());
        assertEquals(" ",iR.getNote());
        assertEquals(Language.ARABIC,iR.getLanguage());
        r.deleteRequest(testIRID);
    }

    @Test
    public void testCompleteRequest(){
        //adds interpreter request to database and hashmap
        String iR1ID = r.submitInterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        //completes request
        r.completeRequest(iR1ID);
        //retrieves completed request
        InterpreterRequest iR2 = r.getInterpreterRequest(iR1ID);
        assertEquals(RequestProgressStatus.DONE,iR2.getStatus());
        r.deleteRequest(iR1ID);
    }

    @Test
    public void testUpdateRequest(){
        //adds interpreter request to database and hashmap
        String testIRID = r.submitInterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        //Interpreter request to be modified
        InterpreterRequest iR1 = new InterpreterRequest("NODE2","emp@hospital.com", "Says name is Wilson Wong", Language.CHINESE);
        //modifying interpreter request
        r.updateInterpreterRequest(testIRID, iR1.getNodeID(), iR1.getAssigner(), iR1.getNote(), iR1.getSubmittedTime(), iR1.getCompletedTime(), iR1.getStatus(), iR1.getLanguage());
        InterpreterRequest iR2 = r.getInterpreterRequest(testIRID);
        assertEquals(testIRID,iR2.getRequestID());
        assertEquals(iR1.getNodeID(),iR2.getNodeID());
        assertEquals(iR1.getAssigner(),iR2.getAssigner());
        assertEquals(iR1.getNote(),iR2.getNote());
        assertEquals(iR1.getSubmittedTime(),iR2.getSubmittedTime());
        assertEquals(iR1.getCompletedTime(),iR2.getCompletedTime());
        assertEquals(iR1.getStatus(),iR2.getStatus());
        assertEquals(iR1.getLanguage(),iR2.getLanguage());
        r.deleteRequest(iR2.getRequestID());
    }
}
