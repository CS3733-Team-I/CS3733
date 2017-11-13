package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.entity.Path;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PathfindingSidebarController {

    @FXML TextField tb1;
    @FXML TextField inputStartID;
    @FXML TextField inputEndID;

    @FXML Label pathfindingOutputText;

    PathfindingWindowController parent = null;

    PathfindingSidebarController(PathfindingWindowController parent) {
        this.parent = parent;
    }

    @FXML
    void GoPressed() {
        System.out.println(String.format("Search input: %s\n", tb1.getText()));
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        pathfindingOutputText.setText(inputStartID.getText()+inputEndID.getText());
    }
}
