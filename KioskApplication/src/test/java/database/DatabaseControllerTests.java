package database;

import database.objects.*;
import database.utility.DatabaseException;
import org.junit.Before;
import utility.KioskPermission;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.request.Language;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DatabaseControllerTests {

    private DatabaseController dbController;

    public DatabaseControllerTests() {
        dbController = DatabaseController.getInstance();
    }

    @Before
    public void statrtup(){
        dbController.addEmployee("boss@hospital.com","password",KioskPermission.EMPLOYEE,
                RequestType.GENERAL);

        dbController.addEmployee("emp@hospital.com","password",KioskPermission.EMPLOYEE,
                RequestType.GENERAL);
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

        List<Employee> employees = dbController.getAllEmployees();
        for (Employee e : employees) {
            System.out.println(e.getLoginID());
            dbController.removeEmployee(e.getLoginID());
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
        Node receivedNode  = dbController.getNode(node.getNodeID());
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

        Node receivedNode = dbController.getNode(node.getNodeID());
        Assert.assertEquals(receivedNode, node);
    }

    @Test
    public void TestDatabaseRemoveNode() throws DatabaseException {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);

        dbController.removeNode(node);

        Node receivedNode = dbController.getNode(node.getNodeID());
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
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                1, 1,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        Assert.assertEquals(iR,dbController.getInterpreterRequest(iR.getRequestID()));
    }

    @Test
    public void testInterpreterRequestUpdate() throws DatabaseException{
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
                1, 1,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        long t2 = System.currentTimeMillis()+2;

        //updates InterpreterRequest values
        iR.setNodeID("NODE2");
        iR.setAssignerID(2);
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
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                1, 1,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        //deletes iR
        dbController.deleteInterpreterRequest(iR.getRequestID());
        InterpreterRequest recievedIR = dbController.getInterpreterRequest(iR.getRequestID());
        Assert.assertTrue(recievedIR==null);
    }

    @Test
    public void testInterpreterRequestRemoveAssociatedNode() throws DatabaseException {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();

        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1",
                1, 1,"", new Timestamp(t1), new Timestamp(t1-1),
                new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        //deletes node
        dbController.removeNode(node);
        InterpreterRequest recievedIR = dbController.getInterpreterRequest(iR.getRequestID());
        Assert.assertTrue(recievedIR==null);
    }

    /**
     * Tests for employee information
     * What I need to test:
     * 1. Getting a loginIDs
     * 2. Getting passwords
     * 3. Getting permissions
     * 4. Getting employee type
     * 5. Adding an employee login
     */

    @Test
    public void testAddEmployee(){
        dbController.addEmployee("Name","password", KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        LinkedList<Employee> emps = dbController.getAllEmployees();
        assertEquals("Name",emps.get(emps.size()-1).getUsername());
    }

    @Test
    public void testRemoveEmployee(){
        dbController.addEmployee("Name","password", KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        LinkedList<Employee> emps = dbController.getAllEmployees();
        dbController.removeEmployee(emps.get(emps.size()-1).getLoginID());
        assertNull(dbController.getEmployee(emps.get(emps.size()-1).getLoginID()));
    }

    @Test
    public void testUpdateEmployee(){
        Employee testEmp = new Employee("Name","password", KioskPermission.EMPLOYEE,
                RequestType.INTERPRETER);
        dbController.addEmployee(testEmp.getUsername(),testEmp.getPassword("password"),
                testEmp.getPermission(), testEmp.getServiceAbility());
        testEmp.updateUsername("NewName","password");
        testEmp.updatePassword("NewPassword","password");
        LinkedList<Employee> emps = dbController.getAllEmployees();
        dbController.updateEmployee(emps.getLast().getLoginID(),testEmp.getUsername(),testEmp.getPassword("NewPassword"),
                KioskPermission.ADMIN, RequestType.GENERAL);
        Employee updatedEmployee=dbController.getEmployee(emps.getLast().getLoginID());
        assertEquals("NewName",updatedEmployee.getUsername());
        assertEquals(KioskPermission.ADMIN,updatedEmployee.getPermission());
        assertEquals(RequestType.GENERAL,updatedEmployee.getServiceAbility());
        assertTrue(updatedEmployee.validatePassword("NewPassword"));
    }

    @Test
    public void testGetAllEmployees(){
        dbController.addEmployee("Name1","password1", KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        dbController.addEmployee("Name2","password2", KioskPermission.ADMIN, RequestType.GENERAL);
        LinkedList<Employee> employees = dbController.getAllEmployees();
        assertEquals("Name1",employees.get(employees.size()-2).getUsername());
        assertEquals("Name2",employees.get(employees.size()-1).getUsername());
    }
}
