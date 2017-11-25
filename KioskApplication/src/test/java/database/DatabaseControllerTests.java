package database;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Node;
import database.objects.Request;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import utility.Request.Language;
import utility.Request.RequestProgressStatus;

import java.sql.Timestamp;
import java.util.List;

public class DatabaseControllerTests {

    private DatabaseController dbController;

    public DatabaseControllerTests() {
        dbController = DatabaseController.getTestInstance();
    }

    @After
    public void removeAllFromDB() {
        List<Node> nodes = dbController.getAllNodes();
        for (Node node : nodes) dbController.removeNode(node);

        List<Edge> edges = dbController.getAllEdges();
        for (Edge edge : edges) dbController.removeEdge(edge);

        List<InterpreterRequest> interpreterRequests = dbController.getAllInterpreterRequests();
        for (InterpreterRequest iR: interpreterRequests) dbController.deleteInterpreterRequest(iR.getRequestID());
    }

    @Test
    public void TestDatabaseAddNode() {
        Node node = new Node("NODE1", 123, 472,
                             NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                             "Test Node", "TN1", "I");
        dbController.addNode(node);
        Node receivedNode  = dbController.getNode(node.getNodeID());
        Assert.assertEquals(receivedNode, node);
    }

    @Test
    public void TestDatabaseEditNode() {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
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
    public void TestDatabaseRemoveNode() {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        dbController.addNode(node);

        dbController.removeNode(node);

        Node receivedNode = dbController.getNode(node.getNodeID());
        Assert.assertTrue(receivedNode == null);
    }

    @Test
    public void TestDatabaseAddEdge() {
        Node node1 = new Node("NODE1", 243, 633,
                NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.RETL,
                "Test Node 1", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node 2", "TN2", "I");

        dbController.addNode(node1);
        dbController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        dbController.addEdge(edge1);

        Edge receivedEdge = dbController.getEdge(edge1.getEdgeID());
        Assert.assertEquals(edge1, receivedEdge);
    }

    @Test
    public void TestDatabaseRemoveEdge() {
        Node node1 = new Node("NODE1", 243, 633,
                NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.RETL,
                "Test Node 1", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node 2", "TN2", "I");

        dbController.addNode(node1);
        dbController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        dbController.removeEdge(edge1);

        Edge receivedEdge = dbController.getEdge(edge1.getEdgeID());
        Assert.assertTrue(receivedEdge == null);
    }

    @Test
    public void TestDatabaseRemoveEdgeParent() {
        Node node1 = new Node("NODE1", 243, 633,
                NodeFloor.FIRST, NodeBuilding.FRANCIS45, NodeType.RETL,
                "Test Node 1", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node 2", "TN2", "I");

        dbController.addNode(node1);
        dbController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        dbController.removeNode(node1);

        Edge receivedEdge = dbController.getEdge(edge1.getEdgeID());
        Assert.assertTrue(receivedEdge == null);
    }

    @Test
    public void testAddInterpreterRequest(){
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        dbController.addNode(node);
        InterpreterRequest iR1 = new InterpreterRequest("NODE1","boss@hospital.com", " ", Language.ARABIC);
        dbController.addInterpreterRequest(iR1);
        Assert.assertEquals(iR1,dbController.getInterpreterRequest(iR1.getRequestID()));
    }

    @Test public void testUpdateInterpreterRequest(){
        Node node1 = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        Node node2 = new Node("NODE2", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node 2", "TN2", "I");
        dbController.addNode(node1);
        dbController.addNode(node2);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1","boss@hospital.com", " ", new Timestamp(t1), new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        long t2 = System.currentTimeMillis()+2;

        //updates InterpreterRequest values
        iR.setNodeID("NODE2");
        iR.setAssigner("emp@hospital.com");
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
    public void testDeleteInterpreterRequest(){
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1","boss@hospital.com", " ", new Timestamp(t1), new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        //deletes iR
        dbController.deleteInterpreterRequest(iR.getRequestID());
        InterpreterRequest recievedIR = dbController.getInterpreterRequest(iR.getRequestID());
        Assert.assertTrue(recievedIR==null);
    }

    @Test
    public void testRemoveInterpreterRequestAssociatedNode(){
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        dbController.addNode(node);
        long t1 = System.currentTimeMillis();
        InterpreterRequest iR = new InterpreterRequest("Int 2017:11:22 NODE1","NODE1","boss@hospital.com", " ", new Timestamp(t1), new Timestamp(t1-1), RequestProgressStatus.TO_DO, Language.ARABIC);
        dbController.addInterpreterRequest(iR);
        //deletes Node
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
}
