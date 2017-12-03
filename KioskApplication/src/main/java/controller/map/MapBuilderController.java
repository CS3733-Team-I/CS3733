package controller.map;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import controller.MainWindowController;
import controller.ScreenController;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import utility.ResourceManager;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;
import utility.node.NodeSelectionType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class MapBuilderController extends ScreenController {
    @FXML private TabPane builderTabPane;

    //default, nodeFloor is in sync with main map
    NodeType nodeType = NodeType.HALL;
    NodeFloor nodeFloor = NodeFloor.THIRD;
    NodeBuilding nodeBuilding = NodeBuilding.FRANCIS15;
    TeamAssigned nodeTeamAssigned = TeamAssigned.I;
    RequiredFieldValidator lNameValidator = new RequiredFieldValidator();
    RequiredFieldValidator sNameValidator = new RequiredFieldValidator();

    @FXML private Tab nodeTab;
    private String nodeDialogString;

    @FXML private StackPane mapBuilderStackPane;
    @FXML private JFXComboBox<NodeType> CBnodeType;
    @FXML private JFXComboBox<TeamAssigned> CBnodeTeamAssigned;
    @FXML private JFXComboBox<NodeBuilding> CBnodeBuilding;
    @FXML private JFXTextField lName;
    @FXML private JFXTextField sName;
    @FXML private JFXButton btNodeInstruction;
    @FXML private GridPane Advance;
    @FXML private TextField xcoord;
    @FXML private TextField ycoord;
    @FXML private TextField nodeID;
    @FXML private Label lbXcoor;
    @FXML private Label lbYcoor;
    @FXML private Label lbNodeID;

    //node operation buttons
    @FXML private JFXButton btNodeSave;
    @FXML private JFXButton btNodeUndo;
    @FXML private JFXButton btNodeRedo;
    @FXML private JFXButton btNodeDelete;
    @FXML private JFXButton btAdvance;
    @FXML private JFXButton setkiosklocation;

    // Edges related fields
    @FXML private JFXListView<Node> lvConnectedNodes;
    @FXML private JFXButton btExpand;
    @FXML JFXPopup popup;

    // Observer lists
    private SimpleObjectProperty<Node> selectedNode;
    private SimpleObjectProperty<Node> newNode;
    private ObservableList<Node> observableChangedNodes;


    public MapBuilderController(MainWindowController parent, MapController map) {
        super(parent, map);

        selectedNode = new SimpleObjectProperty<>();
        newNode = new SimpleObjectProperty<>();
        observableChangedNodes = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() throws IOException{
        Advance.setVisible(false);

        // node Input put validators
        lName.getValidators().add(lNameValidator);
        sName.getValidators().add(sNameValidator);
        lNameValidator.setMessage("Long Name Required");
        sNameValidator.setMessage("Short Name Required");

        //disable all fields
        btNodeSave.setDisable(true);
        setkiosklocation.setDisable(true);
        setEditingDisabled();

        //add items into the combobox
        CBnodeType.getItems().addAll(NodeType.values());
        CBnodeTeamAssigned.getItems().addAll(TeamAssigned.values());
        CBnodeBuilding.getItems().addAll(NodeBuilding.values());

        Image infoIcon = ResourceManager.getInstance().getImage("/images/icons/informationIcon.png");
        ImageView infoIconView = new ImageView(infoIcon);
        infoIconView.setFitHeight(24);
        infoIconView.setFitWidth(24);

        Image deleteIcon = new Image(getClass().getResource("/images/icons/delete.png").toString());
        ImageView deleteIconView = new ImageView(deleteIcon);
        deleteIconView.setFitHeight(24);
        deleteIconView.setFitWidth(24);

        Image goIcon = new Image(getClass().getResource("/images/icons/go.png").toString());
        ImageView goIconView = new ImageView(goIcon);
        goIconView.setFitHeight(24);
        goIconView.setFitWidth(24);

        Image expandIcon = new Image(getClass().getResource("/images/icons/expand.png").toString());
        ImageView expandIconView = new ImageView(expandIcon);
        expandIconView.setFitHeight(24);
        expandIconView.setFitWidth(24);

        Image plusIcon = new Image(getClass().getResource("/images/icons/plus.png").toString());
        ImageView plusIconView = new ImageView(plusIcon);
        plusIconView.setFitHeight(24);
        plusIconView.setFitWidth(24);

        btNodeInstruction.setGraphic(infoIconView);
        btExpand.setGraphic(expandIconView);

        nodeID.setEditable(false);
        xcoord.setEditable(false);
        ycoord.setEditable(false);

        //update floor based on the floor selector
        nodeFloor = getMapController().getCurrentFloor();

        CBnodeType.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodeType = newValue;

            if(!CBnodeType.isDisable()) {
                if(!observableNewNodes.isEmpty()) {
                    //System.out.println("Before setting node type: "+ observableNewNodes.get(0).getNodeType());
                    observableNewNodes.get(0).setNodeType(CBnodeType.getValue());
                    //System.out.println("After setting node type: "+ observableNewNodes.get(0).getNodeType());
                    updateNodeID();
                    return;
                }
                for(Node changedTypeNode : observableSelectedNodes) {
                    if(observableChangedNodes.contains(changedTypeNode)) {
                        System.out.println("update changes in changed node");
                        for(Node changingNode : observableChangedNodes) {
                            if(changingNode.getXyz().equals(changedTypeNode.getXyz())) {
                                changingNode.setNodeType(CBnodeType.getValue());
                                updateNodeID();
                            }
                        }
                    }
                    else {
                        changedTypeNode.setNodeType(CBnodeType.getValue());
                        updateNodeID();
                        if(!observableChangedNodes.contains(changedTypeNode)){
                            observableChangedNodes.add(changedTypeNode);
                        } //current selected node is changed
                    }
                }
            }
        });

        CBnodeBuilding.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodeBuilding = newValue;
            if(!CBnodeBuilding.isDisable()) {
                if(!observableNewNodes.isEmpty()) {
                    observableNewNodes.get(0).setBuilding(CBnodeBuilding.getValue());
                    updateNodeID();
                    return;
                }
                for(Node changedBuildingNode : observableSelectedNodes) {
                    if(observableChangedNodes.contains(changedBuildingNode)) {
                        for(Node changingNode : observableChangedNodes) {
                            if(changingNode.getXyz().equals(changedBuildingNode.getXyz())) {
                                changingNode.setBuilding(CBnodeBuilding.getValue());
                                updateNodeID();
                            }
                        }
                    }
                    else {
                        changedBuildingNode.setBuilding(CBnodeBuilding.getValue());
                        updateNodeID();
                        if(!observableChangedNodes.contains(changedBuildingNode)){
                            observableChangedNodes.add(changedBuildingNode);
                        }

                    }
                }
            }
        });

        CBnodeTeamAssigned.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodeTeamAssigned = newValue;
            if(!CBnodeTeamAssigned.isDisable()) {
                if(!observableNewNodes.isEmpty()) {
                    observableNewNodes.get(0).setTeamAssigned("Team " + CBnodeTeamAssigned.getValue().toString());
                    updateNodeID();
                    return;
                }
                for(Node changedTeamNode : observableSelectedNodes) {
                    if(observableChangedNodes.contains(changedTeamNode)) {
                        for(Node changingNode : observableChangedNodes) {
                            if(changingNode.getXyz().equals(changedTeamNode.getXyz())) {
                                changingNode.setTeamAssigned("Team " + CBnodeTeamAssigned.getValue().toString());
                                updateNodeID();
                            }
                        }
                    }
                    else {
                        changedTeamNode.setTeamAssigned("Team " + CBnodeTeamAssigned.getValue().toString());
                        updateNodeID();
                        if(!observableChangedNodes.contains(changedTeamNode)){
                            observableChangedNodes.add(changedTeamNode);
                        }

                    }
                }
            }
        });

        //notify changed node list
        lName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!lName.isDisable()) {
                lName.resetValidation();
                if(!observableNewNodes.isEmpty()) {
                    observableNewNodes.get(0).setLongName(lName.getText());
                }
                for(Node changedLNameNode : observableSelectedNodes) {
                    if(observableChangedNodes.contains(changedLNameNode)) {
                        for(Node changingNode : observableChangedNodes) {
                            if(changingNode.getXyz().equals(changedLNameNode.getXyz())) {
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
        });
        sName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!sName.isDisable()) {
                sName.resetValidation();
                if(!observableNewNodes.isEmpty()) {
                    observableNewNodes.get(0).setShortName(sName.getText());
                }
                for(Node changedSNameNode : observableSelectedNodes) {
                    if(observableChangedNodes.contains(changedSNameNode)) {
                        for(Node changingNode : observableChangedNodes) {
                            if(changingNode.getXyz().equals(changedSNameNode.getXyz())) {
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
        });

        nodeID.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!nodeID.isDisabled()) {
                if(!observableNewNodes.isEmpty()) {
                    observableNewNodes.get(0).setNodeID(nodeID.getText());
                }
                for(Node changedIDNode : observableSelectedNodes) {
                    if(observableChangedNodes.contains(changedIDNode)) {
                        for(Node changingNode : observableChangedNodes) {
                            if(changingNode.getXyz().equals(changedIDNode.getXyz())) {
                                changingNode.setNodeID(nodeID.getText());
                            }
                        }
                    }
                    else {
                        changedIDNode.setNodeID(nodeID.getText());
                        observableChangedNodes.add(changedIDNode);
                    }
                    //reflect node ID changes in mapEntity also in edges
                    for(Node node : MapEntity.getInstance().getAllNodes()) {
                        if(node.getXyz().equals(changedIDNode.getXyz())) {

                            for(Edge edge : MapEntity.getInstance().getEdges(new Node(oldValue))) {

                                System.out.println("3. Begin Updating edge ID");

                                if(edge.getNode1ID().equals(oldValue)) {
                                    edge.setNode1ID(nodeID.getText());
                                }
                                if(edge.getNode2ID().equals(oldValue)) {
                                    edge.setNode2ID(nodeID.getText());
                                }
                                System.out.println("EdgeID:" + edge.getEdgeID() + " now Connecting: " + edge.getNode1ID() + " : " + edge.getNode2ID());
                            }
                            node.setNodeID(nodeID.getText());
                        }
                    }
                }
            }
        });

        //keep track on selected, new, and changed nodes and edges list
        observableSelectedNodes.addListener((ListChangeListener<Node>) c -> {
            while(c.next()) {
                if(c.wasAdded()) {
                    for(Node node : c.getAddedSubList()) {
                        getMapController().setNodeSelected(node, NodeSelectionType.SELECTED);
                    }

                    for(Node connectedNode : MapEntity.getInstance().getConnectedNodes(observableSelectedNodes.get(0))) {
                        lvConnectedNodes.getItems().add(connectedNode);
                    }

                    updateNodeDisplay(NodeSelectionType.SELECTED);
                }

                if(c.wasRemoved()) {
                    lvConnectedNodes.getItems().clear();

                    for(Node node : c.getRemoved()) {
                        getMapController().setNodeSelected(node, NodeSelectionType.NORMAL);
                    }
                }
            }

            if(observableSelectedNodes.isEmpty() && observableNewNodes.isEmpty()) {
                setEditingDisabled();
            } else {
                setEditingEnabled();
            }
        });

        observableChangedNodes.addListener((ListChangeListener<Node>) c -> {
            updateNodeDisplay(NodeSelectionType.CHANGED);

            while(c.next()) {
                if(c.wasAdded()) {
                    for(Node node : c.getAddedSubList()) {
                        getMapController().setNodeSelected(node, NodeSelectionType.CHANGED);
                    }
                }
                if(c.wasRemoved()) {
                    for(Node node : c.getRemoved()) {
                        getMapController().setNodeSelected(node, NodeSelectionType.NORMAL);
                    }
                }
            }

            if(observableChangedNodes.isEmpty() && observableNewNodes.isEmpty()){
                btNodeSave.setDisable(true);
            } else {
                btNodeSave.setDisable(false);
            }
        });

        observableNewNodes.addListener((ListChangeListener<Node>) c -> {
            while(c.next()) {
                if(c.wasAdded()) {
                    for(Node node : c.getAddedSubList()) {
                        getMapController().addNode(node, NodeSelectionType.NEW);
                    }

                    updateNodeDisplay(NodeSelectionType.NEW);
                }
                if(c.wasRemoved()) {
                    for(Node node : c.getRemoved()) {
                        getMapController().removeNode(node);
                    }

                    updateNodeDisplay(NodeSelectionType.NEW);
                }
            }

            if(observableChangedNodes.isEmpty() && observableNewNodes.isEmpty()){
                btNodeSave.setDisable(true);
            } else {
                btNodeSave.setDisable(false);
            }

            if(observableSelectedNodes.isEmpty() && observableNewNodes.isEmpty()) {
                setEditingDisabled();
            }
        });

        observableSelectedEdges.addListener((ListChangeListener<Edge>) c -> System.out.println("Edge Change Detected"));

        // Enable nodes to be dragged from the map to the list view in order to
        // connect nodes that are far apart or on separate floors
        lvConnectedNodes.setOnDragOver(event -> {
            if(event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }

            event.consume();
        });

        lvConnectedNodes.setOnDragEntered(event -> {
            if(event.getDragboard().hasString()) {
                lvConnectedNodes.setStyle("-fx-background-color: #4e9f42");
            }
            event.consume();
        });

        lvConnectedNodes.setOnDragExited(event -> {
            lvConnectedNodes.setStyle("-fx-background-color: IVORY");
            event.consume();
        });

        lvConnectedNodes.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                success = true;
            }

            // let the source know whether the string was successfully transferred and used
            event.setDropCompleted(success);

            //add connection between nodes
            addConnection(db.getString());

            event.consume();
        });

        initPopup();
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/MapBuilderView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(MouseEvent event) {
        //deselect node on one mouse click
        if(event.getClickCount() == 1) {
            observableSelectedNodes.clear();
        } else if(event.getClickCount() == 2) {
            observableSelectedNodes.clear();

            Node newNode = new Node("", (int)event.getX(), (int)event.getY(),
                                    getMapController().getCurrentFloor(), NodeBuilding.FRANCIS15,
                                    NodeType.HALL, "", "",
                                    "Team " + TeamAssigned.I.toString());

            observableNewNodes.clear();
            observableNewNodes.add(newNode);
        }
    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {
        if(observableNewNodes.contains(node)) {
            observableNewNodes.clear();
            return;
        } else if(observableSelectedNodes.contains(node)) {
            return;
        } else {
            updateSelectedNode(node);
        }
    }

    @Override
    public void onMapEdgeClicked(database.objects.Edge edge) {
        //remove changes on nodes
        //TODO make this into a method
        observableSelectedNodes.clear();
        observableNewNodes.clear();

        observableSelectedEdges.clear();
        observableSelectedEdges.add(edge);
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);
    }

    @Override
    public void resetScreen() {

        getMapController().setEditMode(true);

        getMapController().setAnchor(0, 450, 0, 0);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);

    }

    // Handles node Related operations

//TODO REFACTOR THIS USING "CHANGE"
    private void updateNodeDisplay(NodeSelectionType nodeSelectionType) {
        setEditingDisabled();
        switch (nodeSelectionType) {
            case SELECTED:
                if (observableSelectedNodes.size() == 1) {
                    for(Node targetNode : observableChangedNodes) {
                        if(observableSelectedNodes.get(0).getXyz().equals(targetNode.getXyz())) {
                            xcoord.setText(String.valueOf(targetNode.getXcoord()));
                            ycoord.setText(String.valueOf(targetNode.getYcoord()));

                            CBnodeBuilding.setValue(targetNode.getBuilding());
                            CBnodeType.setValue(targetNode.getNodeType());
                            CBnodeTeamAssigned.setValue(convertToTeamEnum(targetNode.getTeamAssigned()));

                            lName.setText(targetNode.getLongName());
                            sName.setText(targetNode.getShortName());

                            nodeID.setText(targetNode.getNodeID());

                            setEditingEnabled();
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
                } else {
                    //TODO make this an exception
                    System.out.println("THIS SHOULD NEVER HAPPEN!\n");
                }
                break;
            case CHANGED:
                Node targetNode = observableChangedNodes.get(0);
                xcoord.setText(String.valueOf(targetNode.getXcoord()));
                ycoord.setText(String.valueOf(targetNode.getYcoord()));

                CBnodeBuilding.setValue(targetNode.getBuilding());
                CBnodeType.setValue(targetNode.getNodeType());
                CBnodeTeamAssigned.setValue(convertToTeamEnum(targetNode.getTeamAssigned()));

                lName.setText(targetNode.getLongName());
                sName.setText(targetNode.getShortName());

                nodeID.setText(targetNode.getNodeID());

                setEditingEnabled();
                break;
            case NEW:
                if(observableNewNodes.size() == 1) {
                    setEditingEnabled();

                    lName.setText("");
                    sName.setText("");

                    xcoord.setText(String.valueOf(observableNewNodes.get(0).getXcoord()));
                    ycoord.setText(String.valueOf(observableNewNodes.get(0).getYcoord()));

                    CBnodeBuilding.setValue(observableNewNodes.get(0).getBuilding());
                    CBnodeType.setValue(observableNewNodes.get(0).getNodeType());
                    CBnodeTeamAssigned.setValue(convertToTeamEnum(observableNewNodes.get(0).getTeamAssigned()));

                    updateNodeID();
                } else {
                    //TODO make this an exception
                    System.out.println("THIS SHOULD NEVER HAPPEN!\n");
                }
                break;
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
                return "L2";
            case "LOWERLEVEL_1":
                return "L1";
            default:
                return "03";
        }
    }

//    public String convertToFloorEnumString(enumString) {
//        switch ()
//    }

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
        //System.out.println(observableSelectedNodes.get(0).getNodeID());
        if(nodeType == NodeType.ELEV) {
            String result = elevNameInChangedList();
            nodeID.setText(nodeTeamAssigned.name() + nodeType.toString() + "00" + result + convertFloor(getMapController().getCurrentFloor().toLiteralString()));
        } else {
        //System.out.println("");
            if(!MapEntity.getInstance().selectNodeID(Integer.parseInt(xcoord.getText()), Integer.parseInt(ycoord.getText()), nodeFloor, nodeType).equals("")){
                String nodeIDtemp = MapEntity.getInstance().selectNodeID(Integer.parseInt(xcoord.getText()), Integer.parseInt(ycoord.getText()), nodeFloor, nodeType);
                nodeID.setText(nodeIDtemp);
            }else{
                String nodeTypeCount = MapEntity.getInstance().getNodeTypeCount(nodeType, nodeFloor, nodeTeamAssigned, "");
                //nodeTypeCountPrepared += Integer.parseInt(nodeTypeCount) + countChangedList(nodeType);
                System.out.println(convertFloor(nodeFloor.toLiteralString()));
                nodeID.setText(nodeTeamAssigned.name() + nodeType.toString() + formatInt(Integer.parseInt(nodeTypeCount) + countChangedList(nodeType)) + convertFloor(nodeFloor.toLiteralString()));

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
        String preparedName = MapEntity.getInstance().generateElevName(nodeFloor, nodeTeamAssigned, result);
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

    private void setEditingEnabled() {
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
        setkiosklocation.setDisable(false);
    }

    private void setEditingDisabled() {
        CBnodeType.setDisable(true);
        CBnodeBuilding.setDisable(true);
        CBnodeTeamAssigned.setDisable(true);

        lName.setDisable(true);
        lName.setText("");

        sName.setDisable(true);
        sName.setText("");

        nodeID.setDisable(true);
        nodeID.setText("");

        xcoord.setDisable(true);
        xcoord.setText("");

        ycoord.setDisable(true);
        ycoord.setText("");

        //turn off advanced options
        btAdvance.setDisable(true);
        //disable node operation buttons
        setkiosklocation.setDisable(true);
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
        MapEntity map = MapEntity.getInstance();

        boolean isValid = true;
        if(lName.getText().trim().equals("")) {
            lName.validate();
            isValid = false;
        }
        if(sName.getText().trim().equals("")) {
            sName.validate();
            isValid = false;
        }

        if (!isValid) return;

        List<Node> nodesToAdd = new ArrayList<>(observableNewNodes);

        //clear new node list
        observableNewNodes.clear();

        nodeDialogString = "";
        for(Node newNode : nodesToAdd) {
            if (map.getNode(newNode.getNodeID()) == null) {
                try {
                    map.addNode(newNode);
                    getMapController().addNode(newNode, NodeSelectionType.NORMAL);
                    nodeDialogString += "node ID: " + newNode.getNodeID() +"\n" + " saved.\n\n";
                } catch (DatabaseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error adding node to DB");
                    alert.setHeaderText("Error occurred while adding node to database.");
                    alert.setContentText(ex.toString());
                    alert.showAndWait();

                    nodeDialogString += "ERROR: node " + newNode.getNodeID() + " was not added to database.\n\n";
                }
            }  else { //duplicate node ID found
                nodeDialogString += "node ID: " + newNode.getNodeID() + "\n" + "Duplicate ID found\n\n";
                System.out.println(nodeDialogString);
                loadDialog(event);
                nodeDialogString = "";
                return;
            }
        }

        for(database.objects.Node changedNode : observableChangedNodes) {
            try {
                map.editNode(changedNode);
                nodeDialogString += "node ID " + changedNode.getNodeID() + "\n" + " edited.\n\n";
            } catch (DatabaseException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error editing node in DB");
                alert.setHeaderText("Error occurred while updating a node in the database.");
                alert.setContentText(ex.toString());
                alert.showAndWait();

                nodeDialogString += "ERROR: node " + changedNode.getNodeID() + " was not edited to database.\n";
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
        nodeDialogLayout.setHeading(new Text(""));
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
            btAdvance.setStyle("-fx-background-color: #00589F");
            Advance.setVisible(false);
        }
        else {
            btAdvance.setStyle("-fx-background-color: #0090AD");
            Advance.setVisible(true);
        }
    }

    @FXML
    private void setKioskDefaultLocation(ActionEvent event){
        SystemSettings.getInstance().setDefaultnode(observableSelectedNodes.get(0).getNodeID());
    }

    @FXML
    private void onExpandPressed() {
        if(!lvConnectedNodes.isExpanded()) {
            lvConnectedNodes.setExpanded(true);
            lvConnectedNodes.depthProperty().set(1);
        }
        else {
            lvConnectedNodes.setExpanded(false);
            lvConnectedNodes.depthProperty().set(0);
        }
    }

    @FXML
    private void showPopup(MouseEvent event) {
        popup.show(lvConnectedNodes, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(), event.getY());
    }

    private void initPopup() {
        Image goIcon = new Image(getClass().getResource("/images/icons/go.png").toString());
        ImageView goIconView = new ImageView(goIcon);
        goIconView.setFitHeight(24);
        goIconView.setFitWidth(24);

        Image deleteIcon = new Image(getClass().getResource("/images/icons/delete.png").toString());
        ImageView deleteIconView = new ImageView(deleteIcon);
        deleteIconView.setFitHeight(24);
        deleteIconView.setFitWidth(24);

        Image backIcon = new Image(getClass().getResource("/images/icons/back.png").toString());
        ImageView backIconView = new ImageView(backIcon);
        backIconView.setFitHeight(24);
        backIconView.setFitWidth(24);

        JFXButton btGoToNode = new JFXButton("", goIconView);
        btGoToNode.setTooltip(new Tooltip("Go To Node"));
        btGoToNode.setOnAction(event -> {
            Node selectedNode = lvConnectedNodes.getSelectionModel().getSelectedItem();

            updateSelectedNode(selectedNode);
            popup.hide();
        });

        JFXButton btDeleteConnection = new JFXButton("", deleteIconView);
        btGoToNode.setTooltip(new Tooltip("Delete Connection"));
        btDeleteConnection.setOnAction(event -> {
            Node selectedNode = lvConnectedNodes.getSelectionModel().getSelectedItem();

            deleteConnection(event, selectedNode);
            popup.hide();
        });

        JFXButton btBack = new JFXButton("", backIconView);
        btGoToNode.setTooltip(new Tooltip("Back"));
        btBack.setOnAction(event -> popup.hide());

        btGoToNode.setStyle("-fx-background-color: #000000;" + "-fx-backgound-raidus: 0");
        btDeleteConnection.setStyle("-fx-background-color: #d32a04;" + "-fx-backgound-raidus: 0");
        btBack.setStyle("-fx-background-color: #999999;" + "-fx-backgound-raidus: 0");

        VBox vBox = new VBox(btGoToNode, btDeleteConnection, btBack);
        popup = new JFXPopup(vBox);
    }

    private void updateSelectedNode(database.objects.Node selectedNode) {
        //remove unsaved new node, if any
        observableNewNodes.clear();

        observableSelectedNodes.clear();
        observableSelectedNodes.add(selectedNode);
    }

    @FXML
    private void onDeleteClicked(ActionEvent e) {
        if(!observableNewNodes.isEmpty()) {
            observableNewNodes.clear();
        } else if(!observableSelectedNodes.isEmpty()) {
            //remove this node from map builder changed list
            Iterator<database.objects.Node> builderNodeObjectIterator = observableChangedNodes.iterator();
            while (builderNodeObjectIterator.hasNext()) {
                database.objects.Node deletedNode = builderNodeObjectIterator.next();
                if (deletedNode.getXyz().equals(observableChangedNodes.get(0).getXyz())) {
                    builderNodeObjectIterator.remove();
                    break;
                }
            }

            //remove this node from map controller drawn list
            getMapController().removeNode(observableSelectedNodes.get(0));

            for(database.objects.Node deletedNode : MapEntity.getInstance().getAllNodes()) {
                if(deletedNode.getXyz().equals(observableSelectedNodes.get(0).getXyz())) {
                    try{
                        MapEntity.getInstance().removeNode(observableSelectedNodes.get(0));
                    }catch (DatabaseException databaseException) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error deleting Node");
                        alert.setHeaderText("Error occurred while updating a node in the database.");
                        alert.setContentText(databaseException.toString());
                        alert.showAndWait();
                    }
                }
            }

            observableSelectedNodes.clear();
        }
    }

    @FXML
    private void deleteConnection(ActionEvent e, database.objects.Node targetNode) {
        database.objects.Edge deletingEdge = MapEntity.getInstance().getConnectingEdge(targetNode, observableSelectedNodes.get(0));
        try{
            MapEntity.getInstance().removeEdge(deletingEdge);
        }catch (DatabaseException databaseException) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error deleting Connection");
            alert.setHeaderText("Error occurred while updating an edge in the database.");
            alert.setContentText(databaseException.toString());
            alert.showAndWait();
        }

        //refresh edges display
        // TODO do this better
        getMapController().setEdgesVisible(false);
        getMapController().setEdgesVisible(true);

        updateSelectedNode(observableSelectedNodes.get(0));
    }

    @Override
    public void addConnectionByNodes(String nodeXyz1, String nodeXyz2) {
        MapEntity map = MapEntity.getInstance();
        if(!observableNewNodes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error creating connection ");
            alert.setHeaderText("Save new node before creating connections");
            alert.setContentText("Press save button to save any new node before connecting it with other nodes.");
            alert.showAndWait();
            return;
        }
        for (database.objects.Node connectingNode1 : map.getAllNodes()) {
            if (nodeXyz1.equals(connectingNode1.getXyz())) {
                for (database.objects.Node connectingNode2 : map.getAllNodes()) {
                    if (nodeXyz2.equals(connectingNode2.getXyz())) {
                        try{
                            if (MapEntity.getInstance().getEdge(connectingNode1.getNodeID() + "_" + connectingNode2.getNodeID()) != null ||
                                    MapEntity.getInstance().getEdge(connectingNode2.getNodeID() + "_" + connectingNode1.getNodeID()) != null) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error creating connection ");
                                alert.setHeaderText("Connection already exit");
                                alert.setContentText("Connection between these two nodes already exist");
                                alert.showAndWait();
                                return;
                            }
                        }catch(DatabaseException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error creating connection ");
                            alert.setHeaderText("Error occurred while looking for nodes in the database.");
                            alert.setContentText(ex.toString());
                            alert.showAndWait();
                        }
                        //add the new edge
                        Edge edge = new Edge((connectingNode1.getNodeID()+"_"+connectingNode2.getNodeID()), connectingNode1.getNodeID(), connectingNode2.getNodeID());
                        try{
                            map.addEdge(edge);
                        }catch (DatabaseException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error creating connection ");
                            alert.setHeaderText("Error occurred while putting edge in the database.");
                            alert.setContentText(ex.toString());
                            alert.showAndWait();
                        }
                        //refresh edges display
                        getMapController().setEdgesVisible(false);
                        getMapController().setEdgesVisible(true);
                    }
                }
            }
        }
    }

    @FXML
    private void addConnection(String nodeXyz) {
        MapEntity map = MapEntity.getInstance();

        if(!observableNewNodes.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error creating connection");
            alert.setHeaderText("Save new node before creating connections");
            alert.setContentText("Press save button to save any new node before connecting them with other nodes.");
            alert.showAndWait();
            return;
        } else if(!observableSelectedNodes.isEmpty()) {
            for(database.objects.Node connectingNode : map.getAllNodes()) {
                if(nodeXyz.equals(connectingNode.getXyz())) {
                    try{
                        if (MapEntity.getInstance().getEdge(connectingNode.getNodeID() + "_" + observableSelectedNodes.get(0).getNodeID()) != null ||
                                MapEntity.getInstance().getEdge(observableSelectedNodes.get(0).getNodeID() + "_" + connectingNode.getNodeID()) != null) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error creating connection");
                            alert.setHeaderText("Connection already exit");
                            alert.setContentText("Connection between these two nodes already exist");
                            alert.showAndWait();
                            return;
                        }
                    }catch(DatabaseException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error creating connection");
                        alert.setHeaderText("Error occurred while looking for nodes in the database.");
                        alert.setContentText(ex.toString());
                        alert.showAndWait();
                    }
                    //add the new edge
                    Edge edge = new Edge((connectingNode.getNodeID()+ "_" +observableSelectedNodes.get(0).getNodeID()), connectingNode.getNodeID(), observableSelectedNodes.get(0).getNodeID());
                    try{
                        map.addEdge(edge);
                    }catch (DatabaseException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error creating connection");
                        alert.setHeaderText("Error occurred while putting edge in the database.");
                        alert.setContentText(ex.toString());
                        alert.showAndWait();
                    }
                    //refresh edges display
                    getMapController().setEdgesVisible(false);
                    getMapController().setEdgesVisible(true);
                    updateSelectedNode(observableSelectedNodes.get(0));
                }
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Creating connection");
            alert.setHeaderText("Create connection between nodes");
            alert.setContentText("Select a node from the map before creating connections");
            alert.showAndWait();
        }
    }
    @Override
    public boolean isNewNodeEmpty() {
        return observableNewNodes.isEmpty();
    }

    @Override
    public void showFloors() {
        JFXDialogLayout floorDialoglo = new JFXDialogLayout();
        floorDialoglo.setHeading(new Text("Select a floor"));
        JFXDialog Dialog = new JFXDialog(mapBuilderStackPane, floorDialoglo, JFXDialog.DialogTransition.CENTER);
        floorDialoglo.setActions(Dialog);

        Dialog.show();
    }
}