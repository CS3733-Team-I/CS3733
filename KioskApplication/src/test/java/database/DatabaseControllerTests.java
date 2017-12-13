package database;

import database.connection.NotFoundException;
import database.objects.*;
import database.objects.requests.ITRequest;
import database.utility.DatabaseException;
import org.junit.Before;
import utility.KioskPermission;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.request.ITService;
import utility.request.Language;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DatabaseControllerTests {

    private Employee emp1;

    private DatabaseController dbController;

    public DatabaseControllerTests() {
        dbController = DatabaseController.getInstance();
    }

    /**
     * Creates the objects to be inserted into the database for tests,
     * DatabaseController should not be called here to reduce run time for tests
     */
    @Before
    public void setup(){
        emp1 = new Employee("boss@hospital.com","Wong","Wilson",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.GENERAL);
    }

    @After
    public void removeAllFromDB() {
        try {
            List<Node> nodes = dbController.getAllNodes();
            for (Node node : nodes) dbController.removeNode(node);

        List<Edge> edges = dbController.getAllEdges();
        for (Edge edge : edges) dbController.removeEdge(edge);

        List<InterpreterRequest> interpreterRequests = dbController.getAllInterpreterRequests();
        for (InterpreterRequest iR: interpreterRequests) dbController.deleteInterpreterRequest(iR.getRequestID());

        List<SecurityRequest> sRs = dbController.getAllSecurityRequests();
        for (SecurityRequest sR: sRs) dbController.deleteSecurityRequest(sR.getRequestID());

        List<FoodRequest> fRs = dbController.getAllFoodRequests();
        for (FoodRequest fR: fRs) dbController.deleteFoodRequest(fR.getRequestID());

        List<JanitorRequest> jRs = dbController.getAllJanitorRequests();
        for (JanitorRequest jR: jRs) dbController.deleteJanitorRequest(jR.getRequestID());

        List<ITRequest> itRequests = dbController.getAllITRequest();
        for (ITRequest itRequest: itRequests) dbController.deleteITRequest(itRequest.getRequestID());

        List<Employee> employees = dbController.getAllEmployees();
        for (Employee e : employees) {
            dbController.removeEmployee(e.getID());
        }

        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestDatabaseAddNode() throws DatabaseException {
        Node node = new Node("NODE1", 123, 472,
                             NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                             "Test node", "TN1", "I");
        dbController.addNode(node);
        Node receivedNode;
        try {
            receivedNode = dbController.getNode(node.getNodeID());
        }
        catch(NotFoundException exception){
            receivedNode = null;
        }
        Assert.assertEquals(receivedNode, node);
    }

    @Test
    public void TestDatabaseEditNode() throws DatabaseException {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);

        // Change node parameters
        node.setXcoord(392);
        node.setYcoord(281);
        node.setFloor(NodeFloor.SECOND);
        node.setBuilding(NodeBuilding.FRANCIS15);
        node.setNodeType(NodeType.SERV);
        node.setLongName("TEST NODE 12345");
        node.setShortName("node1");
        node.setTeamAssigned("Z");

        dbController.updateNode(node);

        Node receivedNode;
        try {
            receivedNode = dbController.getNode(node.getNodeID());
        }
        catch(NotFoundException exception){
            receivedNode = null;
        }
        Assert.assertEquals(receivedNode, node);
    }

    @Test
    public void TestDatabaseRemoveNode() throws DatabaseException {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);

        dbController.removeNode(node);

        Node receivedNode;
        try {
            receivedNode = dbController.getNode(node.getNodeID());
        }
        catch(NotFoundException exception){
            receivedNode = null;
        }
        Assert.assertTrue(receivedNode == null);
    }

    @Test
    public void TestDatabaseAddEdge() throws DatabaseException {
        Node node1 = new Node("NODE1", 243, 633,
                NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.RETL,
                "Test node 1", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");

        dbController.addNode(node1);
        dbController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        dbController.addEdge(edge1);

        Edge receivedEdge = dbController.getEdge(edge1.getEdgeID());
        Assert.assertEquals(edge1, receivedEdge);
    }

    @Test
    public void TestDatabaseRemoveEdge() throws DatabaseException {
        Node node1 = new Node("NODE1", 243, 633,
                NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.RETL,
                "Test node 1", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");

        dbController.addNode(node1);
        dbController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        dbController.removeEdge(edge1);

        Edge receivedEdge = dbController.getEdge(edge1.getEdgeID());
        Assert.assertTrue(receivedEdge == null);
    }

    @Test
    public void TestDatabaseRemoveEdgeParent() throws DatabaseException {
        Node node1 = new Node("NODE1", 243, 633,
                NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.RETL,
                "Test node 1", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");

        dbController.addNode(node1);
        dbController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        dbController.removeNode(node1);

        Edge receivedEdge = dbController.getEdge(edge1.getEdgeID());
        Assert.assertTrue(receivedEdge == null);
    }

    /**
     * InterpreterRequest Tests
     */

    @Test
    public void testInterpreterRequestAdd() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        Assert.assertEquals(iR,dbController.getInterpreterRequest(iR.getRequestID()));
    }

    @Test
    public void testInterpreterRequestUpdate() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");

        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.GENERAL);
        int emp2ID=dbController.addEmployee(emp2,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        long t2 = System.currentTimeMillis()+2;

        //updates InterpreterRequest values
        iR.setNodeID("NODE2");
        iR.setAssignerID(emp2ID);
        iR.setNote("Professor Wong");
        iR.setSubmittedTime(new Timestamp(t2));
        iR.setCompletedTime(new Timestamp(t2-1));
        iR.setStatus(RequestProgressStatus.IN_PROGRESS);
        iR.setLanguage(Language.CHINESE);

        dbController.updateInterpreterRequest(iR);
        InterpreterRequest recievedIR = dbController.getInterpreterRequest(iR.getRequestID());
        Assert.assertEquals(iR, recievedIR);
    }

    @Test
    public void testInterpreterRequestDelete() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        //deletes iR
        dbController.deleteInterpreterRequest(iR.getRequestID());
        InterpreterRequest recievedIR = dbController.getInterpreterRequest(iR.getRequestID());
        Assert.assertTrue(recievedIR==null);
    }

    @Test
    public void testInterpreterRequestRemoveAssociatedNode() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();

        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        //deletes node
        dbController.removeNode(node);
        Assert.assertNull(dbController.getInterpreterRequest(iR.getRequestID()));
    }

    @Test
    public void testSecurityRequestAdd() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        SecurityRequest securityRequest = new SecurityRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, 1);
        dbController.addSecurityRequest(securityRequest);
        Assert.assertEquals(securityRequest,dbController.getSecurityRequest(securityRequest.getRequestID()));
    }

    @Test
    public void testSecurityRequestUpdate() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");

        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.SECURITY);
        int emp2ID=dbController.addEmployee(emp2,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        SecurityRequest securityRequest = new SecurityRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, 2);
        dbController.addSecurityRequest(securityRequest);
        long t2 = System.currentTimeMillis()+2;

        //updates InterpreterRequest values
        securityRequest.setNodeID("NODE2");
        securityRequest.setAssignerID(emp2ID);
        securityRequest.setNote("Professor Wong");
        securityRequest.setSubmittedTime(new Timestamp(t2));
        securityRequest.setCompletedTime(new Timestamp(t2-1));
        securityRequest.setStatus(RequestProgressStatus.IN_PROGRESS);
        securityRequest.setPriority(3);

        dbController.updateSecurityRequest(securityRequest);
        SecurityRequest updatedSR = dbController.getSecurityRequest(securityRequest.getRequestID());
        Assert.assertEquals(securityRequest, updatedSR);
    }

    @Test
    public void testSecurityRequestDelete() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        SecurityRequest securityRequest = new SecurityRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, 1);
        dbController.addSecurityRequest(securityRequest);
        //deletes securityRequest
        dbController.deleteSecurityRequest(securityRequest.getRequestID());
        Assert.assertNull(dbController.getSecurityRequest(securityRequest.getRequestID()));
    }

    @Test
    public void testSecurityRequestRemoveAssociatedNode() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();

        SecurityRequest securityRequest = new SecurityRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, 1);
        dbController.addSecurityRequest(securityRequest);
        //deletes node
        dbController.removeNode(node);
        Assert.assertNull(dbController.getSecurityRequest(securityRequest.getRequestID()));
    }

    @Test
    public void testFoodRequestAdd() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        FoodRequest foodRequest = new FoodRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, "NODE2", new Timestamp(t1+2));
        dbController.addFoodRequest(foodRequest);
        Assert.assertEquals(foodRequest,dbController.getFoodRequest(foodRequest.getRequestID()));
    }

    @Test
    public void testFoodRequestUpdate() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");

        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.SECURITY);
        int emp2ID=dbController.addEmployee(emp2,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        FoodRequest foodRequest = new FoodRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, "NODE2", new Timestamp(t1+2));
        dbController.addFoodRequest(foodRequest);
        long t2 = System.currentTimeMillis()+2;

        //updates InterpreterRequest values
        foodRequest.setNodeID("NODE2");
        foodRequest.setAssignerID(emp2ID);
        foodRequest.setNote("Professor Wong");
        foodRequest.setSubmittedTime(new Timestamp(t2));
        foodRequest.setCompletedTime(new Timestamp(t2-1));
        foodRequest.setStatus(RequestProgressStatus.IN_PROGRESS);
        foodRequest.setDestinationID("NODE1");

        dbController.updateFoodRequest(foodRequest);
        FoodRequest updatedFR = dbController.getFoodRequest(foodRequest.getRequestID());
        Assert.assertEquals(foodRequest, updatedFR);
    }

    @Test
    public void testFoodRequestDelete() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        FoodRequest foodRequest = new FoodRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, "NODE2",new Timestamp(t1+2));
        dbController.addFoodRequest(foodRequest);
        //deletes foodRequest
        dbController.deleteFoodRequest(foodRequest.getRequestID());
        Assert.assertNull(dbController.getFoodRequest(foodRequest.getRequestID()));
    }

    @Test
    public void testFoodRequestRemoveAssociatedNode() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();

        FoodRequest foodRequest = new FoodRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, "NODE2", new Timestamp(t1+2));
        dbController.addFoodRequest(foodRequest);
        //deletes node
        dbController.removeNode(node1);
        Assert.assertNull(dbController.getFoodRequest(foodRequest.getRequestID()));
    }

    @Test
    public void testJanitorRequestAdd() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node1);
        long t1 = System.currentTimeMillis();
        JanitorRequest janitorRequest = new JanitorRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO);
        dbController.addJanitorRequest(janitorRequest);
        Assert.assertEquals(janitorRequest,dbController.getJanitorRequest(janitorRequest.getRequestID()));
    }

    @Test
    public void testJanitorRequestUpdate() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");

        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.SECURITY);
        int emp2ID=dbController.addEmployee(emp2,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        JanitorRequest janitorRequest = new JanitorRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO);
        dbController.addJanitorRequest(janitorRequest);
        long t2 = System.currentTimeMillis()+2;

        //updates JanitorRequest values
        janitorRequest.setNodeID("NODE2");
        janitorRequest.setAssignerID(emp2ID);
        janitorRequest.setNote("Professor Wong");
        janitorRequest.setSubmittedTime(new Timestamp(t2));
        janitorRequest.setCompletedTime(new Timestamp(t2-1));
        janitorRequest.setStatus(RequestProgressStatus.IN_PROGRESS);

        dbController.updateJanitorRequest(janitorRequest);
        JanitorRequest updatedJR = dbController.getJanitorRequest(janitorRequest.getRequestID());
        Assert.assertEquals(janitorRequest, updatedJR);
    }

    @Test
    public void testJanitorRequestDelete() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node1);
        long t1 = System.currentTimeMillis();
        JanitorRequest janitorRequest = new JanitorRequest("Int 2017:11:22 NODE1",node1.getNodeID(),
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO);
        dbController.addJanitorRequest(janitorRequest);
        //deletes janitorRequest
        dbController.deleteJanitorRequest(janitorRequest.getRequestID());
        Assert.assertNull(dbController.getJanitorRequest(janitorRequest.getRequestID()));
    }

    @Test
    public void testJanitorRequestRemoveAssociatedNode() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();

        JanitorRequest janitorRequest = new JanitorRequest("Int 2017:11:22 NODE1",node1.getNodeID(),
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO);
        dbController.addJanitorRequest(janitorRequest);
        //deletes node
        dbController.removeNode(node1);
        Assert.assertNull(dbController.getJanitorRequest(janitorRequest.getRequestID()));
    }

    @Test
    public void testITRequestAdd() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node1);
        long t1 = System.currentTimeMillis();
        ITRequest itRequest = new ITRequest("Int 2017:11:22 NODE1","NODE1",
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, ITService.NETWORK_DOWN);
        dbController.addITRequest(itRequest);
        Assert.assertEquals(itRequest,dbController.getITRequest(itRequest.getRequestID()));
    }

    @Test
    public void testITRequestUpdate() throws DatabaseException{
        int emp1ID=dbController.addEmployee(emp1,"password");

        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.SECURITY);
        int emp2ID=dbController.addEmployee(emp2,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        ITRequest itRequest = new ITRequest("Int 2017:11:22 NODE1",node1.getNodeID(),
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO,ITService.KIOSK);
        dbController.addITRequest(itRequest);
        long t2 = System.currentTimeMillis()+2;

        //updates JanitorRequest values
        itRequest.setNodeID(node2.getNodeID());
        itRequest.setAssignerID(emp2ID);
        itRequest.setNote("Professor Wong");
        itRequest.setSubmittedTime(new Timestamp(t2));
        itRequest.setCompletedTime(new Timestamp(t2-1));
        itRequest.setStatus(RequestProgressStatus.IN_PROGRESS);
        itRequest.setItService(ITService.NETWORK_DOWN);

        dbController.updateITRequest(itRequest);
        ITRequest updatedITR = dbController.getITRequest(itRequest.getRequestID());
        Assert.assertEquals(itRequest, updatedITR);
    }

    @Test
    public void testITRequestDelete() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node1);
        long t1 = System.currentTimeMillis();
        ITRequest itRequest = new ITRequest("Int 2017:11:22 NODE1",node1.getNodeID(),
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO,ITService.NETWORK_DOWN);
        dbController.addITRequest(itRequest);
        //deletes itRequest
        dbController.deleteITRequest(itRequest.getRequestID());
        Assert.assertNull(dbController.getITRequest(itRequest.getRequestID()));
    }

    @Test
    public void testITRequestRemoveAssociatedNode() throws DatabaseException {
        int emp1ID=dbController.addEmployee(emp1,"password");
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();

        ITRequest itRequest = new ITRequest("Int 2017:11:22 NODE1",node1.getNodeID(),
                emp1ID, emp1ID,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO,ITService.NETWORK_DOWN);
        dbController.addITRequest(itRequest);
        //deletes node
        dbController.removeNode(node1);
        Assert.assertNull(dbController.getITRequest(itRequest.getRequestID()));
    }

    /*
     * Tests for employee information
     * What I need to test:
     * 1. Getting a loginIDs
     * 2. Getting passwords
     * 3. Getting permissions
     * 4. Getting employee type
     * 5. Adding an employee login
     */

    @Test
    public void testEmployeeAdd() throws DatabaseException{
        Employee testEmp = new Employee("Name","n","t","password",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        int ID = dbController.addEmployee(testEmp,"password");
        assertEquals("Name",dbController.getEmployee(ID).getUsername());
    }

    @Test
    public void testEmployeeRemove() throws DatabaseException{
        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.GENERAL);
        int emp2ID=dbController.addEmployee(emp2,"password");
        dbController.removeEmployee(emp2ID);
        assertNull(dbController.getEmployee(emp2ID));
    }

    @Test
    public void testEmployeeUpdate() throws DatabaseException{
        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password",new ArrayList<>(),KioskPermission.EMPLOYEE,RequestType.GENERAL);
        int emp2ID=dbController.addEmployee(emp2,"password");
        Employee upEmp = dbController.getEmployee(emp2ID);
        upEmp.setUsername("NewName","password");
        upEmp.setPassword("NewPassword","password");
        dbController.updateEmployee(upEmp,"NewPassword");
        Employee updatedEmployee=dbController.getEmployee(emp2ID);
        assertEquals("NewName",updatedEmployee.getUsername());
        assertTrue(updatedEmployee.validatePassword("NewPassword"));
    }

    @Test
    public void testEmployeeGetAll()throws DatabaseException{
        Employee testEmp1=new Employee("Name1","ln1","fn1","password1",new ArrayList<>(),
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        dbController.addEmployee(testEmp1,"password1");
        Employee testEmp2=new Employee("Name2","ln2","fn2","password2",new ArrayList<>(),
                KioskPermission.ADMIN, RequestType.GENERAL);
        dbController.addEmployee(testEmp2,"password2");
        LinkedList<Employee> employees = dbController.getAllEmployees();
        assertEquals("Name1",employees.get(employees.size()-2).getUsername());
        assertEquals("Name2",employees.get(employees.size()-1).getUsername());
    }
}
