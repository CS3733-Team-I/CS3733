package KioskApplication.controller;

import KioskApplication.entity.MapEntity;
import KioskApplication.entity.Path;
import KioskApplication.pathfinder.Pathfinder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

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
        MapEntity map = MapEntity.getInstance();
        Path path = Pathfinder.generatePath(map.getNode(inputStartID.getText()), map.getNode(inputEndID.getText()));
        pathfindingOutputText.setText(path.toString());
        parent.displayPathOnMap(path);
    }
}
//Two node IDs for testing: IDEPT00503 and IREST00103