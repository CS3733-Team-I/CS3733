package KioskApplication.controller;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminAddNodeController {
    AdminWindowController parent;

    AdminAddNodeController(AdminWindowController parent) {
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
        System.out.println("setCoords");
        xcoord.setText(String.valueOf(x));
        ycoord.setText(String.valueOf(y));
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
