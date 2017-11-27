package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utility.ApplicationScreen;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class AdminNodeController extends ScreenController {
    NodeFloor floor;

    AdminNodeController(MainWindowController parent, MapController map) {
        super(parent, map);

        this.floor = map.getCurrentFloor();
    }

    private boolean isAdd = true;

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
    @FXML private Button submitButton;
    @FXML private Button DeleteBtn;

    @FXML
    protected void initialize() {
        resetScreen();
    }

    public String getFloorTxt(){
        switch(floor.ordinal()){
            case 0:
                return ("L2");
            case 1:
                return ("L1");
            case 2:
                return ("0G");
            case 3:
                return ("01");
            case 4:
                return ("02");
            case 5:
                return ("03");
            default:
                return ("00");
        }
    }

    @FXML
    void updateNodeID() {
        System.out.println("updateNodeID");
        if(!floorChoiceBox.getValue().equals(null) && !floorChoiceBox.getValue().equals("--select--")
                && !nodeTypeChoiceBox.getValue().equals(null) && !nodeTypeChoiceBox.getValue().equals("--select--")
                && !teamAssignedChoiceBox.getValue().equals(null) && !teamAssignedChoiceBox.getValue().equals("--select--")){

            NodeType nodeType = getNodeType();
            NodeFloor floor = getNodeFloor();
            // get the number of nodes of the same type, floor and team
            int nodeTypeCount = MapEntity.getInstance().getNodeTypeCount(nodeType, floor, "Team " + teamAssignedChoiceBox.getValue().toString());
            // Set the determined nodeID
            nodeID.setText(teamAssignedChoiceBox.getValue().toString() + nodeTypeChoiceBox.getValue().toString() + formatInt(nodeTypeCount) + floorChoiceBox.getValue().toString());
            // Check to see if nodeID already exists, if so find a open number between 1 and the nodeTypeCount
            // TODO implement this
        }
    }

    private String formatInt(int nodeTypeCount) {
        if (nodeTypeCount + 1 < 10) {
            return "00" + (nodeTypeCount + 1);
        } else if (nodeTypeCount + 1 < 100) {
            return "0" + (nodeTypeCount + 1);
        } else if (nodeTypeCount + 1 <= 999) {
            return (nodeTypeCount + 1) + "";
        } else {
            return "";
        }
    }

    @FXML
    void onBackPressed() {
        System.out.println("Back Pressed\n");
        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
    }

    @FXML
    void onSubmitClicked() {
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
        else if(teamAssignedChoiceBox.getValue().equals(null) || teamAssignedChoiceBox.getValue().equals("")) {
            errorMsg.setText("You must input the team assigned!");
        }
        else {
            // Determine floor
            NodeFloor floor = getNodeFloor();

            // Determine building
            NodeBuilding building = getNodeBuilding();

            // Determine type
            NodeType type = getNodeType();

            if(isAdd) {
                System.out.println("Adding node?");
                // Ensure there is no existing node with that ID
                if (MapEntity.getInstance().getNode(nodeID.getText()) == null) {
                    //create new node
                    Node node1 = new Node(nodeID.getText(), (int) Double.parseDouble(xcoord.getText()), (int) Double.parseDouble(ycoord.getText()), floor, building, type, lname.getText(), sname.getText(), convertToDBTeam(teamAssignedChoiceBox.getValue().toString()));
                    // Add node
                    MapEntity.getInstance().addNode(node1);
                    System.out.println("Adding node " + nodeID.getText());
                    resetScreen();
                }
            }else{
                System.out.println("Editing node?");
                // Find the existing node with that ID
                if(MapEntity.getInstance().getNode(nodeID.getText()) != null) {
                    System.out.println("Editing node " + nodeID.getText());
                    // Create node
                    Node node1 = new Node(nodeID.getText(), (int)Double.parseDouble(xcoord.getText()), (int)Double.parseDouble(ycoord.getText()), floor, building, type, lname.getText(), sname.getText(), convertToDBTeam(teamAssignedChoiceBox.getValue().toString()));
                    // Update node
                    MapEntity.getInstance().editNode(node1);
                    System.out.println("Updated row(s) with nodeID: " + nodeID.getText());
                    resetScreen();
                }
            }
        }
    }

    public NodeType getNodeType() {
        NodeType type = NodeType.HALL; // Default
        if(nodeTypeChoiceBox.getValue().toString().equals("ELEV"))
            type = NodeType.ELEV;
        if(nodeTypeChoiceBox.getValue().toString().equals("HALL"))
            type = NodeType.HALL;
        if(nodeTypeChoiceBox.getValue().toString().equals("REST"))
            type = NodeType.REST;
        if(nodeTypeChoiceBox.getValue().toString().equals("DEPT"))
            type = NodeType.DEPT;
        if(nodeTypeChoiceBox.getValue().toString().equals("STAI"))
            type = NodeType.STAI;
        if(nodeTypeChoiceBox.getValue().toString().equals("CONF"))
            type = NodeType.CONF;
        if(nodeTypeChoiceBox.getValue().toString().equals("EXIT"))
            type = NodeType.EXIT;
        if(nodeTypeChoiceBox.getValue().toString().equals("INFO"))
            type = NodeType.INFO;
        if(nodeTypeChoiceBox.getValue().toString().equals("LABS"))
            type = NodeType.LABS;
        if(nodeTypeChoiceBox.getValue().toString().equals("SERV"))
            type = NodeType.SERV;
        return type;
    }

    public NodeFloor getNodeFloor(){
        NodeFloor fl = NodeFloor.THIRD; // Default
        if(floorChoiceBox.getValue().toString().equals("L2"))
            fl = NodeFloor.LOWERLEVEL_2;
        if(floorChoiceBox.getValue().toString().equals("L1"))
            fl = NodeFloor.LOWERLEVEL_1;
        if(floorChoiceBox.getValue().toString().equals("0G"))
            fl = NodeFloor.GROUND;
        if(floorChoiceBox.getValue().toString().equals("01"))
            fl = NodeFloor.FIRST;
        if(floorChoiceBox.getValue().toString().equals("02"))
            fl = NodeFloor.SECOND;
        if(floorChoiceBox.getValue().toString().equals("03"))
            fl = NodeFloor.THIRD;
        return fl;
    }

    public NodeBuilding getNodeBuilding(){
        NodeBuilding building = NodeBuilding.FRANCIS45; // Default
        if(buildingChoiceBox.getValue().toString().equals("FRANCIS45"))
            building = NodeBuilding.FRANCIS45;
        if(buildingChoiceBox.getValue().toString().equals("BTM"))
            building = NodeBuilding.BTM;
        if(buildingChoiceBox.getValue().toString().equals("SHAPIRO"))
            building = NodeBuilding.SHAPIRO;
        if(buildingChoiceBox.getValue().toString().equals("TOWER"))
            building = NodeBuilding.TOWER;
        return building;
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/nodeSidebar.fxml");
        }
        return contentView;
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
        System.out.println("Team: [" + eString + "]");
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
            default:
                return "";
        }
    }

    public String convertToDBTeam(String eString){
        System.out.println("Team: [" + eString + "]");
        switch (eString){
            case "A":
                return "Team A";
            case "B":
                return "Team B";
            case "C":
                return "Team C";
            case "D":
                return "Team D";
            case "E":
                return "Team E";
            case "F":
                return "Team F";
            case "G":
                return "Team G";
            case "H":
                return "Team H";
            case "I":
                return "Team I";
            default:
                return "";
        }
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
        System.out.println("setCoords");
        resetScreen();
        xcoord.setText(String.valueOf(location.getX()));
        ycoord.setText(String.valueOf(location.getY()));
        isAdd = true;
        submitButton.setText("Add");
        DeleteBtn.setVisible(false);
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
        if(isAdd) {
            floorChoiceBox.setValue(getFloorTxt());
            updateNodeID();
        }
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {}

    @Override
    public void onMapNodeClicked(Node node) {
        xcoord.setText(String.valueOf(node.getXcoord()));
        ycoord.setText(String.valueOf(node.getYcoord()));
        floorChoiceBox.setValue(convertFloor(node.getFloor().toString()));
        buildingChoiceBox.setValue(node.getBuilding().toString());
        nodeTypeChoiceBox.setValue(node.getNodeType().toString());
        lname.setText(node.getLongName());
        sname.setText(node.getShortName());
        String team = convertTeam(node.getTeamAssigned().toString());
        teamAssignedChoiceBox.setValue(team);
        nodeID.setText(node.getNodeID());
        isAdd = false;
        DeleteBtn.setVisible(true);
        submitButton.setText("Edit");
    }
    @FXML
    void deleteNode() throws IOException {
        if(nodeID.getText().equals("") || nodeID.getText() == null){ // If no node selected
            System.out.println("No node Selected");
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

                if (isSuccess) { // If successfully deleted
                    System.out.println("node " + nodeID.getText() + " Deleted");
                    resetScreen();
                }
                else  // If DB failed to delete
                    System.out.println("Failed to remove node: " + nodeID.getText());
            }
            else { // If not, notify user
                System.out.println("node " + nodeID.getText() + " is not in the database");
            }
        }
    }

    @Override
    public void resetScreen() {
        isAdd = true;
        DeleteBtn.setVisible(false);
        submitButton.setText("Add");
        xcoord.setText("");
        ycoord.setText("");
        floorChoiceBox.setValue(getFloorTxt());
        buildingChoiceBox.setValue("--select--");
        nodeTypeChoiceBox.setValue("--select--");
        lname.setText("");
        sname.setText("");
        teamAssignedChoiceBox.setValue("I");
        errorMsg.setText("");
        nodeID.setText("");
    }
}
