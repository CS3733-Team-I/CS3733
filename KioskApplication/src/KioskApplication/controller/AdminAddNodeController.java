package KioskApplication.controller;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.*;
import KioskApplication.database.objects.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD_NODE;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminAddNodeController {
    AdminWindowController parent;

    AdminAddNodeController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML private TextField xcoord;

    @FXML private TextField ycoord;

    @FXML private TextField nodeID;

    @FXML private ChoiceBox floorChoiceBox;

    @FXML private ChoiceBox buildingChoiceBox;

    @FXML private ChoiceBox nodeTypeChoiceBox;

    @FXML private TextField lname;

    @FXML private TextField sname;

    @FXML private ChoiceBox teamAssignedChoiceBox;

    @FXML private Label errorMsg;


    public void setCoords(double x, double y){
        System.out.println("setCoords");
        xcoord.setText(String.valueOf(x));
        ycoord.setText(String.valueOf(y));
    }

    /*@FXML
    void onfloorClicked() throws IOException{
        floorChoiceBox.setItems(FXCollections.observableArrayList("L2", "L1", "G", "1", "2", "3"));
        floorChoiceBox.
    }*/

    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD_NODE);
    }

    @FXML
    void updateNodeID() throws IOException {
        System.out.println("updateNodeID");
        if(!floorChoiceBox.getValue().equals(null) && !floorChoiceBox.getValue().equals("--select--")
                && !nodeTypeChoiceBox.getValue().equals(null) && !nodeTypeChoiceBox.getValue().equals("--select--")
                && !teamAssignedChoiceBox.getValue().equals(null) && !teamAssignedChoiceBox.getValue().equals("--select--")){
            nodeID.setText(teamAssignedChoiceBox.getValue().toString() + nodeTypeChoiceBox.getValue().toString() + "000" + floorChoiceBox.getValue().toString());
        }
    }

    @FXML
    void onBackPressed() throws IOException{
        System.out.println("Back Pressed\n");

        this.parent.switchTo(SIDEBAR_MENU);
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
        else if(floorChoiceBox.getValue().equals(null) || floorChoiceBox.getValue().equals("--select--"))
            errorMsg.setText("You must input a building!");
        else if(buildingChoiceBox.getValue().equals(null) || buildingChoiceBox.getValue().equals("--select--"))
            errorMsg.setText("You must input a building!");
        else if(nodeTypeChoiceBox.getValue().equals(null) || nodeTypeChoiceBox.getValue().equals("--select--"))
            errorMsg.setText("You must input the node type!");
        else if(lname.getText().equals(null) || lname.getText().equals(""))
            errorMsg.setText("You must input a long name!");
        else if(sname.getText().equals(null) || sname.getText().equals(""))
            errorMsg.setText("You must input a short name!");
        else if(teamAssignedChoiceBox.getValue().equals(null) || teamAssignedChoiceBox.getValue().equals(""))
            errorMsg.setText("You must input the team assigned!");
        else {
            // Determine floor
            NodeFloor floor = NodeFloor.THIRD; // Default
            if(floorChoiceBox.getValue().equals("L2"))
                floor = NodeFloor.LOWERLEVEL_2;
            if(floorChoiceBox.getValue().equals("L1"))
                floor = NodeFloor.LOWERLEVEL_1;
            if(floorChoiceBox.getValue().equals("0G"))
                floor = NodeFloor.GROUND;
            if(floorChoiceBox.getValue().equals("01"))
                floor = NodeFloor.FIRST;
            if(floorChoiceBox.getValue().equals("02"))
                floor = NodeFloor.SECOND;
            if(floorChoiceBox.getValue().equals("03"))
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
            // Ensure there is no existing node with that ID
            if(MapEntity.getInstance().getNode(nodeID.getText()) != null) {

                // Creates node
                Node node1 = new Node(nodeID.getText(), Integer.parseInt(xcoord.getText()), Integer.parseInt(ycoord.getText()), floor, building, type, lname.getText(), sname.getText(), teamAssignedChoiceBox.getValue().toString());
                // Add Node
                MapEntity.getInstance().addNode(node1);
                System.out.println("Adding node " + nodeID.getText());
            }
        }
    }

}
