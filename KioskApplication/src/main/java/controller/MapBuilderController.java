package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import utility.Display.Node.NodeDisplay;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;

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
    NodeBuilding nodeBuilding = NodeBuilding.FRANCIS15;
    TeamAssigned nodeTeamAssigned = TeamAssigned.I;
    @FXML
    private Tab nodeTab;
    private String nodeDialogString;
    @FXML
    private JFXDialogLayout nodeDialogLayout;
    @FXML
    private StackPane SPnodeDialog;
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
    @FXML
    private JFXButton setkiosklocation;
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
         * node Input put validators
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
        setkiosklocation.setDisable(false);
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
                    for(database.objects.Node changedTypeNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedTypeNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedTypeNode.getNodeID()) {
                                    changingNode.setNodeType(CBnodeType.getValue());
                                    System.out.println("1. New node Type: " + changingNode.getNodeType());
                                }
                            }
                        }
                        else {
                            System.out.println("2. adding to changed list: node Type, before: " + changedTypeNode.getNodeType());
                            changedTypeNode.setNodeType(CBnodeType.getValue());
                            observableChangedNodes.add(changedTypeNode); //current selected node is changed
                            System.out.println("2. adding to changed list: node Type, after: " + changedTypeNode.getNodeType());
                        }
                    }
                }
            }
        });
        CBnodeBuilding.valueProperty().addListener(new ChangeListener<NodeBuilding>() {
            @Override
            public void changed(ObservableValue<? extends NodeBuilding> observable, NodeBuilding oldValue, NodeBuilding newValue) {
                nodeBuilding = newValue;
                updateNodeID();
                if(!CBnodeBuilding.isDisable()) {
                    for(database.objects.Node changedBuildingNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedBuildingNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() ==changedBuildingNode.getNodeID()) {
                                    changingNode.setBuilding(CBnodeBuilding.getValue());
                                }
                            }
                        }
                        else {
                            changedBuildingNode.setBuilding(CBnodeBuilding.getValue());
                            observableChangedNodes.add(changedBuildingNode);
                        }
                    }
                }
            }
        });
        CBnodeTeamAssigned.valueProperty().addListener(new ChangeListener<TeamAssigned>() {
            @Override
            public void changed(ObservableValue<? extends TeamAssigned> observable, TeamAssigned oldValue, TeamAssigned newValue) {
                nodeTeamAssigned = newValue;
                updateNodeID();
                if(!CBnodeTeamAssigned.isDisable()) {
                    for(database.objects.Node changedTeamNode : observableSelectedNodes) {
                        if(observableChangedNodes.contains(changedTeamNode)) {
                            for(database.objects.Node changingNode : observableChangedNodes) {
                                if(changingNode.getNodeID() == changedTeamNode.getNodeID()) {
                                    changingNode.setTeamAssigned(CBnodeTeamAssigned.getValue().toString());
                                }
                            }
                        }
                        else {
                            changedTeamNode.setTeamAssigned(CBnodeTeamAssigned.getValue().toString());
                            observableChangedNodes.add(changedTeamNode);
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

       // setkiosklocation
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
            mapController.observableHighlightedSelectedNodes.clear();
            observableSelectedNodes.clear();
        }
        //detects double-click events
        else if(e.getClickCount() == 2)
        {
            //remove selected nodes, if any
            mapController.observableHighlightedSelectedNodes.clear();
            observableSelectedNodes.clear();

            //update node ID
            setNodeFieldToDefault();

            database.objects.Node newNode = new database.objects.Node(nodeID.getText(), (int)location.getX(), (int)location.getY(),
                    mapController.floorSelector.getValue(), CBnodeBuilding.getValue(), CBnodeType.getValue(), lName.getText(), sName.getText(), CBnodeTeamAssigned.getValue().toString());
            mapController.observableHighlightedNewNodes.clear();
            mapController.observableHighlightedNewNodes.add(newNode);
            observableNewNodes.clear();
            observableNewNodes.add(newNode);
        }
    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {

        if(observableNewNodes.contains(node)) {
            mapController.observableHighlightedNewNodes.clear();
            observableNewNodes.clear();
            return;
        }
        else if(observableSelectedNodes.contains(node)) {
            return;
        }
        else {
            //remove unsaved new node, if any
            mapController.observableHighlightedNewNodes.clear();
            observableNewNodes.clear();

            if(!mapController.observableHighlightedSelectedNodes.isEmpty()){
                mapController.observableHighlightedSelectedNodes.clear();
            }
            mapController.observableHighlightedSelectedNodes.add(node);

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
        mapController.observableHighlightedSelectedNodes.clear();
        observableSelectedNodes.clear();
        mapController.observableHighlightedNewNodes.clear();
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
    private void onbtInfoClicked() {
        if (tfNodeInfo.isVisible()) {
            tfNodeInfo.setVisible(false);
        } else {
            tfNodeInfo.setVisible(true);
        }
    }

    /**
     * Handles node Related operations
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
                    CBnodeTeamAssigned.setValue(convertToTeamEnum(observableNewNodes.get(0).getTeamAssigned()));

                    lName.setText(observableNewNodes.get(0).getLongName());
                    sName.setText(observableNewNodes.get(0).getShortName());
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
        for(database.objects.Node newNode : observableNewNodes) {
            if (MapEntity.getInstance().getNode(newNode.getNodeID()) == null) {
                try {
                    MapEntity.getInstance().addNode(newNode);
                    nodeDialogString += "node ID: " + newNode.getNodeID() + " was successfully saved.\n";
                } catch (DatabaseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error adding node to DB");
                    alert.setHeaderText("Error occurred while adding node to database.");
                    alert.setContentText(ex.toString());
                    alert.showAndWait();

                    nodeDialogString += "ERROR: node " + newNode.getNodeID() + " was not added to database.\n";
                }
            }  else { //duplicate node ID found
                nodeDialogString += "node ID: " + newNode.getNodeID() + "Duplicate ID found, not saved\n";
                loadDialog(event);
                nodeDialogString = "";
                return;
            }
        }
        mapController.observableHighlightedNewNodes.clear();
        observableNewNodes.clear();

        //clear new node list
        for(database.objects.Node changedNode : observableChangedNodes) {
            if(MapEntity.getInstance().getNode(changedNode.getNodeID()) != null) {
                try {
                    MapEntity.getInstance().editNode(changedNode);
                    nodeDialogString += "node ID " + changedNode.getNodeID() + " was successfully edited.\n";
                } catch (DatabaseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error editing node in DB");
                    alert.setHeaderText("Error occurred while updating a node in the database.");
                    alert.setContentText(ex.toString());
                    alert.showAndWait();

                    nodeDialogString += "ERROR: node " + changedNode.getNodeID() + " was not edited to database.\n";
                }
            } else {
                nodeDialogString += "node ID "+changedNode.getNodeID()+" not found.\n";
                loadDialog(event);
                nodeDialogString = "";
                return;
            }
        }
        observableChangedNodes.clear();
        loadDialog(event);
        nodeDialogString = "";
    }

//TODO
    @FXML
    private void loadDialog(ActionEvent event) {

        //TODO FIND A BETTER PLACE TO PUT THE DIALOG
        nodeDialogLayout.setHeading(new Text("System Information"));
        nodeDialogLayout.setBody(new Text(nodeDialogString));
        JFXDialog nodeDialog = new JFXDialog(SPnodeDialog, nodeDialogLayout, JFXDialog.DialogTransition.CENTER);
        JFXButton btnodeDialog= new JFXButton("OK");
        btnodeDialog.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                nodeDialog.close();
            }
        });
        nodeDialogLayout.setActions(btnodeDialog);

        nodeDialog.show();
    }

    @FXML
    private void kioskLocation(ActionEvent event){
        // do nothing right now
        SystemSettings.getInstance().setDefaultnode(heightLightedNode);
    }
}
