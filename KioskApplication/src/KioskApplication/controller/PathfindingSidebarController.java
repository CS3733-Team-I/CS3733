package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.xml.soap.Text;

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
        //System.out.printf("Generate Path button pressed.\n");
        
        pathfindingOutputText.setText(inputStartID.getText()+inputEndID.getText());
    }
}
