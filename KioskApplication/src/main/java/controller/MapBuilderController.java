package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import utility.nodeDisplay.NodeDisplay;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;

import java.io.IOException;
import java.util.Map;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

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
    NodeType nodeType = NodeType.TEMP;
    NodeFloor nodeFloor = NodeFloor.THIRD;
    NodeBuilding nodeBuilding = NodeBuilding.FRANCIS15;
    TeamAssigned nodeTeamAssigned = TeamAssigned.I;
    RequiredFieldValidator lNameValidator = new RequiredFieldValidator();
    RequiredFieldValidator sNameValidator = new RequiredFieldValidator();
    @FXML
    private Tab nodeTab;
    private String nodeDialogString;
    @FXML
    private StackPane mapBuilderStackPane;
    @FXML private JFXComboBox<NodeType> CBnodeType;
    @FXML private JFXComboBox<TeamAssigned> CBnodeTeamAssigned;
    @FXML private JFXComboBox<NodeBuilding> CBnodeBuilding;
    @FXML private JFXTextField lName;
    @FXML private JFXTextField sName;
    @FXML
    private JFXButton btNodeInstruction;
    @FXML
    private GridPane Advance;
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
    @FXML
    private JFXButton btAdvance;
    /**
     * Edges related fields
     */

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
    public void initialize() throws IOException{

        Advance.setVisible(false);

        /**
         * Node Input put validators
         */
        lName.getValidators().add(lNameValidator);
        sName.getValidators().add(sNameValidator);
        lNameValidator.setMessage("Long Name Required");
        sNameValidator.setMessage("Short Name Required");

        //disable all fields
        btNodeSave.setDisable(true);
        setNodeAllDisable();

        //add items into the combobox
        CBnodeType.getItems().addAll(NodeType.values());
        CBnodeTeamAssigned.getItems().addAll(TeamAssigned.values());
        CBnodeBuilding.getItems().addAll(NodeBuilding.values());

        Image infoIcon = new Image(getClass().getResource("/images/icons/informationIcon.png").toString());
        ImageView infoIconView = new ImageView(infoIcon);
        infoIconView.setFitHeight(24);
        infoIconView.setFitWidth(24);
        btNodeInstruction.setGraphic(infoIconView);

        nodeID.setEditable(false);
        xcoord.setEditable(false);
        ycoord.setEditable(false);

        //update floor based on the floor selector
        nodeFloor = mapController.getCurrentFloor();

        //TODO MAKE THE NODE CHANGE REACTION MORE CONCRETELY
        mapController.floorSelector.valueProperty().addListener(new ChangeListener<NodeFloor>() {
            @Override
            public void changed(ObservableValue<? extends NodeFloor> observable, NodeFloor oldValue, NodeFloor newValue) {
                nodeFloor = mapController.getCurrentFloor();
                updateNodeID();
                //observableChangedNodes.addAll(observableSelectedNodes); //current selected node is changed
            }
        });
        CBnodeType.valueProperty().addListener(new ChangeListener<NodeType>() {
            @Override
            public void changed(ObservableValue<? extends NodeType> observable, NodeType oldValue, NodeType newValue) {
                nodeType = newValue;

                if(!CBnodeType.isDisable()) {
                    if(!observableNewNodes.isEmpty()) {
                        //System.out.println("Before setting node type: "+ observableNewNodes.get(0).getNodeType());
                        observableNewNodes.get(0).setNodeType(CBnodeType.getValue());
                        //System.out.println("After setting node type: "+ observableNewNodes.get(0).getNodeType());
                        updateNodeID();
                        return;
                    }
                    for(database.objects.Node changedTypeNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedTypeNode)) {
                            System.out.println("update changes in changed node");
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedTypeNode.getNodeID()) {
                                    changingNode.setNodeType(CBnodeType.getValue());
                                    updateNodeID();
                                }
                            }
                        }
                        else {
                            System.out.println("");
                            changedTypeNode.setNodeType(CBnodeType.getValue());
                            observableChangedNodes.add(changedTypeNode); //current selected node is changed
                            updateNodeID();
                        }
                    }
                }
            }
        });

        CBnodeBuilding.valueProperty().addListener(new ChangeListener<NodeBuilding>() {
            @Override
            public void changed(ObservableValue<? extends NodeBuilding> observable, NodeBuilding oldValue, NodeBuilding newValue) {
                nodeBuilding = newValue;
                if(!CBnodeBuilding.isDisable()) {
                    if(!observableNewNodes.isEmpty()) {
                        observableNewNodes.get(0).setBuilding(CBnodeBuilding.getValue());
                        updateNodeID();
                        return;
                    }
                    for(database.objects.Node changedBuildingNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedBuildingNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() ==changedBuildingNode.getNodeID()) {
                                    changingNode.setBuilding(CBnodeBuilding.getValue());
                                    updateNodeID();
                                }
                            }
                        }
                        else {
                            changedBuildingNode.setBuilding(CBnodeBuilding.getValue());
                            observableChangedNodes.add(changedBuildingNode);
                            updateNodeID();
                        }
                    }
                }
            }
        });
        CBnodeTeamAssigned.valueProperty().addListener(new ChangeListener<TeamAssigned>() {
            @Override
            public void changed(ObservableValue<? extends TeamAssigned> observable, TeamAssigned oldValue, TeamAssigned newValue) {
                nodeTeamAssigned = newValue;
                if(!CBnodeTeamAssigned.isDisable()) {
                    if(!observableNewNodes.isEmpty()) {
                        observableNewNodes.get(0).setTeamAssigned(CBnodeTeamAssigned.getValue().toString());
                        updateNodeID();
                        return;
                    }
                    for(database.objects.Node changedTeamNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedTeamNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedTeamNode.getNodeID()) {
                                    changingNode.setTeamAssigned(CBnodeTeamAssigned.getValue().toString());
                                    updateNodeID();
                                }
                            }
                        }
                        else {
                            changedTeamNode.setTeamAssigned(CBnodeTeamAssigned.getValue().toString());
                            observableChangedNodes.add(changedTeamNode);
                            updateNodeID();
                        }
                    }
                }
            }
        });
        //notify changed node list
        lName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if(!lName.isDisable()) {
                    if(!observableNewNodes.isEmpty()) {
                        //System.out.println("HERE NEW LNAME");
                        observableNewNodes.get(0).setLongName(lName.getText());
                    }
                    for(database.objects.Node changedLNameNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedLNameNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedLNameNode.getNodeID()) {
                                    changingNode.setLongName(lName.getText());
                                }
                            }
                        }
                        else {
                            changedLNameNode.setLongName(lName.getText());
                            observableChangedNodes.add(changedLNameNode);
                        }
                    }
                }
            }
        });
        sName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!sName.isDisable()) {
                    if(!observableNewNodes.isEmpty()) {
                        observableNewNodes.get(0).setShortName(sName.getText());
                    }
                    for(database.objects.Node changedSNameNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedSNameNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedSNameNode.getNodeID()) {
                                    changingNode.setShortName(sName.getText());
                                }
                            }
                        }
                        else {
                            changedSNameNode.setShortName(sName.getText());
                            observableChangedNodes.add(changedSNameNode);
                        }
                    }
                }
            }
        });
        nodeID.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //System.out.println("1. Begin Updating node ID");
                if(!nodeID.isDisabled()) {
                    //System.out.println("2. Actually Updating node ID");
                    if(!observableNewNodes.isEmpty()) {
                        observableNewNodes.get(0).setNodeID(nodeID.getText());
                    }
                    for(database.objects.Node changedIDNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedIDNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedIDNode.getNodeID()) {
                                    changingNode.setNodeID(nodeID.getText());
                                }
                            }
                        }
                        else {
                            changedIDNode.setNodeID(nodeID.getText());
                            observableChangedNodes.add(changedIDNode);
                        }
                    }
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
                        for(database.objects.Node addedChangedNode : c.getAddedSubList()) {
                            mapController.observableHighlightedChangedNodes.add(addedChangedNode);
                        }
                    }
                    if(c.wasRemoved()) {
                        for(database.objects.Node removedChangedNode : c.getRemoved()) {
                            mapController.observableHighlightedChangedNodes.remove(removedChangedNode);
                        }
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
            mapController.observableHighlightedSelectedNodes.clear();
            observableSelectedNodes.clear();
        }
        //detects double-click events
        else if(e.getClickCount() == 2)
        {
            //remove selected nodes, if any
            mapController.observableHighlightedSelectedNodes.clear();
            observableSelectedNodes.clear();

            database.objects.Node newNode = new database.objects.Node(nodeID.getText(), (int)location.getX(), (int)location.getY(),
                    mapController.floorSelector.getValue(), NodeBuilding.FRANCIS15, NodeType.TEMP, lName.getText(), sName.getText(), TeamAssigned.I.toString());

            mapController.isNodeAdded = false;
            mapController.observableHighlightedNewNodes.clear();
            mapController.observableHighlightedNewNodes.add(newNode);
            observableNewNodes.clear();
            observableNewNodes.add(newNode);
        }
    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {

        if(observableNewNodes.contains(node)) {
            mapController.isNodeAdded = false;
            mapController.observableHighlightedNewNodes.clear();
            observableNewNodes.clear();
            return;
        }
        else if(observableSelectedNodes.contains(node)) {
            return;
        }
        else {
            //remove unsaved new node, if any
            mapController.isNodeAdded = false;
            mapController.observableHighlightedNewNodes.clear();
            observableNewNodes.clear();

            //add new node to selected node list
            //TODO MAKE THIS AN EXCEPTION
            if(mapController.observableHighlightedSelectedNodes.size() > 2) {
                System.out.println("observableHighlightedSelectedNodes size greater than 2, Problematic!");
            }
            mapController.observableHighlightedSelectedNodes.clear();
            mapController.observableHighlightedSelectedNodes.add(node);

            if(observableSelectedNodes.size() > 2) {
                System.out.println("observableSelectedNodes size greater than 2, Problematic!");
            }
            observableSelectedNodes.clear();
            observableSelectedNodes.add(node);
        }

    }

    @Override
    public void onMapEdgeClicked(database.objects.Edge edge) {
        //remove changes on nodes
        //TODO make this into a method
        mapController.observableHighlightedSelectedNodes.clear();
        observableSelectedNodes.clear();

        mapController.isNodeAdded = false;
        mapController.observableHighlightedNewNodes.clear();
        observableNewNodes.clear();

        observableSelectedEdges.clear();
        observableSelectedEdges.add(edge);
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
        mapController.showEdgesBox.setSelected(false);
        mapController.showNodesBox.setSelected(false);
    }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0, 450, 0, 0);
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
                    for(database.objects.Node targetNode : observableChangedNodes) {
                        if(observableSelectedNodes.get(0).getNodeID() == targetNode.getNodeID()) {
                            xcoord.setText(String.valueOf(targetNode.getXcoord()));
                            ycoord.setText(String.valueOf(targetNode.getYcoord()));

                            CBnodeBuilding.setValue(targetNode.getBuilding());
                            CBnodeType.setValue(targetNode.getNodeType());
                            CBnodeTeamAssigned.setValue(convertToTeamEnum(targetNode.getTeamAssigned()));

                            lName.setText(targetNode.getLongName());
                            sName.setText(targetNode.getShortName());

                            nodeID.setText(targetNode.getNodeID());
                            setNodeFieldEnable();
                            return;
                        }
                    }
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
                    System.out.println("THIS SHOULD NEVER HAPPEN!\n");
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
                    CBnodeTeamAssigned.setValue(convertToTeamEnum(observableNewNodes.get(0).getTeamAssigned()));

                    lName.setText(observableNewNodes.get(0).getLongName());
                    sName.setText(observableNewNodes.get(0).getShortName());
                    //nodeID.setText(observableNewNodes.get(0).getNodeID());
                    updateNodeID();
                }
                else {
                    //TODO make this an exception
                    System.out.println("THIS SHOULD NEVER HAPPEN!\n");
                }
                break;
        }
        setNodeFieldEnable();
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
        System.out.println(observableSelectedNodes.get(0).getNodeID());
        if(nodeType == NodeType.ELEV) {
            /*String elevTypeCount = MapEntity.getInstance().getNodeTypeCount(nodeType, nodeFloor, "Team " + nodeTeamAssigned.toString());
            nodeID.setText(nodeTeamAssigned.toString() + nodeType.toString() + "00" + (elevTypeCount + trackElev) + convertFloor(mapController.floorSelector.getValue().toString()));
            trackElev++;*/
            String result = elevNameInChangedList();
            nodeID.setText(nodeTeamAssigned.toString() + nodeType.toString() + "00" + result + convertFloor(mapController.floorSelector.getValue().toString()));

        }
        else {
        //System.out.println("");
            int nodeTypeCountPrepared = 0;
            if(!MapEntity.getInstance().selectNodeID(Integer.parseInt(xcoord.getText()), Integer.parseInt(ycoord.getText()), nodeFloor, nodeType).equals("")){
                String nodeIDtemp = MapEntity.getInstance().selectNodeID(Integer.parseInt(xcoord.getText()), Integer.parseInt(ycoord.getText()), nodeFloor, nodeType);
                nodeID.setText(nodeIDtemp);
            }else{
                String nodeTypeCount = MapEntity.getInstance().getNodeTypeCount(nodeType, nodeFloor, "Team " + nodeTeamAssigned.toString(), "");
                nodeTypeCountPrepared += Integer.parseInt(nodeTypeCount) + countChangedList(nodeType);
                nodeID.setText(nodeTeamAssigned.toString() + nodeType.toString() + formatInt(nodeTypeCountPrepared-1) + convertFloor(nodeFloor.toString()));

            }


        }
        // Check to see if nodeID already exists, if so find a open number between 1 and the nodeTypeCount
        // TODO implement this
    }

    public int countChangedList(NodeType nodeType){
        int result=0;
        for(int i= 0; i<observableChangedNodes.size(); i++){
            if(observableChangedNodes.get(i).getNodeType() == nodeType && MapEntity.getInstance().selectNodeID(observableChangedNodes.get(i).getXcoord(), observableChangedNodes.get(i).getYcoord(), observableChangedNodes.get(i).getFloor(), observableChangedNodes.get(i).getNodeType()).equals("")){
                result++;
            }
        }
        return result;
    }
    
    private String elevNameInChangedList(){

        String result = "";
        for(int i=0; i<observableChangedNodes.size(); i++){
            if(observableChangedNodes.get(i).getNodeType() == NodeType.ELEV){
                result += observableChangedNodes.get(i).getNodeID().charAt(7);
            }
        }
        String preparedName = MapEntity.getInstance().generateElevName(nodeFloor, "Team" + nodeTeamAssigned.toString(), result);
        return preparedName;
    }

    public boolean checkExist(){
        for(int i=0; i<observableChangedNodes.size(); i++) {
            if (observableSelectedNodes.get(0).getNodeType() == observableChangedNodes.get(i).getNodeType() && observableSelectedNodes.get(0).getXyz() == observableChangedNodes.get(i).getXyz()){
                return false;
            }
        }
        return true;
    }

    private void setNodeFieldEnable() {
        CBnodeType.setDisable(false);
        CBnodeBuilding.setDisable(false);
        CBnodeTeamAssigned.setDisable(false);
        lName.setDisable(false);
        sName.setDisable(false);
        nodeID.setDisable(false);
        xcoord.setDisable(false);
        ycoord.setDisable(false);
        //turn off advanced options
        //TODO change save, undo, redo disable/renable condition
        btAdvance.setDisable(false);
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
        nodeID.setDisable(true);
        xcoord.setDisable(true);
        ycoord.setDisable(true);
        //turn off advanced options
        btAdvance.setDisable(true);
        //disable node operation buttons

        btNodeUndo.setDisable(true);
        btNodeRedo.setDisable(true);
        btNodeDelete.setDisable(true);
    }


    public TeamAssigned convertToTeamEnum(String DBTeamString){
        switch (DBTeamString){
            case "A":
                return TeamAssigned.A;
            case "B":
                return TeamAssigned.B;
            case "C":
                return TeamAssigned.C;
            case "D":
                return TeamAssigned.D;
            case "E":
                return TeamAssigned.E;
            case "F":
                return TeamAssigned.F;
            case "G":
                return TeamAssigned.G;
            case "H":
                return TeamAssigned.H;
            case "I":
                return TeamAssigned.I;
            default:
                return TeamAssigned.I;
        }
    }

    @FXML
    private void SaveNode(ActionEvent event) {

        if(lName.getText().trim().equals("")) {
            lName.validate();
            return;
        }
        if(sName.getText().trim().equals("")) {
            sName.validate();
            return;
        }

        for(database.objects.Node newNode : observableNewNodes) {
            if(newNode.getNodeType() == NodeType.TEMP) { //no type of temp is allowed to save
                nodeDialogString +=  "Node ID: " + newNode.getNodeID() + "\n" + "Node Type cannot be TEMP.\n\n";
                System.out.println(nodeDialogString);
                loadDialog(event);
                nodeDialogString = "";
                return;
            }
            else if (MapEntity.getInstance().getNode(newNode.getNodeID()) == null) {
                try {
                    //System.out.println("In saving NodeType: " + newNode.getNodeType());
                    //System.out.println("In saving NodeID: " + newNode.getNodeID());
                    MapEntity.getInstance().addNode(newNode);
                    nodeDialogString += "Node ID: " + newNode.getNodeID() +"\n" + " saved.\n\n";
                } catch (DatabaseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error adding node to DB");
                    alert.setHeaderText("Error occurred while adding node to database.");
                    alert.setContentText(ex.toString());
                    alert.showAndWait();

                    nodeDialogString += "ERROR: Node " + newNode.getNodeID() + " was not added to database.\n\n";
                }
            }  else { //duplicate node ID found
                nodeDialogString += "Node ID: " + newNode.getNodeID() + "\n" + "Duplicate ID found\n\n";
                System.out.println(nodeDialogString);
                loadDialog(event);
                nodeDialogString = "";
                return;
            }
        }

        //clear new node list
        mapController.isNodeAdded = true;
        mapController.observableHighlightedNewNodes.clear();
        observableNewNodes.clear();

        for(database.objects.Node changedNode : observableChangedNodes) {
                try {
                    MapEntity.getInstance().editNode(changedNode);
                    nodeDialogString += "Node ID " + changedNode.getNodeID() + "\n" + " edited.\n\n";
                } catch (DatabaseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error editing node in DB");
                    alert.setHeaderText("Error occurred while updating a node in the database.");
                    alert.setContentText(ex.toString());
                    alert.showAndWait();

                    nodeDialogString += "ERROR: Node " + changedNode.getNodeID() + " was not edited to database.\n";
                }
        }
        observableChangedNodes.clear();
        System.out.println(nodeDialogString);
        loadDialog(event);
        nodeDialogString = "";
    }


    @FXML
    private void loadDialog(ActionEvent event) {

        //TODO FIND A BETTER PLACE TO PUT THE DIALOG
        JFXDialogLayout nodeDialogLayout = new JFXDialogLayout();
        nodeDialogLayout.setHeading(new Text("System Information"));
        nodeDialogLayout.setBody(new Text(nodeDialogString));
        JFXDialog nodeDialog = new JFXDialog(mapBuilderStackPane, nodeDialogLayout, JFXDialog.DialogTransition.CENTER);
        JFXButton btnodeDialog= new JFXButton("OK");
        btnodeDialog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nodeDialog.close();
                mapBuilderStackPane.setMaxWidth(USE_PREF_SIZE);
                mapBuilderStackPane.setMinWidth(USE_PREF_SIZE);
                mapBuilderStackPane.setMaxHeight(USE_PREF_SIZE);
                mapBuilderStackPane.setMinHeight(USE_PREF_SIZE);
                mapBuilderStackPane.getChildren().remove(nodeDialog);
                mapBuilderStackPane.getChildren().remove(nodeDialogLayout);
            }
        });
        nodeDialogLayout.setActions(btnodeDialog);

        nodeDialog.show();
    }

    @FXML
    private void OnInstructionPressed(ActionEvent event) {
        btNodeInstruction.setDisable(true);
        JFXDialogLayout helpDlo = new JFXDialogLayout();
        helpDlo.setHeading(new Text("Help"));
        helpDlo.setBody(new Text("Click on a existing node to edit it.\n" +
                                 "Double click on the map to create a\n" +
                                 "new node. Click on a unsaved new node\n" +
                                 "again to remove it.\n\n" +
                                 "When a node is selected, click an empty\n" +
                                 "spot in map to deselect. Edit their\n" +
                                 "information and click on confirm to save."));
        JFXDialog helpD = new JFXDialog(mapBuilderStackPane, helpDlo, JFXDialog.DialogTransition.CENTER);


        JFXButton button = new JFXButton("Okay");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                helpD.close();
                btNodeInstruction.setDisable(false);
                mapBuilderStackPane.setMaxWidth(USE_PREF_SIZE);
                mapBuilderStackPane.setMinWidth(USE_PREF_SIZE);
                mapBuilderStackPane.setMaxHeight(USE_PREF_SIZE);
                mapBuilderStackPane.setMinHeight(USE_PREF_SIZE);
                mapBuilderStackPane.getChildren().remove(helpD);
                mapBuilderStackPane.getChildren().remove(helpDlo);
            }
        });
        helpDlo.setActions(button);
        helpD.show();
    }

    @FXML
    private void onAdvancePressed() {
        if(Advance.isVisible()) {
            Advance.setVisible(false);
        }
        else {
            Advance.setVisible(true);
        }
    }
}
