package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.Path;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.xml.soap.Text;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PathfindingSidebarController {
    //Search box text field
    @FXML TextField tb1;

    //temporary text fields for NodeIDs for pathfinding
    @FXML TextField inputStartID;
    //@FXML TextField inputMiddleID;
    @FXML TextField inputEndID;

    //temporary textArea for output message
    @FXML Label pathfindingOutputText;

    @FXML
    void GoPressed() {
        System.out.println(String.format("Search input: %s\n", tb1.getText()));
    }

    @FXML
    void btGeneratePathPressed() {
        pathfindingOutputText.setText(inputStartID.getText()+inputEndID.getText());

        // TODO remove this, for testing purposes
        Node node1 = new Node("Node1", 1234, 1234, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node2 = new Node("Node2", 1234, 1334, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node3 = new Node("Node3", 1334, 1334, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node4 = new Node("Node4", 1334, 1234, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");
        Node node5 = new Node("Node5", 1434, 1134, NodeFloor.THIRD, NodeBuilding.FRANCIS45, NodeType.HALL, "", "", "");

        List<Node> nodes = Arrays.asList(node1, node4);

        LinkedList<Edge> edges = new LinkedList<>();
        Node lastNode = null;
        for (Node n : nodes) {
            if (!lastNode.equals(null)) {
                String edgeID = lastNode.getNodeID() + "_" + n.getNodeID();
                edges.add(new Edge(edgeID, lastNode.getNodeID(), n.getNodeID()));
            }
        }

        Path generatedPath = new Path(nodes, edges);
    }
}
