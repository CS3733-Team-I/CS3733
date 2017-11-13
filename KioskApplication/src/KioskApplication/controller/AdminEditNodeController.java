package KioskApplication.controller;

import KioskApplication.database.DatabaseController;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;

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

    @FXML private ChoiceBox<?> floorChoiceBox;

    @FXML private ChoiceBox<?> buildingChoiceBox;

    @FXML private ChoiceBox<?> nodeTypeChoiceBox;

    @FXML private TextField lname;

    @FXML private TextField sname;

    @FXML private TextField team;

    @FXML private Label errorMsg;

    public void setCoords(double x, double y){
        // Find closest node and display information ******

        // Loop through database checking the distance each node is,
        // if it is the new closest store its nodeID

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
            // Notify user

        }
        else{
            System.out.println("Delete node: " + nodeID.getText());
            // Check to ensure node with that ID is in database
                // Delete node
            // If not, notify user
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
        else if(floorChoiceBox.getValue().equals(null) || floorChoiceBox.getValue().equals("-select-"))
            errorMsg.setText("You must input a building!");
        else if(buildingChoiceBox.getValue().equals(null) || buildingChoiceBox.getValue().equals("-select-"))
            errorMsg.setText("You must input a building!");
        else if(nodeTypeChoiceBox.getValue().equals(null) || nodeTypeChoiceBox.getValue().equals("-select-"))
            errorMsg.setText("You must input the node type!");
        else if(lname.getText().equals(null) || lname.getText().equals(""))
            errorMsg.setText("You must input a long name!");
        else if(sname.getText().equals(null) || sname.getText().equals(""))
            errorMsg.setText("You must input a short name!");
        else if(team.getText().equals(null) || team.getText().equals(""))
            errorMsg.setText("You must input the team assigned!");
        else {
            // Determine floor
            NodeFloor floor = NodeFloor.THIRD; // Default
            if(floorChoiceBox.getValue().equals("L2"))
                floor = NodeFloor.LOWERLEVEL_2;
            if(floorChoiceBox.getValue().equals("L1"))
                floor = NodeFloor.LOWERLEVEL_1;
            if(floorChoiceBox.getValue().equals("G"))
                floor = NodeFloor.GROUND;
            if(floorChoiceBox.getValue().equals("1"))
                floor = NodeFloor.FIRST;
            if(floorChoiceBox.getValue().equals("2"))
                floor = NodeFloor.SECOND;
            if(floorChoiceBox.getValue().equals("3"))
                floor = NodeFloor.THIRD;

            // Determine building
            NodeBuilding building = NodeBuilding.FRANCIS45; // Default
            if(buildingChoiceBox.getValue().equals("FRANCIS45"))
                building = NodeBuilding.FRANCIS45;
            if(buildingChoiceBox.getValue().equals("BTM"))
                building = NodeBuilding.BTM;
            if(buildingChoiceBox.getValue().equals("SHAPIRO"))
                building = NodeBuilding.SHAPIRO;
            if(buildingChoiceBox.getValue().equals("TOWER"))
                building = NodeBuilding.TOWER;

            // Determine type
            NodeType type = NodeType.HALL; // Default
            if(nodeTypeChoiceBox.getValue().equals("ELEV"))
                type = NodeType.ELEV;
            if(nodeTypeChoiceBox.getValue().equals("HALL"))
                type = NodeType.HALL;
            if(nodeTypeChoiceBox.getValue().equals("REST"))
                type = NodeType.REST;
            if(nodeTypeChoiceBox.getValue().equals("DEPT"))
                type = NodeType.DEPT;
            if(nodeTypeChoiceBox.getValue().equals("STAI"))
                type = NodeType.STAI;
            if(nodeTypeChoiceBox.getValue().equals("CONF"))
                type = NodeType.CONF;
            if(nodeTypeChoiceBox.getValue().equals("EXIT"))
                type = NodeType.EXIT;
            if(nodeTypeChoiceBox.getValue().equals("INFO"))
                type = NodeType.INFO;
            if(nodeTypeChoiceBox.getValue().equals("LABS"))
                type = NodeType.LABS;
            if(nodeTypeChoiceBox.getValue().equals("SERV"))
                type = NodeType.SERV;

            System.out.println("Adding node?");
            // Find the existing node with that ID
            if(DatabaseController.getNode(nodeID.getText()) != null) {
                System.out.println("Adding node " + nodeID.getText());
                // Update Node
                //DatabaseController. ???? (nodeID.getText(), Integer.parseInt(xcoord.getText()), Integer.parseInt(ycoord.getText()), floor, building, type, lname.getText(), sname.getText(), team.getText());
            }
        }
    }
}
