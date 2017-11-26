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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import utility.Display.Node.NodeDisplay;
import utility.Node.NodeBuilding;
import utility.Node.NodeFloor;
import utility.Node.NodeType;
import utility.Node.TeamAssigned;

import javax.swing.*;

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
    NodeType nodeType = NodeType.TEMP;
    TeamAssigned nodeTeamAssigned = TeamAssigned.I;
    @FXML
    private Tab nodeTab;
    private String nodeDialogString;
    @FXML
    private AnchorPane nodeDialogAnchor;
    @FXML private JFXComboBox<NodeType> CBnodeType;
    @FXML private JFXComboBox<TeamAssigned> CBnodeTeamAssigned;
    @FXML private JFXComboBox<NodeBuilding> CBnodeBuilding;
    @FXML private JFXTextField lName;
    @FXML private JFXTextField sName;
    @FXML
    private JFXToggleButton tbNodeInstruction;
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
        tbNodeInstruction.setGraphic(infoIconView);


        nodeID.setEditable(false);
        xcoord.setEditable(false);
        ycoord.setEditable(false);
        tfNodeInfo.setVisible(false);
        nodeAdvanced.setVisible(false);

        //update floor based on the floor selector
        nodeFloor = mapController.getCurrentFloor();

        //TODO MAKE THE NODE CHANGE REACTION MORE CONCRETELY
        mapController.floorSelector.valueProperty().addListener(new ChangeListener<NodeFloor>() {
            @Override
            public void changed(ObservableValue<? extends NodeFloor> observable, NodeFloor oldValue, NodeFloor newValue) {
                nodeFloor = mapController.getCurrentFloor();
                //updateNodeID();
                //observableChangedNodes.addAll(observableSelectedNodes); //current selected node is changed
            }
        });
        tbNodeAdvanced.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(nodeAdvanced.isVisible()) {
                    nodeAdvanced.setVisible(false);
                }
                else {
                    nodeAdvanced.setVisible(true);
                }
            }
        });
        tbNodeInstruction.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (tfNodeInfo.isVisible()) {
                    tfNodeInfo.setVisible(false);
                } else {
                    tfNodeInfo.setVisible(true);
                }

            }
        });
        CBnodeType.valueProperty().addListener(new ChangeListener<NodeType>() {
            @Override
            public void changed(ObservableValue<? extends NodeType> observable, NodeType oldValue, NodeType newValue) {
                nodeType = newValue;
                updateNodeID();
                if(!CBnodeType.isDisable()) {
                    observableChangedNodes.addAll(observableSelectedNodes); //current selected node is changed
                }
            }
        });
        CBnodeTeamAssigned.valueProperty().addListener(new ChangeListener<TeamAssigned>() {
            @Override
            public void changed(ObservableValue<? extends TeamAssigned> observable, TeamAssigned oldValue, TeamAssigned newValue) {
                nodeTeamAssigned = newValue;
                updateNodeID();
                if(!CBnodeTeamAssigned.isDisable()) {
                    observableChangedNodes.addAll(observableSelectedNodes); //current selected node is changed
                }
            }
        });
        //notify changed node list
        lName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!lName.isDisable()) {
                    observableChangedNodes.addAll(observableSelectedNodes); //current selected node is changed
                }
            }
        });
        sName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!sName.isDisable()) {
                    observableChangedNodes.addAll(observableSelectedNodes); //current selected node is changed
                }
            }
        });
        //keep track on selected, new, and changed nodes and edges list
        observableSelectedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        updateNodeDisplay(NodeDisplay.SELECTED);
                        return;
                    }
                    else if(c.wasAdded()) {
                        updateNodeDisplay(NodeDisplay.SELECTED);
                    }
                }
                if(observableSelectedNodes.isEmpty() && observableNewNodes.isEmpty()) {
                    setNodeAllDisable();
                }
            }
        });
        observableChangedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                updateNodeDisplay(NodeDisplay.CHANGED);//currently do nothing
                while(c.next()) {
                    if(c.wasAdded()) {
                        for(database.objects.Node changedNode : c.getAddedSubList()) {
                            mapController.observableHighlightededChangedNodes.add(changedNode);
                        }
                    }
                    //TODO
                    if(c.wasRemoved()) {
                        return;
                    }
                }
                if(observableChangedNodes.isEmpty() && observableNewNodes.isEmpty()){
                    btNodeSave.setDisable(true);
                }
                else {
                    btNodeSave.setDisable(false);
                }
            }
        });
        observableNewNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while(c.next()) {
                    if(c.wasRemoved()) {
                        updateNodeDisplay(NodeDisplay.NEW);
                        return;
                    }
                    else if(c.wasAdded()) {
                        updateNodeDisplay(NodeDisplay.NEW);
                    }
                }
                if(observableChangedNodes.isEmpty() && observableNewNodes.isEmpty()){
                    btNodeSave.setDisable(true);
                }
                else {
                    btNodeSave.setDisable(false);
                }
                if(observableSelectedNodes.isEmpty() && observableNewNodes.isEmpty()) {
                    setNodeAllDisable();
                }
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

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
        //deselect node on one mouse click
        if(e.getClickCount() == 1) {
            mapController.observableHighlightededSelectedNodes.clear();
            observableSelectedNodes.clear();
        }
        //detects double-click events
        else if(e.getClickCount() == 2)
        {
            //remove selected nodes, if any
            mapController.observableHighlightededSelectedNodes.clear();
            observableSelectedNodes.clear();

            //update Node ID
            setNodeFieldToDefault();

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

        if(observableNewNodes.contains(node)) {
            mapController.observableHighlightededNewNodes.clear();
            observableNewNodes.clear();
            return;
        }
        else if(observableSelectedNodes.contains(node)) {
            return;
        }
        else {
            //remove unsaved new node, if any
            mapController.observableHighlightededNewNodes.clear();
            observableNewNodes.clear();

            if(!mapController.observableHighlightededSelectedNodes.isEmpty()){
                mapController.observableHighlightededSelectedNodes.clear();
            }
            mapController.observableHighlightededSelectedNodes.add(node);

            if(!observableSelectedNodes.isEmpty()) {
                observableSelectedNodes.clear();
            }
            observableSelectedNodes.add(node);

            //switch to node tab
            selectionModel.select(0);
            //fill in node fields in done in observable list listener
        }

    }

    @Override
    public void onMapEdgeClicked(database.objects.Edge edge) {
        //remove changes on nodes
        //TODO make this into a method
        mapController.observableHighlightededSelectedNodes.clear();
        observableSelectedNodes.clear();
        mapController.observableHighlightededNewNodes.clear();
        observableNewNodes.clear();

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


//TODO REFACTOR THIS USING "CHANGE"
    private void updateNodeDisplay(NodeDisplay nodeDisplay) {
        setNodeAllDisable();
        switch (nodeDisplay) {
            case SELECTED:
                if(observableSelectedNodes.size() == 0) { //no node selected
                    setNodeAllDisable();
                }
                else if(observableSelectedNodes.size() == 1){

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
                    //TODO make this an exception
                    System.out.println("THIS SHOULD NEVER HAPPEN!!!!\n\n\n\n\n\n");
                }
                break;
            case CHANGED: //currently do nothing
                break;
            case NEW:
                if(observableNewNodes.size() == 0) { //no node selected
                    setNodeAllDisable();
                }
                else if(observableNewNodes.size() == 1){

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
                    //TODO make this an exception
                    System.out.println("THIS SHOULD NEVER HAPPEN!!!!\n\n\n\n\n\n");
                }
                break;
        }
        setNodeFieldEnable();
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
        CBnodeType.setValue(NodeType.TEMP);
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
        btNodeUndo.setDisable(false);
        btNodeRedo.setDisable(false);
        btNodeDelete.setDisable(false);
    }
    private void setNodeAllDisable() {
        CBnodeType.setDisable(true);
        CBnodeBuilding.setDisable(true);
        CBnodeTeamAssigned.setDisable(true);
        lName.setDisable(true);
        sName.setDisable(true);
        //turn off advanced options
        tbNodeAdvanced.setSelected(false);
        tbNodeAdvanced.setDisable(true);
        //disable node operation buttons
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

    @FXML
    private void SaveNode() {
        for(database.objects.Node newNode : observableNewNodes) {
            if (MapEntity.getInstance().getNode(nodeID.getText()) == null) {
                MapEntity.getInstance().addNode(newNode);
                nodeDialogString += "Node ID: " + nodeID + " was successfully saved.\n";
            }
            else { //duplicate node ID found
                nodeDialogString += "Node ID: " + nodeID + "Duplicate ID found, not saved\n";
            }
        }
        mapController.observableHighlightededNewNodes.clear();
        observableNewNodes.clear();

        //clear new node list
        for(database.objects.Node changedNode : observableChangedNodes) {
            if(MapEntity.getInstance().getNode(nodeID.getText()) != null) {
                MapEntity.getInstance().editNode(changedNode);
                nodeDialogString += "Node ID "+changedNode.getNodeID()+" was successfully edited.\n";
            }
            else {
                nodeDialogString += "Node ID "+changedNode.getNodeID()+" not found.\n";
            }
        }
        System.out.println(nodeDialogString);
    }

//TODO
    @FXML
    private void loadDialog(ActionEvent event) {

    }
}
