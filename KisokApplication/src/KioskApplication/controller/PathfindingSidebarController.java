package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PathfindingSidebarController {
    @FXML TextField tb1;

    @FXML
    void GoPressed() {
        System.out.println(String.format("Search input: %s\n", tb1.getText()));
    }
    @FXML
    void btGeneratePathPressed() {
        System.out.printf("Generate Path button pressed.\n");
    }
}
