package KioskApplication.controller;

import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeBuilding;
import KioskApplication.utility.NodeFloor;
import KioskApplication.utility.NodeType;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD_NODE;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminEditNodeController {
    AdminWindowController parent;

    AdminEditNodeController(AdminWindowController parent) {
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
        // Find closest node and display information ******

        // Loop through database checking the distance each node is,
        // if it is the new closest store its nodeID

        //xcoord.setText(String.valueOf(x));
        //ycoord.setText(String.valueOf(y));
    }

    public void onMapNodePressed(Node node){
        xcoord.setText(String.valueOf(node.getXcoord()));
        ycoord.setText(String.valueOf(node.getYcoord()));
        floorChoiceBox.setValue(convertFloor(node.getFloor().toString()));
        buildingChoiceBox.setValue(node.getBuilding().toString());
        nodeTypeChoiceBox.setValue(node.getNodeType().toString());
        lname.setText(node.getLongName());
        sname.setText(node.getShortName());
        teamAssignedChoiceBox.setValue(convertTeam(node.getTeamAssigned().toString()));
        nodeID.setText(node.getNodeID());
    }

    public String convertFloor(String eString){
        switch (eString){
            case "THIRD":
                return "03";
            case "SECOND":
                return "02";
            case "FIRST":
                return "01";
            case "LOWERLEVEL_2":
                return "L1";
            case "LOWERLEVEL_1":
                return "L2";
        }

        return "";
    }

    public String convertTeam(String eString){
        switch (eString){
            case "Team A":
                return "A";
            case "Team B":
                return "B";
            case "Team C":
                return "C";
            case "Team D":
                return "D";
            case "Team E":
                return "E";
            case "Team F":
                return "F";
            case "Team G":
                return "G";
            case "Team H":
                return "H";
            case "Team I":
                return "I";
        }
        return "";
    }


    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD_NODE);
    }

    @FXML
    void onBackPressed() throws IOException{
        System.out.println("Back Pressed\n");

        this.parent.switchTo(SIDEBAR_MENU);
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
    void deleteNode() throws IOException{
        if(nodeID.getText().equals("") || nodeID.getText() == null){ // If no node selected
            System.out.println("No Node Selected");
            // Notify user

        }
        else{
            System.out.println("Delete node: " + nodeID.getText());
            // Check to ensure node with that ID is in database
            Node delN = null;
            delN = MapEntity.getInstance().getNode(nodeID.getText());
            if(delN != null) {
                // Delete node
                boolean isSuccess = true;
                MapEntity.getInstance().removeNode(delN.getNodeID());

                if (isSuccess) // If successfully deleted
                    System.out.println("Node " + nodeID.getText() + " Deleted");
                else  // If DB failed to delete
                    System.out.println("Failed to remove node: " + nodeID.getText());
            }
            else { // If not, notify user
                System.out.println("Node " + nodeID.getText() + " is not in the database");
            }
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

            System.out.println("Editing node?");
            // Find the existing node with that ID
            if(MapEntity.getInstance().getNode(nodeID.getText()) != null) {
                System.out.println("Editing node " + nodeID.getText());
                // Create Node
                Node node1 = new Node(nodeID.getText(), (int)Double.parseDouble(xcoord.getText()), (int)Double.parseDouble(ycoord.getText()), floor, building, type, lname.getText(), sname.getText(), teamAssignedChoiceBox.getValue().toString());
                // Update Node
                MapEntity.getInstance().addNode(node1);
                System.out.println("Updated row(s) with nodeID: " + nodeID.getText());
            }
        }
    }
}
