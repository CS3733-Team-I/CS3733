package database;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import utility.NodeBuilding;
import utility.NodeFloor;
import utility.NodeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DatabaseControllerTests {

    public DatabaseControllerTests() {
        DatabaseController.initTests();
    }

    @After
    public void removeAllFromDB() {
        List<Node> nodes = DatabaseController.getAllNodes();
        for (Node node : nodes) DatabaseController.removeNode(node);

        List<Edge> edges = DatabaseController.getAllEdges();
        for (Edge edge : edges) DatabaseController.removeEdge(edge);
    }

    @Test
    public void TestDatabaseAddNode() {
        Node node = new Node("NODE1", 123, 472,
                             NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                             "Test Node", "TN1", "I");
        DatabaseController.addNode(node);
        Node receivedNode  = DatabaseController.getNode(node.getNodeID());
        Assert.assertEquals(receivedNode, node);
    }

    @Test
    public void TestDatabaseEditNode() {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        DatabaseController.addNode(node);

        // Change node parameters
        node.setXcoord(392);
        node.setYcoord(281);
        node.setFloor(NodeFloor.SECOND);
        node.setBuilding(NodeBuilding.FRANCIS15);
        node.setNodeType(NodeType.SERV);
        node.setLongName("TEST NODE 12345");
        node.setShortName("node1");
        node.setTeamAssigned("Z");

        DatabaseController.updateNode(node);

        Node receivedNode = DatabaseController.getNode(node.getNodeID());
        Assert.assertEquals(receivedNode, node);
    }

    @Test
    public void TestDatabaseRemoveNode() {
        Node node = new Node("NODE1", 123, 472,
                NodeFloor.THIRD, NodeBuilding.BTM, NodeType.ELEV,
                "Test Node", "TN1", "I");
        DatabaseController.addNode(node);

        DatabaseController.removeNode(node);

        Node receivedNode = DatabaseController.getNode(node.getNodeID());
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

        DatabaseController.addNode(node1);
        DatabaseController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        DatabaseController.addEdge(edge1);

        Edge receivedEdge = DatabaseController.getEdge(edge1.getEdgeID());
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

        DatabaseController.addNode(node1);
        DatabaseController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        DatabaseController.removeEdge(edge1);

        Edge receivedEdge = DatabaseController.getEdge(edge1.getEdgeID());
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

        DatabaseController.addNode(node1);
        DatabaseController.addNode(node2);

        Edge edge1 = new Edge("NODE1_NODE2", node1.getNodeID(), node2.getNodeID());

        DatabaseController.removeNode(node1);

        Edge receivedEdge = DatabaseController.getEdge(edge1.getEdgeID());
        Assert.assertTrue(receivedEdge == null);
    }
}
