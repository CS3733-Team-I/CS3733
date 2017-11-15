package KioskApplication.tests.database;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DatabaseControllerTests {

    MapEntity mapEntity;

    public DatabaseControllerTests() {
        DatabaseController.init();

        mapEntity = mapEntity.getInstance();
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
        Assert.assertEquals(node, receivedNode );
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
        Assert.assertEquals(node, receivedNode);
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

    public void TestDatabaseAddEdge() {

    }

    public void TestDatabaseGetEdge() {

    }

    public void TestDatabaseEditEdge() {

    }

    public void TestDatabaseRemoveEdge() {

    }
}
