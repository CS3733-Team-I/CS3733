package KioskApplication.controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD_EDGE;



public class AdminAddEdgeController {

    AdminWindowController parent;

    AdminAddEdgeController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML
    private TextField node1ID;

    @FXML
    private TextField node2ID;

    @FXML
    private TextField edgeID;

    @FXML
    private Button submitButton;

    @FXML
    private Label errorMsg;

    @FXML
    void onBackPressed() throws IOException{
        this.parent.switchTo(SIDEBAR_MENU);
    }

    @FXML
    void onSubmitClicked() throws IOException{

    }
}
