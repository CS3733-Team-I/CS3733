package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.NoSelectionModel;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.request.RequestType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private GridPane waypointsContainer;
    @FXML private JFXListView<HBox> waypointListView;
    @FXML private Label exceptionText;

    @FXML private ImageView addIconView;
    @FXML private ImageView removeIconView;
    @FXML private JFXButton btNavigate;
    @FXML private  JFXButton btClearPath;

    @FXML private JFXButton btExit;
    @FXML private JFXButton btRestRoom;
    @FXML private JFXButton btRestaurant;
    @FXML private JFXButton btElevator;

    private Boolean isAddingWaypoint;

    private LinkedList<Node> currentWaypoints;

    private SystemSettings systemSettings;

    private SearchController searchController;

    private javafx.scene.Node searchView;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentWaypoints = new LinkedList<>();
        systemSettings = SystemSettings.getInstance();
        isAddingWaypoint = true;
        ArrayList<ISearchEntity> searchNodeAndDoctor = new ArrayList<>();
        SystemSettings.getInstance().updateDistance();
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
    }

    @FXML
    void initialize() throws IOException{
        //initialize search
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/view/searchView.fxml"));
        searchLoader.setController(searchController);
        searchView = searchLoader.load();

        // Set containers to be transparent to mouse events
        ResourceBundle rB = systemSettings.getResourceBundle();
        getMapController().setFloorSelector(NodeFloor.THIRD);
        container.setPickOnBounds(false);
        waypointsContainer.setPickOnBounds(false);
        waypointListView.setPickOnBounds(false);
        waypointListView.setSelectionModel(new NoSelectionModel<>());
        exceptionText.setText("");

        /**
         * load images for nearest exit, elevatior, food and restroom
         */
        Image addIcon = ResourceManager.getInstance().getImage("/images/icons/plus.png");
        ImageView infoIconView = new ImageView(addIcon);
        infoIconView.setFitHeight(24);
        infoIconView.setFitWidth(24);

        Image removeIcon = ResourceManager.getInstance().getImage("/images/icons/delete.png");
        ImageView removeView = new ImageView(removeIcon);
        removeView.setFitHeight(24);
        removeView.setFitWidth(24);

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

        addWaypointBox();

        waypointListView.getItems().addListener((ListChangeListener<HBox>) c -> {
            while (c.next()) {
                if(waypointListView.getItems().size() < 2) {
                    btNavigate.setDisable(true);
                }
                else {
                    btNavigate.setDisable(false);
                }
            }
        });

        systemSettings.addObserver((o, arg) -> {
            ResourceBundle resB = systemSettings.getResourceBundle();
            //btnSubmit.setText(resB.getString("search"));
            //searchBar.setPromptText(resB.getString("search"));
            btClearPath.setText(resB.getString("my.clear"));
            btNavigate.setText(resB.getString("my.navigate"));
            //btClear.setText(resB.getString("clear"));

            btNavigate.setText(resB.getString("navigate"));
            //waypointLabel.setText(resB.getString("waypoints"));
            btClearPath.setText(resB.getString("clearpath"));
        });

        searchController.getCBValueProperty().addListener(new ChangeListener<ISearchEntity>() {
            @Override
            public void changed(ObservableValue<? extends ISearchEntity> observable, ISearchEntity oldValue, ISearchEntity newValue) {
                if (newValue != null) {
                    if(((Node) newValue.getLocation()).getFloor() != getMapController().getCurrentFloor()) {
                        getMapController().setFloorSelector(((Node)newValue.getLocation()).getFloor());
                    }
                    LinkedList<Node> displayedNode = new LinkedList<>();
                    displayedNode.add((newValue.getLocation()));
                    getMapController().zoomOnSelectedNodes(displayedNode);
                    onMapNodeClicked((newValue.getLocation()));
                }
            }
        });
    }

    @FXML
    void onResetPressed() {
        currentWaypoints.clear();
        waypointListView.getItems().clear();
        addWaypointBox();
        exceptionText.setText("");

        getMapController().setPath(null);
        getMapController().clearMap();
        getMapController().getMiniMapController().clearMiniWaypoint();
        getMapController().reloadDisplay();
    }

    public void enableNavBtn(){
        btNavigate.setDisable(false);
    }
    public void disableClearBtn(){
        btClearPath.setDisable(true);
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        if(getMapController().isPathShowing()) {
            onResetPressed();
        }
        exceptionText.setText("");
        if (currentWaypoints.size() > 0) {
            if(currentWaypoints.size() == 1) {
                isAddingWaypoint = true;
                Node end = removeWaypoint(currentWaypoints.get(0));
                onMapNodeClicked(SystemSettings.getInstance().getKioskLocation());
                isAddingWaypoint = true;
                onMapNodeClicked(end);
            }
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());

            try{
                Path path = pathfinder.generatePath(currentWaypoints);
                getMapController().setPath(path);
                LinkedList<LinkedList<String>> directionsList = getMapController().getPath().getDirectionsList();
                for(LinkedList<String> directionSegment: directionsList) {
                    for (String direction : directionSegment) {
                        Label label = new Label(direction);
                        label.setTextFill(Color.BLACK);
                        //TODO FIX THIS
                    }
                }
            }
            catch(PathfinderException exception){
                exceptionText.setText("ERROR! "+ exception.getMessage());
            }
        }
        addTextDirection();
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
        if(getMapController().isPathShowing()) {
            onResetPressed();
        }
        if (isAddingWaypoint) {
            if (!currentWaypoints.contains(node)) {
                currentWaypoints.add(node);
                newWaypointBox(node);

                getMapController().addWaypoint(new Point2D(node.getXcoord(), node.getYcoord()), node);

                isAddingWaypoint = false;
            }
        }
        else {
            //remove last node
            removeWaypoint(node);
            //add new waypoint
            newWaypointBox(node);
            currentWaypoints.add(node);
            getMapController().addWaypoint(new Point2D(node.getXcoord(), node.getYcoord()), node);
        }
        addWaypointBox();
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
        getMapController().setAnchor(0, 400, 0, 0);

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
        btNavigate.setText(rB.getString("my.navigate"));
        //waypointLabel.setText(rB.getString("my.waypoints"));

        //reset search
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
    }

    /**
     * create new waypoint list cell
     */
    private void newWaypointBox(Node node) {
        HBox waypointBox = new HBox();

        waypointBox.setAlignment(Pos.CENTER_LEFT);

        Label nodeNameLabel = new Label(node.getLongName());
        nodeNameLabel.setStyle("-fx-font-weight:bold;" + "-fx-font-size: 12pt; ");
        nodeNameLabel.setPrefWidth(300);
        nodeNameLabel.setPadding(new Insets(0, 0, 0, 10));

        JFXButton btRemoveWaypoint = new JFXButton("");
        btRemoveWaypoint.setOnMouseMoved(e -> resetTimer());
        btRemoveWaypoint.setOnMousePressed(e -> resetTimer());
        btRemoveWaypoint.setGraphic(new ImageView(ResourceManager.getInstance().getImage("/images/icons/close-circle.png")));
        btRemoveWaypoint.setStyle("-fx-background-color: transparent");
        btRemoveWaypoint.setTooltip(new Tooltip("Remove"));
        btRemoveWaypoint.setOnAction(event -> removeWaypoint(node));

        waypointBox.getChildren().addAll(btRemoveWaypoint, nodeNameLabel);
        waypointBox.setAccessibleText(node.getNodeID());
        waypointBox.setAccessibleHelp("waypointCell");
        waypointBox.setAccessibleRoleDescription(node.getLongName());

        waypointListView.getItems().add(waypointBox);

        waypointBox.setOnMouseMoved(e ->resetTimer());
        waypointBox.setOnMousePressed(e ->resetTimer());
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
     * replace the waypoint cells with text direction
     */
    private void addTextDirection() {
        for(HBox waypointCell : waypointListView.getItems()) {
            waypointCell.setOnDragDetected(null);

            if(waypointCell.getAccessibleHelp() != null) {
                if(waypointCell.getAccessibleHelp().equals("waypointCell")) {
                    waypointCell.getChildren().clear();

                    VBox directionLabelBox = new VBox();

                    Label waypointLabel = new Label(waypointListView.getItems().indexOf(waypointCell)+1 + ". " + waypointCell.getAccessibleRoleDescription());
                    try {
                        waypointLabel.setTextFill(getMapController().getsSegmentColorList().get(waypointListView.getItems().indexOf(waypointCell)));
                    } catch (IndexOutOfBoundsException e) {
                        waypointLabel.setTextFill(Color.BLACK);
                    }

                    waypointLabel.setStyle("-fx-font-weight:bold; "+
                            "-fx-font-size: 16pt; ");
                    directionLabelBox.getChildren().add(waypointLabel);

                    TextFlow directionLabel = new TextFlow();
                    directionLabel.setPrefWidth(300);
                    directionLabel.setLineSpacing(5);
                    directionLabel.setStyle("-fx-text-fill: black;" +
                            "-fx-font-weight:bold; "+
                            "-fx-font-size: 12pt; "+
                            " -fx-underline: true;");

                    if (getMapController().getIndexedDirection(waypointListView.getItems().indexOf(waypointCell)) != null) {
                        Text direction = new Text();
                        String lastDirection = "";
                        for(String textDirection : getMapController().getIndexedDirection(waypointListView.getItems().indexOf(waypointCell))) {
                            direction = new Text(textDirection + "\n\n");
                            directionLabel.getChildren().add(direction);

                            lastDirection = textDirection;
                        }

                        // Set last text direction string to not have new lines
                        direction.setText(lastDirection);
                    }

                    directionLabelBox.getChildren().add(directionLabel);

                    waypointCell.getChildren().add(directionLabelBox);
                }
            }
        }
    }

    /**
     * remove the target waypoint bounded with input node
     */
    private Node removeWaypoint(Node node) {
        if(waypointListView.getItems().size()>=2) {
            getMapController().removeWaypoint(currentWaypoints.get(currentWaypoints.size()-1));
            waypointListView.getItems().remove(waypointListView.getItems().size()-2);
            return currentWaypoints.remove(currentWaypoints.size()-1);
        }
        else return null;
    }
    /**
     * create add waypoint list cell
     */
    //TODO make addwaypointbox always the last one
    private void addWaypointBox(){
        HBox addWaypointBox = new HBox();

        addWaypointBox.setAlignment(Pos.CENTER_LEFT);

//        TextField addWaypointLabel = new TextField();
        searchController.setSearchFieldPromptText(
                SystemSettings.getInstance().getResourceBundle().getString("my.searchprompt"));
        searchView.setOnMouseMoved(e -> resetTimer());
        searchView.setOnMousePressed(e -> resetTimer());
       /// searchView
        systemSettings.addObserver((o, arg) -> {
            searchController.setSearchFieldPromptText(systemSettings.getResourceBundle().getString("my.searchprompt"));
        });

        JFXButton btNewWayPoint = new JFXButton("");
        btNewWayPoint.setOnMouseMoved(e -> resetTimer());
        btNewWayPoint.setOnMousePressed(e -> resetTimer());
        btNewWayPoint.setGraphic(new ImageView(ResourceManager.getInstance().getImage("/images/icons/plus-circle.png")));
        btNewWayPoint.setStyle("-fx-background-color: transparent");
        btNewWayPoint.setOnAction(event -> isAddingWaypoint = true);
        btNewWayPoint.setTooltip(new Tooltip("Add Waypoint"));

        addWaypointBox.getChildren().addAll(btNewWayPoint, searchView);
        addWaypointBox.setAccessibleText("add waypoint");

        Iterator<HBox> addwaypointIterator = waypointListView.getItems().iterator();
        while(addwaypointIterator.hasNext()) {
            HBox lastAddWaypoint = addwaypointIterator.next();
            if(lastAddWaypoint.getAccessibleText().equals("add waypoint")) {
                addwaypointIterator.remove();
            }
        }
        waypointListView.getItems().add(addWaypointBox);
//        addWaypointBoxIndex = waypointListView.getItems().indexOf(addWaypointBox);

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
        onMapNodeClicked(node);
    }
}