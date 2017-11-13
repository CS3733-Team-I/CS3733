package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML private Label errorMsg;

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
    @FXML
    void deleteNode() throws IOException{
        if(nodeID.getText().equals("") || nodeID.getText() == null){ // If no node selected
            System.out.println("No Node Selected");

        }
        else{
            System.out.println("Delete node: " + nodeID.getText());

        }
    }
    @FXML
    void onSubmitClicked() throws IOException{

        errorMsg.setText("");
        if(nodeID.getText().equals(null) || nodeID.getText().equals(""))
            errorMsg.setText("You must input the node ID!");
        else if(xcoord.getText().equals(null) || xcoord.getText().equals(""))
            errorMsg.setText("You must input the X coordinate!");
        else if(ycoord.getText().equals(null) || ycoord.getText().equals(""))
            errorMsg.setText("You must input the Y coordinate!");
        else if(floor.getText().equals(null) || floor.getText().equals(""))
            errorMsg.setText("You must input a floor!");
        else if(building.getText().equals(null) || building.getText().equals(""))
            errorMsg.setText("You must input a building!");
        else if(nodetype.getText().equals(null) || nodetype.getText().equals(""))
            errorMsg.setText("You must input the node type!");
        else if(lname.getText().equals(null) || lname.getText().equals(""))
            errorMsg.setText("You must input a long name!");
        else if(sname.getText().equals(null) || sname.getText().equals(""))
            errorMsg.setText("You must input a short name!");
        else if(team.getText().equals(null) || team.getText().equals(""))
            errorMsg.setText("You must input the team assigned!");
        else {
            // Tell Database to edit the node

        }
    }
}
