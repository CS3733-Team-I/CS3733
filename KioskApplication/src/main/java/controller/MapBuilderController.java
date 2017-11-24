package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;

public class MapBuilderController extends ScreenController {

    @FXML
    private TabPane builderTabPane;
    /**
     * Nodes related fields
     */
    Node heightLightedNode;
    //default, nodeFloor is in sync with main map
    NodeFloor nodeFloor = NodeFloor.THIRD;
    NodeType nodeType = NodeType.CONF;
    TeamAssigned nodeTeamAssigned = TeamAssigned.I;
    @FXML
    private Tab nodeTab;
    @FXML private JFXComboBox<NodeType> CBnodeType;
    @FXML private JFXComboBox<TeamAssigned> CBnodeTeamAssigned;
    @FXML private JFXComboBox<NodeBuilding> CBnodeBuilding;
    @FXML private JFXTextField lName;
    @FXML private JFXTextField sName;
    @FXML
    private JFXToggleButton btInfo;
    @FXML
    private TextFlow tfNodeInfo;
    @FXML
    private JFXToggleButton tbNodeAdvanced;
    @FXML
    private GridPane nodeAdvanced;
    @FXML
    private TextField xcoord;
    @FXML
    private TextField ycoord;
    @FXML
    private TextField nodeID;
    @FXML
    private Label lbXcoor;
    @FXML
    private Label lbYcoor;
    @FXML
    private Label lbNodeID;
    /**
     * Edges related fields
     */
    private Tab edgeTab;
    private Tab databaseTab;

    MapBuilderController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML
    public void initialize() {
        CBnodeType.getItems().addAll(NodeType.values());
        CBnodeTeamAssigned.getItems().addAll(TeamAssigned.values());
        CBnodeBuilding.getItems().addAll(NodeBuilding.values());

        Image infoIcon = new Image(getClass().getResource("/images/icons/informationIcon.png").toString());
        ImageView infoIconView = new ImageView(infoIcon);
        infoIconView.setRotate(90);
        infoIconView.setFitHeight(24);
        infoIconView.setFitWidth(24);

        btInfo.setGraphic(infoIconView);

        tfNodeInfo.setVisible(false);

        nodeAdvanced.setVisible(false);
        //update floor based on the floor selector
        nodeFloor = mapController.getCurrentFloor();
        mapController.floorSelector.valueProperty().addListener(new ChangeListener<NodeFloor>() {
            @Override
            public void changed(ObservableValue<? extends NodeFloor> observable, NodeFloor oldValue, NodeFloor newValue) {
                nodeFloor = mapController.getCurrentFloor();
                updateNodeID();
            }
        });
        CBnodeType.valueProperty().addListener(new ChangeListener<NodeType>() {
            @Override
            public void changed(ObservableValue<? extends NodeType> observable, NodeType oldValue, NodeType newValue) {
                nodeType = newValue;
                updateNodeID();
            }
        });
        CBnodeTeamAssigned.valueProperty().addListener(new ChangeListener<TeamAssigned>() {
            @Override
            public void changed(ObservableValue<? extends TeamAssigned> observable, TeamAssigned oldValue, TeamAssigned newValue) {
                nodeTeamAssigned = newValue;
                updateNodeID();
            }
        });
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/MapBuilderView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) {
    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {
        if(heightLightedNode != null) {
            mapController.dehighlightNode(heightLightedNode);
        }
        heightLightedNode = node;
        mapController.highlightNode(node);
        //switch to node tab
        SingleSelectionModel<Tab> selectionModel = builderTabPane.getSelectionModel();
        selectionModel.select(nodeTab);

        xcoord.setText(String.valueOf(node.getXcoord()));
        ycoord.setText(String.valueOf(node.getYcoord()));

        CBnodeBuilding.setValue(node.getBuilding());
        CBnodeType.setValue(node.getNodeType());
        lName.setText(node.getLongName());
        sName.setText(node.getShortName());
        CBnodeTeamAssigned.setValue(convertToTeamEnum(node.getTeamAssigned()));
        nodeID.setText(node.getNodeID());
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {
        //switch to edge tab
        SingleSelectionModel<Tab> selectionModel = builderTabPane.getSelectionModel();
        selectionModel.select(edgeTab);
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
    }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0, 350, 0, 0);
    }

    @FXML
    private void onbtInfoClicked() {
        if (tfNodeInfo.isVisible()) {
            tfNodeInfo.setVisible(false);
        } else {
            tfNodeInfo.setVisible(true);
        }
    }

    @FXML
    private void ontbNodeAdvancedClicked() {
        if(nodeAdvanced.isVisible()) {
            nodeAdvanced.setVisible(false);
        }
        else {
            nodeAdvanced.setVisible(true);
        }
    }

    public String getFloorTxt(){
        switch(nodeFloor.ordinal()){
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

    private void updateNodeID() {
        int nodeTypeCount = MapEntity.getInstance().getNodeTypeCount(nodeType, nodeFloor, "Team " + nodeTeamAssigned.toString());
        // Set the determined nodeID
        nodeID.setText(nodeTeamAssigned.toString() + nodeType.toString() + formatInt(nodeTypeCount) + nodeFloor.toString());
        // Check to see if nodeID already exists, if so find a open number between 1 and the nodeTypeCount
        // TODO implement this
    }

    public TeamAssigned convertToTeamEnum(String DBTeamString){
        switch (DBTeamString){
            case "Team A":
                return TeamAssigned.A;
            case "Team B":
                return TeamAssigned.B;
            case "Team C":
                return TeamAssigned.C;
            case "Team D":
                return TeamAssigned.D;
            case "Team E":
                return TeamAssigned.E;
            case "Team F":
                return TeamAssigned.F;
            case "Team G":
                return TeamAssigned.G;
            case "Team H":
                return TeamAssigned.H;
            case "Team I":
                return TeamAssigned.I;
            default:
                return TeamAssigned.I;
        }
    }
}
