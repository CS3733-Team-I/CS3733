package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Employee;
import database.objects.Node;
import entity.LoginEntity;
import entity.MapEntity;
import entity.Path;
import entity.SearchEntity.ISearchEntity;
import entity.SearchEntity.SearchEmployee;
import entity.SearchEntity.SearchNode;
import entity.SystemSettings;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.NoSelectionModel;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.request.RequestType;

import java.io.IOException;
import java.util.*;

public class PathfindingSidebarController extends ScreenController {

    // Navigate Screen
    @FXML private AnchorPane waypointsContainer;
    @FXML private JFXListView<HBox> waypointListView;

    @FXML private ImageView addIconView;
    @FXML private ImageView removeIconView;
    @FXML private JFXButton showDirectionsButton;
    @FXML private  JFXButton btClearPath;

    @FXML private JFXButton btExit;
    @FXML private JFXButton btRestRoom;
    @FXML private JFXButton btRestaurant;
    @FXML private JFXButton btElevator;
    private Boolean isAddingWaypoint;

    private ObservableList<Node> currentWaypoints;
    private HashMap<Node, HBox> waypointViews;

    private SystemSettings systemSettings;

    private SearchController searchController;

    private javafx.scene.Node searchView;

    private Task<Void> searchResetTask;

    // Direction Screen
    @FXML private AnchorPane directionsContainer;
    @FXML private Label directionsLabel;
    @FXML private VBox textDirectionsBox;
    @FXML private JFXButton emailButton;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentWaypoints = FXCollections.observableArrayList();
        waypointViews = new HashMap<>();
        systemSettings = SystemSettings.getInstance();
        isAddingWaypoint = true;
    }

    @FXML
    void initialize() throws IOException{
        ArrayList<ISearchEntity> searchNodeAndDoctor = new ArrayList<>();
        for(Node targetNode : MapEntity.getInstance().getAllNodes()) {
            if(targetNode.getNodeType() != NodeType.HALL) {
                searchNodeAndDoctor.add(new SearchNode(targetNode));
            }
        }
        for(Employee targetEmployee : LoginEntity.getInstance().getAllLogins()) {
            if(targetEmployee.getServiceAbility() == RequestType.DOCTOR) {
                searchNodeAndDoctor.add(new SearchEmployee(targetEmployee));
            }
        }
        searchController = new SearchController(this, searchNodeAndDoctor);

        //initialize search
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/view/searchView.fxml"));
        searchLoader.setController(searchController);
        searchView = searchLoader.load();

        directionsContainer.setVisible(false);

        emailButton.setGraphic(new ImageView(ResourceManager.getInstance().getImage("/images/icons/mail.png")));

        // Set containers to be transparent to mouse events
        ResourceBundle lang = systemSettings.getResourceBundle();
        getMapController().setFloorSelector(NodeFloor.THIRD);

        waypointListView.setPickOnBounds(false);
        waypointListView.setSelectionModel(new NoSelectionModel<>());
        waypointListView.setFixedCellSize(50);
        waypointListView.prefHeightProperty().bind(Bindings.size(waypointListView.getItems()).multiply(waypointListView.getFixedCellSize()).add(15));

        showDirectionsButton.setVisible(false);

        /**
         * load images for nearest exit, elevatior, food and restroom
         */
        Image foodIcon = ResourceManager.getInstance().getImage("/images/icons/food.png");
        ImageView foodIconView = new ImageView(foodIcon);
        foodIconView.setFitHeight(48);
        foodIconView.setFitWidth(48);
        btRestaurant.setGraphic(foodIconView);

        Image exitIcon = ResourceManager.getInstance().getImage("/images/icons/exit.png");
        ImageView exitView = new ImageView(exitIcon);
        exitView.setFitHeight(48);
        exitView.setFitWidth(48);
        btExit.setGraphic(exitView);

        Image elevIcon = ResourceManager.getInstance().getImage("/images/icons/elevator.png");
        ImageView elevIconView = new ImageView(elevIcon);
        elevIconView.setFitHeight(48);
        elevIconView.setFitWidth(48);
        btElevator.setGraphic(elevIconView);

        Image restroomIcon = ResourceManager.getInstance().getImage("/images/icons/restroom.png");
        ImageView restroomIconView = new ImageView(restroomIcon);
        restroomIconView.setFitHeight(48);
        restroomIconView.setFitWidth(48);
        btRestRoom.setGraphic(restroomIconView);

        insertAddNewWaypointCell();

        // TODO redo localization for this screen
        systemSettings.addObserver((o, arg) -> {
            btClearPath.setText(SystemSettings.getInstance().getResourceBundle().getString("clearpath"));
        });

        searchController.setSearchFieldPromptText(lang.getString("my.searchprompt"));
        searchView.setOnMouseMoved(e -> resetTimer());
        searchView.setOnMousePressed(e -> resetTimer());

        systemSettings.addObserver((o, arg) ->
                searchController.setSearchFieldPromptText(
                    SystemSettings.getInstance().getResourceBundle().getString("my.searchprompt")
                )
        );

        searchController.getCBValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(((Node) newValue.getLocation()).getFloor() != getMapController().getCurrentFloor()) {
                    getMapController().setFloorSelector(((Node)newValue.getLocation()).getFloor());
                }
                LinkedList<Node> displayedNode = new LinkedList<>();
                displayedNode.add((newValue.getLocation()));
                getMapController().zoomOnSelectedNodes(displayedNode);
                onMapNodeClicked((newValue.getLocation()));
            }
        });

        currentWaypoints.addListener((ListChangeListener<Node>) listener -> {
            while (listener.next()) {
                if (listener.wasAdded()) {
                    for (Node node : listener.getAddedSubList()) {
                        getMapController().addWaypoint(node);
                        insertWaypointCell(node);
                    }
                } else if (listener.wasRemoved()) {
                    for (Node node : listener.getRemoved()) {
                        getMapController().removeWaypoint(node);

                        waypointListView.getItems().remove(waypointViews.get(node));
                        waypointViews.remove(node);
                    }
                }

                generatePath();
            }
        });

        //reset search
        this.searchResetTask = new Task<Void>() {
            @Override protected Void call() throws Exception {
                SystemSettings.getInstance().updateDistance();
                ArrayList<ISearchEntity> searchNodeAndDoctor = new ArrayList<>();
                for(Node targetNode : MapEntity.getInstance().getAllNodes()) {
                    if(targetNode.getNodeType() != NodeType.HALL) {
                        searchNodeAndDoctor.add(new SearchNode(targetNode));
                    }
                }
                for(Employee targetEmployee : LoginEntity.getInstance().getAllLogins()) {
                    if(targetEmployee.getServiceAbility() == RequestType.DOCTOR) {
                        searchNodeAndDoctor.add(new SearchEmployee(targetEmployee));
                    }
                }
                searchController.reset(searchNodeAndDoctor);
                return null;
            }
        };
    }

    @FXML
    void showPathButton() {
        if (currentWaypoints.size() == 0) return;

        getMapController().setFloorSelector(currentWaypoints.get(0).getFloor());

        LinkedList<Node> waypointsOnFloor = new LinkedList<>();
        for (Node node : currentWaypoints)
            if (node.getFloor().equals(getMapController().getCurrentFloor())) waypointsOnFloor.add(node);

        getMapController().zoomOnSelectedNodes(waypointsOnFloor);
    }

    @FXML
    void onResetPressed() {
        MapController mapController = getMapController();
        mapController.setPath(null);
        mapController.clearMap();
        mapController.hideTray();
        mapController.clearTray();  //remove path previews
        mapController.reloadDisplay();

        //reset search
        ArrayList<ISearchEntity> searchNodeAndDoctor = new ArrayList<>();
        for(Node targetNode : MapEntity.getInstance().getAllNodes()) {
            if(targetNode.getNodeType() != NodeType.HALL) {
                searchNodeAndDoctor.add(new SearchNode(targetNode));
            }
        }
        for(Employee targetEmployee : LoginEntity.getInstance().getAllLogins()) {
            if(targetEmployee.getServiceAbility() == RequestType.DOCTOR) {
                searchNodeAndDoctor.add(new SearchEmployee(targetEmployee));
            }
        }

        searchController.reset(searchNodeAndDoctor);

        showDirectionsButton.setVisible(false);

        currentWaypoints.clear();
        currentWaypoints.add(SystemSettings.getInstance().getKioskLocation());
    }

    public void disableClearBtn(){
        btClearPath.setDisable(true);
    }

    void generatePath() {
        MapController map = getMapController();
        if (map.isPathShowing()) {
            map.clearNodes();
            map.clearPath();
        }

        if (currentWaypoints.size() >= 2) {
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());

            try{
                Path path = pathfinder.generatePath(new LinkedList<>(currentWaypoints));

                map.setPath(path);
                map.showTray();
                LinkedList<LinkedList<String>> directionsList = getMapController().getPath().getDirectionsList();
            } catch(PathfinderException exception){
                exception.printStackTrace();
                //exceptionText.setText("ERROR! "+ exception.getMessage());
            }
            showDirectionsButton.setVisible(true);
        }

        //addTextDirection();
    }

    /* TEXT DIRECTIONS */

    @FXML
    private void onEmailPressed() {

    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    public void resetTimer(){
        getParent().resetTimer();
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/PathfindingSidebarView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e) {
        if(e.getClickCount() == 2) {
            getMapController().zoomInPressed();
        }
        else {
            //TODO what if location clicked have no node
        }
    }

    @Override
    public void onMapNodeClicked(Node node) {
        if (isAddingWaypoint) {
            if (!currentWaypoints.contains(node)) {
                currentWaypoints.add(node);
                isAddingWaypoint = false;
            }
        } else {
            //remove last node
            removeWaypoint(currentWaypoints.get(currentWaypoints.size() - 1));

            //add new waypoint
            currentWaypoints.add(node);
        }
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {

    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {
    }

    @Override
    public void onScreenChanged() {

    }

    @Override
    public void resetScreen() {
        getMapController().setEditMode(false);

        // Set the map size
        getMapController().setAnchor(0, 0, 0, 0);
        getMapController().setContentLeftAnchor(400);

        // Reset mapcontroller
        onResetPressed();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(false);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
        ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
        // for setting the pathfinding sidebar to the internationalized language

        //btnSubmit.setText(rB.getString("my.search"));
        //searchBar.setText(rB.getString("my.search"));
        btClearPath.setText(rB.getString("my.clear"));
        //waypointLabel.setText(rB.getString("my.waypoints"));
    }

    /**
     * create new waypoint list cell
     */
    private void insertWaypointCell(Node node) {
        HBox waypointBox = new HBox();
        waypointBox.setMaxWidth(318);
        waypointBox.setAlignment(Pos.CENTER_LEFT);

        // Check if the waypoint is the default node
        if (SystemSettings.getInstance().getKioskLocation().getNodeID().equals(node.getNodeID())) {
            Label label = new Label("Current Location");
            label.setStyle("-fx-font-weight:bold;" + "-fx-font-size: 12pt; ");
            label.setPrefWidth(350);

            ImageView crosshairImageView = new ImageView(
                    ResourceManager.getInstance().getImage("/images/icons/pathfinding/currentLocation.png")
            );
            crosshairImageView.setFitWidth(24);
            crosshairImageView.setFitHeight(24);

            ImageView removeWaypointIconView = new ImageView(
                    ResourceManager.getInstance().getImage("/images/icons/pathfinding/close.png")
            );
            removeWaypointIconView.setPickOnBounds(true);
            removeWaypointIconView.setFitWidth(24);
            removeWaypointIconView.setFitHeight(24);
            removeWaypointIconView.setCursor(Cursor.HAND);
            removeWaypointIconView.setOnMouseClicked(event -> removeWaypoint(node));

            waypointBox.getChildren().addAll(crosshairImageView, label, removeWaypointIconView);

            HBox.setMargin(label, new Insets(0, 10, 0, 10));
        } else {
            Label nodeNameLabel = new Label(node.getLongName());
            nodeNameLabel.setStyle("-fx-font-weight:bold;" + "-fx-font-size: 12pt; ");
            nodeNameLabel.setPrefWidth(350);

            ImageView indicatorIconView = new ImageView(
                    ResourceManager.getInstance().getImage("/images/icons/pathfinding/list-waypoint.png")
            );
            indicatorIconView.setFitWidth(24);
            indicatorIconView.setFitHeight(24);

            ImageView removeWaypointIconView = new ImageView(
                    ResourceManager.getInstance().getImage("/images/icons/pathfinding/close.png")
            );
            removeWaypointIconView.setPickOnBounds(true);
            removeWaypointIconView.setFitWidth(24);
            removeWaypointIconView.setFitHeight(24);
            removeWaypointIconView.setCursor(Cursor.HAND);
            removeWaypointIconView.setOnMouseClicked(event -> removeWaypoint(node));

            waypointBox.getChildren().addAll(indicatorIconView, nodeNameLabel, removeWaypointIconView);

            HBox.setMargin(nodeNameLabel, new Insets(0, 10, 0, 10));
        }

        waypointViews.put(node, waypointBox);
        waypointListView.getItems().add(waypointListView.getItems().size() - 1, waypointBox);

        waypointBox.setOnMouseMoved(e ->resetTimer());
        waypointBox.setOnMousePressed(e -> resetTimer());

        waypointBox.setOnDragDetected(event -> {
            /* allow MOVE transfer mode */
            Dragboard db = waypointBox.startDragAndDrop(TransferMode.MOVE);

            /* put a string on dragboard */
            ClipboardContent content = new ClipboardContent();
            content.putString(Integer.toString(waypointListView.getItems().indexOf(waypointBox)));
            db.setContent(content);

            event.consume();
        });

        waypointBox.setOnDragOver(event -> {
            if (event.getGestureSource() != waypointBox &&
                    event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        waypointBox.setOnDragEntered(event -> {
            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != waypointBox &&
                    event.getDragboard().hasString()) {
            }
            event.consume();
        });

        waypointBox.setOnDragExited(event -> {
            /* mouse moved away, remove the graphical cues */
            event.consume();
        });

        waypointBox.setOnDragDropped(event -> {
            /* data dropped */
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                getMapController().swapWaypoint(waypointListView.getItems().indexOf(waypointBox), Integer.parseInt(db.getString()));
                HBox temp = waypointBox;
                waypointListView.getItems().set(waypointListView.getItems().indexOf(waypointBox),
                        waypointListView.getItems().get(Integer.parseInt(db.getString())));
                waypointListView.getItems().set(Integer.parseInt(db.getString()), temp);
                success = true;
            }
            event.setDropCompleted(success);

            event.consume();
        });

        waypointBox.setOnDragDone(event -> {
            System.out.println("onDragDone");
            if (event.getTransferMode() == TransferMode.MOVE) {
            }

            event.consume();
        });
    }

    /**
     * create add waypoint list cell
     */
    private void insertAddNewWaypointCell() {
        HBox addWaypointBox = new HBox();
        addWaypointBox.setMaxWidth(318);
        addWaypointBox.setAlignment(Pos.CENTER_LEFT);
        addWaypointBox.setOnMouseClicked(event -> isAddingWaypoint = true);

        ImageView addWaypointIconView = new ImageView(
                ResourceManager.getInstance().getImage("/images/icons/pathfinding/plus-circle.png")
        );
        addWaypointIconView.setPickOnBounds(true);
        addWaypointIconView.setFitWidth(24);
        addWaypointIconView.setFitHeight(24);
        addWaypointIconView.setCursor(Cursor.HAND);

        addWaypointBox.getChildren().addAll(addWaypointIconView, searchView);

        // Set margins
        HBox.setMargin(searchView, new Insets(0, 34, 0, 10));

        waypointListView.getItems().add(addWaypointBox);
    }

    /**
     * remove the target waypoint bounded with input node
     */
    private Node removeWaypoint(Node node) {
        if (currentWaypoints.contains(node)) {
            currentWaypoints.remove(node);

            return node;
        }

        return null;
    }

    /**
     * get the nearest node of required type to the default kiosk location
     */
    @FXML
    private void handleButtonAction(ActionEvent e) throws  PathfinderException {
        Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());
        Node node = new Node("");
        if((JFXButton)e.getTarget() == btRestRoom) { //TODO when wheelcair accesabiltiy is added gte input from that boolean
            node = pathfinder.findPathToNearestType(SystemSettings.getInstance().getKioskLocation(), NodeType.REST, true);
        }
        else if((JFXButton)e.getTarget() == btElevator) {
            node = pathfinder.findPathToNearestType(SystemSettings.getInstance().getKioskLocation(), NodeType.ELEV, true);
        }
        else if((JFXButton)e.getTarget() == btRestaurant) {
            node = pathfinder.findPathToNearestType(SystemSettings.getInstance().getKioskLocation(), NodeType.RETL, true);
        }
        else if((JFXButton)e.getTarget() == btExit) {
            node = pathfinder.findPathToNearestType(SystemSettings.getInstance().getKioskLocation(), NodeType.EXIT, true);
        }
        isAddingWaypoint = true;
        getMapController().setFloorSelector(node.getFloor());
        getMapController().zoomOnSelectedNodes(Arrays.asList(node));
        onMapNodeClicked(node);
    }

    // TEXT DIRECTIONS
    /**
     * Show the directions box
     */
    @FXML
    void showDirections() {
        addTextDirection();
        directionsContainer.setVisible(true);
    }

    /**
     * Hide the directions box
     */
    @FXML
    void hideDirections() {
        directionsContainer.setVisible(false);
    }

    /**
     * Populate the directions vbox with directions
     */
    private void addTextDirection() {
        textDirectionsBox.getChildren().clear();

        directionsLabel.setText("Directions to " + currentWaypoints.get(currentWaypoints.size() - 1).getLongName());

        for (int waypointIndex = 0; waypointIndex < currentWaypoints.size(); waypointIndex++) {
            AnchorPane waypointBox = new AnchorPane();
            waypointBox.setPrefWidth(400);
            waypointBox.setPrefHeight(80);
            Color thisColor = getMapController().getPath().getSegmentColor(waypointIndex);

            Line connectorLine = new Line(40, (waypointIndex == 0) ? 40 : 0, 40, (waypointIndex == currentWaypoints.size() - 1) ? 40 : 80);
            connectorLine.setStrokeWidth(2);
            waypointBox.getChildren().add(connectorLine);

            Circle bigWaypointCircle = new Circle(25, thisColor);
            bigWaypointCircle.setStroke(Color.BLACK);
            bigWaypointCircle.setStrokeWidth(1);
            bigWaypointCircle.setLayoutX(40);
            bigWaypointCircle.setLayoutY(40);
            waypointBox.getChildren().add(bigWaypointCircle);

            Label waypointName = new Label(currentWaypoints.get(waypointIndex).getLongName());
            waypointName.setAlignment(Pos.CENTER_LEFT);
            waypointName.setWrapText(true);
            waypointName.setFont(Font.font(24));
            waypointBox.getChildren().add(waypointName);
            AnchorPane.setTopAnchor(waypointName, 0D);
            AnchorPane.setBottomAnchor(waypointName, 0D);
            AnchorPane.setRightAnchor(waypointName, 15D);
            AnchorPane.setLeftAnchor(waypointName, 75D);

            textDirectionsBox.getChildren().add(waypointBox);

            // Check if we're not the last node in the list
            if (waypointIndex == currentWaypoints.size() - 1) continue;

            LinkedList<String> directions = getMapController().getIndexedDirection(waypointIndex);
            directions.removeLast();
            for (String direction : directions) {
                AnchorPane directionBox = new AnchorPane();
                directionBox.setPrefWidth(400);
                directionBox.setPrefHeight(40);

                Line directionConnectorLine = new Line(40, 0, 40, 40);
                directionConnectorLine.setStrokeWidth(2);
                directionBox.getChildren().add(directionConnectorLine);

                Circle directionCircle = new Circle(12, thisColor);
                directionCircle.setStroke(Color.BLACK);
                directionCircle.setStrokeWidth(1);
                directionCircle.setLayoutX(40);
                directionCircle.setLayoutY(20);
                directionBox.getChildren().add(directionCircle );

                Label directionLabel = new Label(direction);
                directionLabel.setAlignment(Pos.CENTER_LEFT);
                directionLabel.setWrapText(true);
                directionLabel.setFont(Font.font(14));
                directionBox.getChildren().add(directionLabel);
                AnchorPane.setTopAnchor(directionLabel, 0D);
                AnchorPane.setBottomAnchor(directionLabel, 0D);
                AnchorPane.setRightAnchor(directionLabel, 15D);
                AnchorPane.setLeftAnchor(directionLabel, 75D);

                textDirectionsBox.getChildren().add(directionBox);
            }
        }
    }
}