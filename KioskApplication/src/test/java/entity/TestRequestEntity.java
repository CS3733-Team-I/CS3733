package entity;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Employee;
import database.objects.InterpreterRequest;
import database.objects.Node;
import database.utility.DatabaseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import utility.KioskPermission;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import utility.request.*;
import utility.node.NodeFloor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestRequestEntity {
    private RequestEntity r;
    private DatabaseController db;
    private Node n1,n2,n3,n4;
    private Edge e1,e2,e3;
    private InterpreterRequest presetIR;
    private int empID1, empID2;

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

        Employee testEmp1 = new Employee("boss@hospital.com","Wong","Wilson",
                "123","", KioskPermission.ADMIN, RequestType.GENERAL);
        empID1=db.addEmployee(testEmp1,"123");
        Employee testEmp2 = new Employee("bobby@hospital.com","Bobby","Hill",
                "123","", KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        empID2=db.addEmployee(testEmp2,"123");
        String pIR = r.submitInterpreterRequest("NODE3", empID1,"Is Chinese or Japanese",Language.CHINESE);
        presetIR = r.getInterpreterRequest(pIR);
    }

    @After
    public void cleanUp() throws DatabaseException {
        //deletes request
        r.deleteRequest(presetIR.getRequestID());

        //cleans all employees
        List<Employee> employees = db.getAllEmployees();
        for (Employee e : employees) db.removeEmployee(e.getID());
        //removes node
        db.removeNode(n1);
        db.removeNode(n2);
        db.removeNode(n3);
    }

    @Test
    public void testGetNonexistentInterpreterRequest(){
        long currTime = System.currentTimeMillis();
        InterpreterRequest iR1 = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                empID1, empID1, "", new Timestamp(currTime), new Timestamp(currTime-1),
                new Timestamp(currTime-1), RequestProgressStatus.TO_DO, Language.ARABIC);
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
        String testIRID = r.submitInterpreterRequest("NODE1", empID1, " ", Language.ARABIC);
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
        String testIRID = r.submitInterpreterRequest("NODE1", empID1, " ", Language.ARABIC);
        //retrieves interpreter request from the hashmap
        InterpreterRequest iR = r.getInterpreterRequest(testIRID);
        assertEquals("NODE1",iR.getNodeID());
        assertEquals(empID1,iR.getAssignerID());
        assertEquals(" ",iR.getNote());
        assertEquals(Language.ARABIC,iR.getLanguage());
        r.deleteRequest(testIRID);
    }

    @Test
    public void testCompleteRequest(){
        //adds interpreter request to database and hashmap
        String iR1ID = r.submitInterpreterRequest("NODE1", empID1, " ", Language.ARABIC);
        //starts the request
        r.markInProgress(empID2,iR1ID);
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
        String testIRID = r.submitInterpreterRequest("NODE1", empID1, " ", Language.ARABIC);
        //Interpreter request to be modified
        long currTime = System.currentTimeMillis();
        InterpreterRequest iR1 = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                empID1, empID1, "", new Timestamp(currTime), new Timestamp(currTime-1),
                new Timestamp(currTime-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        //modifying interpreter request
        r.updateInterpreterRequest(testIRID, iR1.getNodeID(), iR1.getAssignerID(), iR1.getNote(), iR1.getSubmittedTime(),
                iR1.getCompletedTime(), iR1.getStatus(), iR1.getLanguage());
        InterpreterRequest iR2 = r.getInterpreterRequest(testIRID);
        assertEquals(testIRID,iR2.getRequestID());
        assertEquals(iR1.getNodeID(),iR2.getNodeID());
        assertEquals(iR1.getAssignerID(),iR2.getAssignerID());
        assertEquals(iR1.getNote(),iR2.getNote());
        assertEquals(iR1.getSubmittedTime(),iR2.getSubmittedTime());
        assertEquals(iR1.getCompletedTime(),iR2.getCompletedTime());
        assertEquals(iR1.getStatus(),iR2.getStatus());
        assertEquals(iR1.getLanguage(),iR2.getLanguage());
        r.deleteRequest(testIRID);
    }

    @Test
    public void getLanguageFrequencyTest(){
        String iR3 = r.submitInterpreterRequest("NODE1", empID2,"",Language.CHINESE);
        String iR1 = r.submitInterpreterRequest("NODE2", empID1,"",Language.GERMAN);
        String iR2 = r.submitInterpreterRequest("NODE1", empID1,"",Language.CHINESE);
        LinkedList<LanguageFrequency> expected = new LinkedList<>();
        expected.add(new LanguageFrequency(Language.CHINESE,2));
        expected.add(new LanguageFrequency(Language.GERMAN,1));
        Collections.sort(expected, new SortByFrequency());
        LinkedList<LanguageFrequency> actual=r.getLanguageFrequency();
        assertEquals(expected.getFirst().getFrequency(),actual.getFirst().getFrequency());
        assertEquals(expected.getLast().getFrequency(),actual.getLast().getFrequency());
        r.deleteRequest(iR1);
        r.deleteRequest(iR2);
        r.deleteRequest(iR3);
    }
}
