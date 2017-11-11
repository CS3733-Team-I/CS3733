package KioskApplication.controller;

import KioskApplication.model.PathfindingModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PathfindingSidebarController {
    @FXML TextField tb1;

    /* Pathfinding model */
    private PathfindingModel model;

    public PathfindingSidebarController() {
        this.model = new PathfindingModel();
    }

    @FXML
    void GoPressed() {
        this.model.setSearchInput(tb1.getText());
        System.out.println(String.format("Search input: %s\n", tb1.getText()));
    }
    @FXML
    void btGeneratePathPressed() {
        System.out.printf("Generate Path button pressed.\n");
    }
}
