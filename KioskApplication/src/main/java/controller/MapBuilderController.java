package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.validation.RequiredFieldValidator;
import database.objects.Edge;
import database.objects.Node;
import database.util.CSVFileUtil;
import entity.MapEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextFlow;
import utility.Display.Node.NodeDisplay;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import utility.Node.TeamAssigned;

import java.util.ArrayList;
import java.util.List;

public class MapBuilderController extends ScreenController {

    /**
     * General
     */
    @FXML
    private TabPane builderTabPane;
    SingleSelectionModel<Tab> selectionModel;
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
    //node operation buttons
    @FXML
    private JFXButton btNodeSave;
    @FXML
    private JFXButton btNodeUndo;
    @FXML
    private JFXButton btNodeRedo;
    @FXML
    private JFXButton btNodeDelete;
    /**
     * Edges related fields
     */
    private Tab edgeTab;
    private Tab databaseTab;

    /**
     * Selected List
     */

    private ObservableList<database.objects.Node> observableSelectedNodes;
    //TODO changed nodes
    private ObservableList<database.objects.Node> observableChangedNodes;
    private ObservableList<database.objects.Node> observableNewNodes;
    private ObservableList<database.objects.Edge> observableSelectedEdges;

    MapBuilderController(MainWindowController parent, MapController map) {
        super(parent, map);
        observableSelectedNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableChangedNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableNewNodes = FXCollections.<database.objects.Node>observableArrayList();
        observableSelectedEdges = FXCollections.<database.objects.Edge>observableArrayList();
    }

    @FXML
    public void initialize() {
        selectionModel = builderTabPane.getSelectionModel();
        /**
         * Node Input put validators
         */
        //TODO USE THE VALIDATORs
        RequiredFieldValidator lNameValidator = new RequiredFieldValidator();
        RequiredFieldValidator sNameValidator = new RequiredFieldValidator();
//        RequiredFieldValidator CBnodeBuildingValidator = new RequiredFieldValidator();
//        RequiredFieldValidator CBnodeTypeValidator = new RequiredFieldValidator();
//        RequiredFieldValidator CBnodeTeamAssignedValidator = new RequiredFieldValidator();

        lName.getValidators().add(lNameValidator);
        sName.getValidators().add(sNameValidator);

        lNameValidator.setMessage("Long Name Required");
        sNameValidator.setMessage("Short Name Required");

        //disable all fields
        CBnodeType.setDisable(true);
        CBnodeBuilding.setDisable(true);
        CBnodeTeamAssigned.setDisable(true);
        lName.setDisable(true);
        sName.setDisable(true);
        tbNodeAdvanced.setSelected(false);
        tbNodeAdvanced.setDisable(true);
        btNodeSave.setDisable(true);
        btNodeUndo.setDisable(true);
        btNodeRedo.setDisable(true);
        btNodeDelete.setDisable(true);

        //add items into the combobox
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
        //keep track on selected nodes and edges list
        observableSelectedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                updateNodeDisplay(NodeDisplay.SELECTED);
            }
        });
        //TODO
        observableChangedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {

            }
        });
        observableNewNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                updateNodeDisplay(NodeDisplay.NEW);
            }
        });
        observableSelectedEdges.addListener(new ListChangeListener<Edge>() {
            @Override
            public void onChanged(Change<? extends Edge> c) {
                System.out.println("Edge Change Detected");
            }
        });

        //set node fields to default
        setNodeFieldToDefault();
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/MapBuilderView.fxml");
        }

        return contentView;
    }

    //TODO
    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
        //detects double-click events
        if(e.getClickCount() == 2)
        {
            //remove selected nodes, if any
            mapController.observableHighlightededNodes.clear();
            observableSelectedNodes.clear();

            database.objects.Node newNode = new database.objects.Node(nodeID.getText(), (int)location.getX(), (int)location.getY(),
                    mapController.floorSelector.getValue(), CBnodeBuilding.getValue(), CBnodeType.getValue(), lName.getText(), sName.getText(), CBnodeTeamAssigned.getValue().toString());
            mapController.observableHighlightededNewNodes.clear();
            mapController.observableHighlightededNewNodes.add(newNode);
            observableNewNodes.clear();
            observableNewNodes.add(newNode);
        }
    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {
        //remove unsaved new nodes, if any IMPORTANT: THIS TWO LINES SHOULD ALWAYS BE AT THE BEGINNING (to remove new node after clicking on them)
        mapController.observableHighlightededNewNodes.clear();
        observableNewNodes.clear();

        mapController.observableHighlightededNodes.clear();
        mapController.observableHighlightededNodes.add(node);
        observableSelectedNodes.clear();
        observableSelectedNodes.add(node);

        //switch to node tab
        selectionModel.select(0);
        //fill in node fields in done in observable list listener
    }

    @Override
    public void onMapEdgeClicked(database.objects.Edge edge) {

        observableSelectedEdges.clear();
        observableSelectedEdges.add(edge);

        //switch to edge tab
        selectionModel.select(1);
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
    }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0, 350, 0, 0);
    }


    /**
     * Handles Database Related operations
     */
    @FXML
    void onReadClicked() {
        // TODO implement this better
        // Load nodes
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapAnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapBnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapCnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapDnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapEnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapFnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapGnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapHnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapInodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapWnodes.csv"));

        // Load edges
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapAedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapBedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapCedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapDedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapEedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapFedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapGedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapHedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapIedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapWedges.csv"));

        MapEntity.getInstance().readAllFromDatabase();

        getMapController().reloadDisplay();
    }

    @FXML
    void onSaveClicked() {
        // TODO Implement SaveCSV with different team letters
        /*
        try {
            URI mapINodes = new URI(getClass().getResource("/csv/MapInodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapINodes.getPath(), false);

            URI mapWNodes = new URI(getClass().getResource("/csv/MapWnodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapWNodes.getPath(), true);

            URI mapIEdges = new URI(getClass().getResource("/csv/MapIedges.csv").toString());
            CSVFileUtil.writeEdgesCSV(mapIEdges.getPath(), false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Handles Node Related operations
     */
    @FXML
    private void ontfNodeInfoClicked() {
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

    private void updateNodeDisplay(NodeDisplay nodeDisplay) {

        switch (nodeDisplay) {
            case SELECTED:
                if(observableSelectedNodes.size() == 0) { //no node selected
                    stNodeFieldDisable();
                }
                else if(observableSelectedNodes.size() == 1){
                    setNodeFieldEnable();
                    xcoord.setText(String.valueOf(observableSelectedNodes.get(0).getXcoord()));
                    ycoord.setText(String.valueOf(observableSelectedNodes.get(0).getYcoord()));

                    CBnodeBuilding.setValue(observableSelectedNodes.get(0).getBuilding());
                    CBnodeType.setValue(observableSelectedNodes.get(0).getNodeType());
                    lName.setText(observableSelectedNodes.get(0).getLongName());
                    sName.setText(observableSelectedNodes.get(0).getShortName());
                    CBnodeTeamAssigned.setValue(convertToTeamEnum(observableSelectedNodes.get(0).getTeamAssigned()));
                    nodeID.setText(observableSelectedNodes.get(0).getNodeID());
                }
                else {
                    //TODO
                    System.out.println("THIS SHOULD NEVER HAPPEN!!!!\n\n\n\n\n\n");
                }
                break;
            case CHANGED:
                break;
            case NEW:
                if(observableNewNodes.size() == 0) { //no node selected
                    stNodeFieldDisable();
                }
                else if(observableNewNodes.size() == 1){
                    setNodeFieldEnable();
                    xcoord.setText(String.valueOf(observableNewNodes.get(0).getXcoord()));
                    ycoord.setText(String.valueOf(observableNewNodes.get(0).getYcoord()));

                    CBnodeBuilding.setValue(observableNewNodes.get(0).getBuilding());
                    CBnodeType.setValue(observableNewNodes.get(0).getNodeType());
                    lName.setText(observableNewNodes.get(0).getLongName());
                    sName.setText(observableNewNodes.get(0).getShortName());
                    CBnodeTeamAssigned.setValue(convertToTeamEnum(observableNewNodes.get(0).getTeamAssigned()));
                    nodeID.setText(observableNewNodes.get(0).getNodeID());
                }
                else {
                    //TODO
                    System.out.println("THIS SHOULD NEVER HAPPEN!!!!\n\n\n\n\n\n");
                }
                break;
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

    private void setNodeFieldToDefault() {
        CBnodeBuilding.setValue(NodeBuilding.FRANCIS15);
        CBnodeType.setValue(NodeType.HALL);
        CBnodeTeamAssigned.setValue(TeamAssigned.I);
    }
    private void setNodeFieldEnable() {
        CBnodeType.setDisable(false);
        CBnodeBuilding.setDisable(false);
        CBnodeTeamAssigned.setDisable(false);
        lName.setDisable(false);
        sName.setDisable(false);
        //turn off advanced options
        //TODO change save, undo, redo disable/renable condition
        tbNodeAdvanced.setDisable(false);
        btNodeSave.setDisable(false);
        btNodeUndo.setDisable(false);
        btNodeRedo.setDisable(false);
        btNodeDelete.setDisable(false);
    }
    private void stNodeFieldDisable() {
        CBnodeType.setDisable(true);
        CBnodeBuilding.setDisable(true);
        CBnodeTeamAssigned.setDisable(true);
        lName.setDisable(true);
        sName.setDisable(true);
        //turn off advanced options
        tbNodeAdvanced.setSelected(false);
        tbNodeAdvanced.setDisable(true);
        //disable node operation buttons
        btNodeSave.setDisable(true);
        btNodeUndo.setDisable(true);
        btNodeRedo.setDisable(true);
        btNodeDelete.setDisable(true);
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
    //TODO
    public void SaveNode() {

    }
}
