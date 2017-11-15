package KioskApplication.controller;

import KioskApplication.database.objects.Edge;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.entity.Path;
import KioskApplication.pathfinder.Pathfinder;
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

    @FXML TextField inputStartID;
    @FXML TextField inputEndID;

    @FXML Label pathfindingOutputText;

    PathfindingWindowController parent = null;

    PathfindingSidebarController(PathfindingWindowController parent) {
        this.parent = parent;
    }



    @FXML
    void btGeneratePathPressed() throws IOException {
        Pathfinder.GeneratePath()
    }
}
