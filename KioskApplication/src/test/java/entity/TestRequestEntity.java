package entity;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import utility.Request.Language;
import utility.Node.NodeFloor;
import utility.Request.RequestProgressStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestRequestEntity {
    private RequestEntity r;
    private MapEntity m;
    private Node n1,n2,n3,n4;
    private Edge e1,e2,e3;

    @Before
    public void setup(){
        DatabaseController.initTests();
        r = RequestEntity.getInstance();
        m = MapEntity.getInstance();

        n1 = new Node("NODE1", NodeFloor.GROUND);
        n2 = new Node("NODE2", NodeFloor.LOWERLEVEL_1);
        n3 = new Node("NODE3", NodeFloor.LOWERLEVEL_2);
        n4 = new Node("NODE4", NodeFloor.FIRST);

        e1 = new Edge("EDGE1", "NODE1", "NODE2");
        e2 = new Edge("EDGE2", "NODE3", "NODE4");
        e3 = new Edge("EDGE3", "NODE1", "NODE4");

        m.addNode(n1);
    }

    @After
    public void cleanUp(){
        //removes node
        m.removeNode(n1.getNodeID());
    }

    @Test
    public void testAddRemoveInterpreterRequest(){
        InterpreterRequest iR1 = new InterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        //adds interpreter request to database and hashmap
        r.submitInterpreterRequest(iR1.getNodeID(),iR1.getassigner(),iR1.getNote(), iR1.getLanguage());
        //retrieves interpreter request from the hashmap
        InterpreterRequest iR2 = r.getInterpreterRequest(iR1.getRequestID());
        //compares the two
        assertEquals(iR1.getLanguage(),iR2.getLanguage());
        //removes the request from the database
        r.deleteRequest(iR1.getRequestID());
        //tries to get the requestID from the hashmap
        try{
            r.getInterpreterRequest(iR1.getRequestID());
            fail("Expected to not find the request in the database");
        } catch (NullPointerException e){
            assertEquals("Unable to find InterpreterRequest in database",e.getMessage());
        }
    }

    @Test
    public void testCompleteRequest(){
        InterpreterRequest iR1 = new InterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        //adds interpreter request to database and hashmap
        r.submitInterpreterRequest(iR1.getNodeID(),iR1.getassigner(),iR1.getNote(), iR1.getLanguage());
        //completes request
        r.completeRequest(iR1.getRequestID());
        //retrieves completed request
        InterpreterRequest iR2 = r.getInterpreterRequest(iR1.getRequestID());
        assertEquals(RequestProgressStatus.DONE,iR2.getStatus());
    }
}
