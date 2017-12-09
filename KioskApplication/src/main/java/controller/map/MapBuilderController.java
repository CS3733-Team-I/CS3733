package controller.map;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import controller.MainWindowController;
import controller.ScreenController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import utility.ResourceManager;
import utility.node.*;

import java.io.IOException;
import java.util.Iterator;

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
        selectedNode.set(null);

        newNode = new SimpleObjectProperty<>();
        newNode.set(null);

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

            checkField(CBnodeType);
        });


        CBnodeBuilding.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodeBuilding = newValue;
            if(!CBnodeBuilding.isDisable()) {
                // If we're adding a new node, newNode will not be null
                if(newNode.get() != null) {
                    newNode.get().setBuilding(CBnodeBuilding.getValue());
                    updateNodeID();
                    return;
                }

                // Otherwise look for the changed node and update the building
                if(observableChangedNodes.contains(selectedNode.get())) {
                    for(Node changingNode : observableChangedNodes) {
                        if(changingNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                            changingNode.setBuilding(CBnodeBuilding.getValue());
                            updateNodeID();
                        }
                    }
                } else {
                    // And if we don't find the changed node, change the ID and add it to the list of changed
                    selectedNode.get().setBuilding(CBnodeBuilding.getValue());
                    updateNodeID();

                    if(!observableChangedNodes.contains(selectedNode.get())){
                        observableChangedNodes.add(selectedNode.get());
                    }

                }
            }
        });

        CBnodeTeamAssigned.valueProperty().addListener((observable, oldValue, newValue) -> {
            nodeTeamAssigned = newValue;
            if(!CBnodeTeamAssigned.isDisable()) {
                if(newNode.get() != null) {
                    newNode.get().setTeamAssigned("Team " + CBnodeTeamAssigned.getValue().toString());
                    updateNodeID();
                    return;
                }

                if (observableChangedNodes.contains(selectedNode.get())) {
                    for(Node changingNode : observableChangedNodes) {
                        if(changingNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                            changingNode.setTeamAssigned("Team " + CBnodeTeamAssigned.getValue().toString());
                            updateNodeID();
                        }
                    }
                } else {
                    selectedNode.get().setTeamAssigned("Team " + CBnodeTeamAssigned.getValue().toString());
                    updateNodeID();
                    if(!observableChangedNodes.contains(selectedNode.get())){
                        observableChangedNodes.add(selectedNode.get());
                    }

                }
            }
        });

        //notify changed node list
        lName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!lName.isDisable()) {
                lName.resetValidation();

                if(newNode.get() != null) {
                    newNode.get().setLongName(lName.getText());
                    return;
                }

                if(observableChangedNodes.contains(selectedNode.get())) {
                    for(Node changingNode : observableChangedNodes) {
                        if(changingNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                            changingNode.setLongName(lName.getText());
                        }
                    }
                } else {
                    selectedNode.get().setLongName(lName.getText());
                    observableChangedNodes.add(selectedNode.get());
                }
            }
        });

        sName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!sName.isDisable()) {
                sName.resetValidation();

                if(newNode.get() != null) {
                    newNode.get().setShortName(sName.getText());
                    return;
                }

                if(observableChangedNodes.contains(selectedNode.get())) {
                    for(Node changingNode : observableChangedNodes) {
                        if(changingNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                            changingNode.setShortName(sName.getText());
                        }
                    }
                }
                else {
                    selectedNode.get().setShortName(sName.getText());
                    observableChangedNodes.add(selectedNode.get());
                }
            }
        });

        nodeID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.equals(newValue)) return;

            if (!nodeID.isDisabled()) {
                if (newNode.get() != null) {
                    newNode.get().setNodeID(nodeID.getText());
                    return;
                }

                if(observableChangedNodes.contains(selectedNode.get())) {
                    for(Node changingNode : observableChangedNodes) {
                        if(changingNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                            changingNode.setNodeID(nodeID.getText());
                        }
                    }
                } else {
                    selectedNode.get().setNodeID(nodeID.getText());
                    observableChangedNodes.add(selectedNode.get());
                }

                //reflect node ID changes in mapEntity also in edges
                for(Node node : MapEntity.getInstance().getAllNodes()) {
                    if(node.getUniqueID() == selectedNode.get().getUniqueID()) {
                        for(Edge edge : MapEntity.getInstance().getEdges(new Node(oldValue))) {
                            System.out.println("3. Begin Updating edge ID");

                            if(edge.getNode1ID().equals(oldValue)) {
                                edge.setNode1ID(nodeID.getText());
                            }
                            if(edge.getNode2ID().equals(oldValue)) {
                                edge.setNode2ID(nodeID.getText());
                            }

                            // TODO make this update the database, currently doesnt

                            System.out.println("EdgeID:" + edge.getEdgeID() + " now Connecting: " + edge.getNode1ID() + " : " + edge.getNode2ID());
                        }

                        node.setNodeID(nodeID.getText());
                    }
                }
            }
        });

        //keep track on selected, new, and changed nodes and edges list
        selectedNode.addListener((ChangeListener<Node>) (ObservableValue<? extends Node> o, Node oldVal, Node newVal) -> {
            if (oldVal != null) {
                if (observableChangedNodes.contains(oldVal))
                    getMapController().setNodeSelected(oldVal, NodeSelectionType.CHANGED);
                else
                    getMapController().setNodeSelected(oldVal, NodeSelectionType.NORMAL);
            }

            lvConnectedNodes.getItems().clear();
            if (newVal != null) {
                if (observableChangedNodes.contains(newVal))
                    getMapController().setNodeSelected(newVal, NodeSelectionType.SELECTEDANDCHANGED);
                else
                    getMapController().setNodeSelected(newVal, NodeSelectionType.SELECTED);

                for(Node connectedNode : MapEntity.getInstance().getConnectedNodes(newVal)) {
                    lvConnectedNodes.getItems().add(connectedNode);
                }

                updateNodeDisplay(NodeSelectionType.SELECTED);
            }

            lvConnectedNodes.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {

                @Override
                public ListCell<Node> call(ListView<Node> param) {
                    ListCell<Node> cell = new ListCell<Node>() {

                        @Override
                        protected void updateItem(Node item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                setText(item.getLongName());
                            } else {
                                setText("");
                            }
                        }
                    };
                    return cell;
                }
            });

            if(newVal == null && newNode.get() == null) {
                setEditingDisabled();
            } else {
                setEditingEnabled();
            }
        });

        observableChangedNodes.addListener((ListChangeListener<Node>) c -> {
            while(c.next()) {
                if(c.wasAdded()) {
                    for(Node node : c.getAddedSubList()) {
                        if (node.equals(selectedNode.get()))
                            getMapController().setNodeSelected(node, NodeSelectionType.SELECTEDANDCHANGED);
                        else
                            getMapController().setNodeSelected(node, NodeSelectionType.CHANGED);
                    }
                }

                if(c.wasRemoved()) {
                    for(Node node : c.getRemoved()) {
                        getMapController().setNodeSelected(node, NodeSelectionType.NORMAL);
                    }
                }
            }

            if(observableChangedNodes.isEmpty() && newNode.get() == null){
                btNodeSave.setDisable(true);
            } else {
                btNodeSave.setDisable(false);
            }
        });

        newNode.addListener((ChangeListener<Node>) (o, oldVal, newVal) -> {
            if (newVal != null) {
                getMapController().addNode(newVal, NodeSelectionType.NEW);
                updateNodeDisplay(NodeSelectionType.NEW);
            }

            if(observableChangedNodes.isEmpty() && newVal == null){
                btNodeSave.setDisable(true);
            } else {
                btNodeSave.setDisable(false);
            }

            if(selectedNode.get() == null && newVal == null) {
                setEditingDisabled();
            }
        });

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
            addConnection(Integer.parseInt(db.getString()));

            event.consume();
        });

        initPopup();
    }


    public void checkField(JFXComboBox comboBox){
        if (!comboBox.isDisable()) {
            // If we're adding a new node, newNode will not be null
            if (newNode.get() != null) {
                newNode.get().setNodeType((NodeType)comboBox.getValue());
                updateNodeID();

                return;
            }

            // Otherwise look for the changed node and update the type
            if (observableChangedNodes.contains(selectedNode.get())) {
                System.out.println("update changes in changed node");
                for (Node changingNode : observableChangedNodes) {
                    if(changingNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                        changingNode.setNodeType((NodeType)comboBox.getValue());
                        updateNodeID();
                    }
                }
            } else {
                // And if we don't find the changed node, change the type and add it to the list of changed
                selectedNode.get().setNodeType((NodeType)comboBox.getValue());
                updateNodeID();

                if (!observableChangedNodes.contains(selectedNode.get())){
                    observableChangedNodes.add(selectedNode.get());
                }
            }
        }
    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    void resetTimer(){
        getParent().resetTimer();
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
            selectedNode.set(null);
        } else if(event.getClickCount() == 2) {
            selectedNode.set(null);

            Node createdNode = new Node("", (int)event.getX(), (int)event.getY(),
                    getMapController().getCurrentFloor(), NodeBuilding.FRANCIS15,
                    NodeType.HALL, "", "",
                    "Team " + TeamAssigned.I.toString());
            if(newNode.get() != null) {
                getMapController().removeNode(newNode.get());
            }
            newNode.set(createdNode);
        }
    }

    @Override
    public void onMapNodeClicked(Node node) {
        if (newNode.get() != null && newNode.get().equals(node)) {
            // Stop displaying newNode
            getMapController().removeNode(newNode.get());
            newNode.set(null);
            return;
        } else if(selectedNode.get() != null && selectedNode.get().equals(node)) {
            return;
        } else {
            updateSelectedNode(node);
        }
    }

    @Override
    public void onMapEdgeClicked(database.objects.Edge edge) {
        //remove changes on nodes
        selectedNode.set(null);
        newNode.set(null);
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {

    }

    @Override
    public void resetScreen() {
        getMapController().setEditMode(true);

        getMapController().setAnchor(0, 450, 0, 0);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);

        setEditingDisabled();

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(true);

    }

    // Handles node Related operations

    //TODO REFACTOR THIS USING "CHANGE"
    private void updateNodeDisplay(NodeSelectionType nodeSelectionType) {
        setEditingDisabled();
        switch (nodeSelectionType) {
            case SELECTED:
                // If the selected node has already been changed set the values to the changed values
                for(Node targetNode : observableChangedNodes) {
                    if(selectedNode.get().getUniqueID() == targetNode.getUniqueID()) {
                        xcoord.setText(String.valueOf(targetNode.getXcoord()));
                        ycoord.setText(String.valueOf(targetNode.getYcoord()));

                        CBnodeBuilding.setValue(targetNode.getBuilding());
                        CBnodeType.setValue(targetNode.getNodeType());
                        CBnodeTeamAssigned.setValue(convertToTeamEnum(targetNode.getTeamAssigned()));

                        lName.setText(targetNode.getLongName());
                        sName.setText(targetNode.getShortName());

                        nodeID.setText(targetNode.getNodeID());
                        return;
                    }
                }

                // Otherwise set the text fields to the selected node
                xcoord.setText(String.valueOf(selectedNode.get().getXcoord()));
                ycoord.setText(String.valueOf(selectedNode.get().getYcoord()));

                CBnodeBuilding.setValue(selectedNode.get().getBuilding());
                CBnodeType.setValue(selectedNode.get().getNodeType());
                lName.setText(selectedNode.get().getLongName());
                sName.setText(selectedNode.get().getShortName());
                CBnodeTeamAssigned.setValue(convertToTeamEnum(selectedNode.get().getTeamAssigned()));
                nodeID.setText(selectedNode.get().getNodeID());
                setEditingEnabled();
                break;
            case NEW:
                lName.setText("");
                sName.setText("");

                xcoord.setText(String.valueOf(newNode.get().getXcoord()));
                ycoord.setText(String.valueOf(newNode.get().getYcoord()));

                CBnodeBuilding.setValue(newNode.get().getBuilding());
                CBnodeType.setValue(newNode.get().getNodeType());
                CBnodeTeamAssigned.setValue(convertToTeamEnum(newNode.get().getTeamAssigned()));

                setEditingEnabled();
                updateNodeID();
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
        //System.out.println(selectedNode.get().getNodeID());
        if(nodeType == NodeType.ELEV) {
            String result = elevNameInChangedList();
            nodeID.setText(nodeTeamAssigned.name() + nodeType.toString() + "00" + result + convertFloor(getMapController().getCurrentFloor().toLiteralString()));
        } else {
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

        // Check the text boxes if selected
        if (selectedNode.get() != null) {
            boolean isValid = true;
            if (lName.getText().trim().equals("")) {
                lName.validate();
                isValid = false;
            }
            if (sName.getText().trim().equals("")) {
                sName.validate();
                isValid = false;
            }

            if (!isValid) return;
        }

        Node nodeToAdd = newNode.get();

        //clear new node
        newNode.set(null);

        nodeDialogString = "";
        if (nodeToAdd != null) {
            try {
                if (map.getNode(nodeToAdd.getNodeID()) != null) {
                    nodeDialogString += "node ID: " + nodeToAdd.getNodeID() + "\n" + "Duplicate ID found\n\n";
                    System.out.println(nodeDialogString);
                    loadDialog(event);
                    nodeDialogString = "";
                    return;
                }
            } catch (NotFoundException e) {
                try {
                    map.addNode(nodeToAdd);
                    getMapController().addNode(nodeToAdd, NodeSelectionType.NORMAL);
                    nodeDialogString += "node ID: " + nodeToAdd.getNodeID() + "\n" + " saved.\n\n";
                } catch (DatabaseException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error adding node to DB");
                    alert.setHeaderText("Error occurred while adding node to database.");
                    alert.setContentText(ex.toString());
                    alert.showAndWait();

                    nodeDialogString += "ERROR: node " + nodeToAdd.getNodeID() + " was not added to database.\n\n";
                }
            }
        }

        for (Node changedNode : observableChangedNodes) {
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
        SystemSettings.getInstance().setDefaultnode(selectedNode.get().getNodeID());
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
        btGoToNode.setOnMouseMoved(e -> resetTimer());
        btGoToNode.setOnMousePressed(e -> resetTimer());
        btGoToNode.setOnAction(event -> {
            Node selectedNode = lvConnectedNodes.getSelectionModel().getSelectedItem();

            updateSelectedNode(selectedNode);
            popup.hide();
        });

        JFXButton btDeleteConnection = new JFXButton("", deleteIconView);
        btDeleteConnection.setTooltip(new Tooltip("Delete Connection"));
        btDeleteConnection.setOnMouseMoved(e -> resetTimer());
        btDeleteConnection.setOnMousePressed(e -> resetTimer());
        btDeleteConnection.setOnAction(event -> {
            Node selectedNode = lvConnectedNodes.getSelectionModel().getSelectedItem();

            deleteConnection(event, selectedNode);

            lvConnectedNodes.getItems().clear();
            for(Node connectedNode : MapEntity.getInstance().getConnectedNodes(this.selectedNode.get())) {
                lvConnectedNodes.getItems().add(connectedNode);
            }


            Node savedNode = this.selectedNode.get();
            updateSelectedNode(null);
            updateSelectedNode(savedNode);

            popup.hide();
        });

        JFXButton btBack = new JFXButton("", backIconView);
        btBack.setTooltip(new Tooltip("Back"));
        btBack.setOnMouseMoved(e -> resetTimer());
        btBack.setOnMousePressed(e -> resetTimer());
        btBack.setOnAction(event -> popup.hide());

        btGoToNode.setStyle("-fx-background-color: #000000;" + "-fx-backgound-raidus: 0");
        btDeleteConnection.setStyle("-fx-background-color: #d32a04;" + "-fx-backgound-raidus: 0");
        btBack.setStyle("-fx-background-color: #999999;" + "-fx-backgound-raidus: 0");

        VBox vBox = new VBox(btGoToNode, btDeleteConnection, btBack);
        popup = new JFXPopup(vBox);
    }

    private void updateSelectedNode(Node node) {
        //remove unsaved new node, if any
        if (newNode.get() != null) {
            getMapController().removeNode(newNode.get());
            newNode.set(null);
        }
        selectedNode.set(node);
    }

    @FXML
    private void onDeleteClicked(ActionEvent e) {
        if(newNode.get() != null) {
            newNode.set(null);
        } else if(selectedNode.get() != null) {
            //remove this node from map builder changed list
            Iterator<database.objects.Node> builderNodeObjectIterator = observableChangedNodes.iterator();
            while (builderNodeObjectIterator.hasNext()) {
                database.objects.Node deletedNode = builderNodeObjectIterator.next();
                if (deletedNode.getUniqueID() == observableChangedNodes.get(0).getUniqueID()) {
                    builderNodeObjectIterator.remove();
                    break;
                }
            }

            //remove this node from map controller drawn list
            getMapController().removeNode(selectedNode.get());

            for(database.objects.Node deletedNode : MapEntity.getInstance().getAllNodes()) {
                if(deletedNode.getUniqueID() == selectedNode.get().getUniqueID()) {
                    try{
                        MapEntity.getInstance().removeNode(selectedNode.get());
                    }catch (DatabaseException databaseException) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error deleting Node");
                        alert.setHeaderText("Error occurred while updating a node in the database.");
                        alert.setContentText(databaseException.toString());
                        alert.showAndWait();
                    }
                }
            }

            selectedNode.set(null);
            getMapController().reloadDisplay();
        }
    }

    @FXML
    private void deleteConnection(ActionEvent e, database.objects.Node targetNode) {
        database.objects.Edge deletingEdge = MapEntity.getInstance().getConnectingEdge(targetNode, selectedNode.get());
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
        getMapController().reloadDisplay();
    }

    public void addConnectionByNodes(int uniqueNodeID1, int uniqueNodeID2) {
        MapEntity map = MapEntity.getInstance();
        if(newNode.get() != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error creating connection ");
            alert.setHeaderText("Save new node before creating connections");
            alert.setContentText("Press save button to save any new node before connecting it with other nodes.");
            alert.showAndWait();
            return;
        }

        for (database.objects.Node connectingNode1 : map.getAllNodes()) {
            if (uniqueNodeID1 == connectingNode1.getUniqueID()) {
                for (database.objects.Node connectingNode2 : map.getAllNodes()) {
                    if (uniqueNodeID2 == connectingNode2.getUniqueID()) {
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

                        getMapController().reloadDisplay();
                        updateSelectedNode(selectedNode.get());
                    }
                }
            }
        }
    }

    @FXML
    private void addConnection(int uniqueNodeID) {
        MapEntity map = MapEntity.getInstance();

        if(newNode.get() != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error creating connection");
            alert.setHeaderText("Save new node before creating connections");
            alert.setContentText("Press save button to save any new node before connecting them with other nodes.");
            alert.showAndWait();
            return;
        } else if(selectedNode.get() != null) {
            for(database.objects.Node connectingNode : map.getAllNodes()) {
                if(uniqueNodeID == connectingNode.getUniqueID()) {
                    try{
                        if (MapEntity.getInstance().getEdge(connectingNode.getNodeID() + "_" + selectedNode.get().getNodeID()) != null ||
                                MapEntity.getInstance().getEdge(selectedNode.get().getNodeID() + "_" + connectingNode.getNodeID()) != null) {
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
                    Edge edge = new Edge((connectingNode.getNodeID()+ "_" +selectedNode.get().getNodeID()), connectingNode.getNodeID(), selectedNode.get().getNodeID());
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
                    getMapController().reloadDisplay();
                    updateSelectedNode(selectedNode.get());
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

    public boolean isNewNodeEmpty() {
        return newNode.get() != null;
    }

    public void showFloors() {
        JFXDialogLayout floorDialoglo = new JFXDialogLayout();
        floorDialoglo.setHeading(new Text("Select a floor"));
        JFXDialog Dialog = new JFXDialog(mapBuilderStackPane, floorDialoglo, JFXDialog.DialogTransition.CENTER);
        floorDialoglo.setActions(Dialog);

        Dialog.show();
    }
}