package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utility.ApplicationScreen;
import utility.NodeBuilding;
import utility.NodeFloor;
import utility.NodeType;

public class AdminAddNodeController extends ScreenController {
    NodeFloor floor;

    AdminAddNodeController(MainWindowController parent, MapController map) {
        super(parent, map);

        this.floor = map.getCurrentFloor();
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

    @FXML
    protected void initialize() {
        System.out.println(floor.ordinal());
        floorChoiceBox.setValue(getFloorTxt());
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

    /*@FXML
    void onfloorClicked() throws IOException{
        floorChoiceBox.setItems(FXCollections.observableArrayList("L2", "L1", "G", "1", "2", "3"));
        floorChoiceBox.
    }*/

    /*@FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD_NODE);
    }*/

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

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
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
        else if(teamAssignedChoiceBox.getValue().equals(null) || teamAssignedChoiceBox.getValue().equals(""))
            errorMsg.setText("You must input the team assigned!");
        else {
            // Determine floor
            NodeFloor floor = getNodeFloor();

            // Determine building
            NodeBuilding building = getNodeBuilding();

            // Determine type
            NodeType type = getNodeType();

            System.out.println("Adding node?");
            // Ensure there is no existing node with that ID
            if(MapEntity.getInstance().getNode(nodeID.getText()) == null) {
                //create new node
                Node node1 = new Node(nodeID.getText(), (int)Double.parseDouble(xcoord.getText()), (int)Double.parseDouble(ycoord.getText()), floor, building, type, lname.getText(), sname.getText(), "Team " + teamAssignedChoiceBox.getValue().toString());
                // Add Node
                System.out.println("ssssss");
                MapEntity.getInstance().addNode(node1);
                System.out.println("Adding node " + nodeID.getText());
                getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
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
            contentView = loadView("/view/addNode.fxml");
        }
        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) {
        System.out.println("setCoords");
        xcoord.setText(String.valueOf(location.getX()));
        ycoord.setText(String.valueOf(location.getY()));
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
        floorChoiceBox.setValue(getFloorTxt());
        updateNodeID();
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {}

    @Override
    public void onMapNodeClicked(Node node) {}

    @Override
    public void resetScreen() {
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
