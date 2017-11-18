package controller;

import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import pathfinder.A_star;
import pathfinder.Pathfinder;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.LinkedList;

public class PathfindingSidebarController {

    @FXML VBox waypointListVbox;
    @FXML private CheckBox showNodes;

    PathfindingWindowController parent = null;

    LinkedList<Node> currentNodes;

    PathfindingSidebarController(PathfindingWindowController parent) {
        this.parent = parent;
        currentNodes = new LinkedList<>();
    }

    public void addNode(Node node) {
        if (!currentNodes.contains(node)) {
            currentNodes.add(node);

            TextField nodeNameTextField = new TextField(node.getNodeID());
            nodeNameTextField.setEditable(false);
            waypointListVbox.getChildren().add(nodeNameTextField);
        }
    }

    @FXML
    void showNodes(){
        boolean isS = showNodes.isSelected();
        System.out.println(isS);
        this.parent.setShowNodes(isS);
    }

    @FXML
    void onResetPressed() {
        currentNodes.clear();
        waypointListVbox.getChildren().clear();
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        if (currentNodes.size() > 0) {
            Pathfinder pathfinder = new Pathfinder(new A_star());
            Path path = pathfinder.generatePath(currentNodes);
            parent.displayPathOnMap(path);

            waypointListVbox.getChildren().clear();

            currentNodes.clear();
        }
    }

    @FXML
    void btClearPathPressed() throws IOException {
        parent.ClearPathOnMap();
    }
}
//Two node IDs for testing: IDEPT00503 and IREST00103