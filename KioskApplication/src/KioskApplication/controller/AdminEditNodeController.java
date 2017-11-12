package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminEditNodeController {
    AdminWindowController parent;

    AdminEditNodeController(AdminWindowController parent) {
        this.parent = parent;
    }


    @FXML private TextField xcoord;

    @FXML private TextField ycoord;

    @FXML private TextField nodeID;

    @FXML private TextField floor;

    @FXML private TextField building;

    @FXML private TextField nodetype;

    @FXML private TextField lname;

    @FXML private TextField sname;

    @FXML private TextField team;

    public void setCoords(double x, double y){
        // Find closest node and display information
        //xcoord.setText(String.valueOf(x));
        //ycoord.setText(String.valueOf(y));
    }

    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD);
    }

    @FXML
    void onBackPressed() throws IOException{
        System.out.println("Back Pressed\n");

        this.parent.switchTo(SIDEBAR_MENU);
    }
}
