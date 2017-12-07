package database;

import database.connection.NotFoundException;
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
    private int emp1ID;
    private int emp2ID;

    public DatabaseControllerTests() {
        dbController = DatabaseController.getInstance();
    }

    @Before
    public void statrtup(){
        Employee emp1 = new Employee("boss@hospital.com","Wong","Wilson",
                "password","",KioskPermission.EMPLOYEE,RequestType.GENERAL);
        emp1ID=dbController.addEmployee(emp1,"password");

        Employee emp2 = new Employee("emp@hospital.com","Hill","Hank",
                "password","",KioskPermission.EMPLOYEE,RequestType.GENERAL);
        emp2ID=dbController.addEmployee(emp2,"password");
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
    public void testEmployeeAdd(){
        Employee testEmp = new Employee("Name","n","t","password","",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        int ID = dbController.addEmployee(testEmp,"password");
        assertEquals("Name",dbController.getEmployee(ID).getUsername());
    }

    @Test
    public void testEmployeeRemove(){
        dbController.removeEmployee(emp2ID);
        assertNull(dbController.getEmployee(emp2ID));
    }

    @Test
    public void testEmployeeUpdate(){
        Employee upEmp = dbController.getEmployee(emp2ID);
        upEmp.updateUsername("NewName","password");
        upEmp.updatePassword("NewPassword","password");
        dbController.updateEmployee(upEmp,"NewPassword");
        Employee updatedEmployee=dbController.getEmployee(emp2ID);
        assertEquals("NewName",updatedEmployee.getUsername());
        assertTrue(updatedEmployee.validatePassword("NewPassword"));
    }

    @Test
    public void testEmployeeGetAll(){
        Employee testEmp1=new Employee("Name1","ln1","fn1","password1","",
                KioskPermission.EMPLOYEE, RequestType.INTERPRETER);
        dbController.addEmployee(testEmp1,"password1");
        Employee testEmp2=new Employee("Name2","ln2","fn2","password2","",
                KioskPermission.ADMIN, RequestType.GENERAL);
        dbController.addEmployee(testEmp2,"password2");
        LinkedList<Employee> employees = dbController.getAllEmployees();
        assertEquals("Name1",employees.get(employees.size()-2).getUsername());
        assertEquals("Name2",employees.get(employees.size()-1).getUsername());
    }
}
