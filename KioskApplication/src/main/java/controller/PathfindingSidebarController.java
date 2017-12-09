package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import controller.map.MapController;
import controller.map.WaypointView;
import database.objects.Edge;
import database.objects.Node;
import entity.Path;
import entity.SystemSettings;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
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

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private AnchorPane waypointsContainer;
    @FXML private JFXListView<HBox> waypointListView;

    @FXML private ImageView addIconView;
    @FXML private ImageView removeIconView;
    @FXML private  JFXButton btClearPath;
    private Boolean isAddingWaypoint;

    private LinkedList<Node> currentWaypoints;

    private SystemSettings systemSettings;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentWaypoints = new LinkedList<>();
        systemSettings = SystemSettings.getInstance();

        isAddingWaypoint = true;
    }

    @FXML
    void initialize() {
        // Set containers to be transparent to mouse events
        System.out.println("initializing");
        ResourceBundle rB = systemSettings.getResourceBundle();
        getMapController().setFloorSelector(NodeFloor.THIRD);
        container.setPickOnBounds(false);
        waypointListView.setPickOnBounds(false);
        waypointListView.setSelectionModel(new NoSelectionModel<>());
        waypointListView.setFixedCellSize(50);
        waypointListView.prefHeightProperty().bind(Bindings.size(waypointListView.getItems()).multiply(waypointListView.getFixedCellSize()).add(15));

        insertAddNewWaypointCell();
        insertCurrentLocationCell();

        systemSettings.addObserver((o, arg) -> {
            ResourceBundle resB = systemSettings.getResourceBundle();
            //btnSubmit.setText(resB.getString("search"));
            //searchBar.setPromptText(resB.getString("search"));
            btClearPath.setText(resB.getString("my.clear"));
            //btClear.setText(resB.getString("clear"));

            //waypointLabel.setText(resB.getString("waypoints"));
            btClearPath.setText(resB.getString("clearpath"));
        });
    }

    @FXML
    void onResetPressed() {
        currentWaypoints.clear();
        waypointListView.getItems().clear();
        insertAddNewWaypointCell();
        insertCurrentLocationCell();

        getMapController().setPath(null);
        getMapController().clearMap();
        getMapController().reloadDisplay();
    }

    public void disableClearBtn(){
        btClearPath.setDisable(true);
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        if(getMapController().isPathShowing()) {
            onResetPressed();
        }
        //exceptionText.setText("");
        if (currentWaypoints.size() > 0) {
            if(currentWaypoints.size() == 1) {
                isAddingWaypoint = true;
                Node end = removeWaypoint(currentWaypoints.get(0));
                onMapNodeClicked(SystemSettings.getInstance().getDefaultnode());
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
                exception.printStackTrace();
                //exceptionText.setText("ERROR! "+ exception.getMessage());
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
                insertWaypointCell(node);

                getMapController().addWaypoint(node);

                isAddingWaypoint = false;
            }
        } else {
            //remove last node
            removeWaypoint(node);

            //add new waypoint
            insertWaypointCell(node);
            currentWaypoints.add(node);
            getMapController().addWaypoint(node);
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

        waypointListView.getItems().add(waypointListView.getItems().size() - 1, waypointBox);


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
        if(waypointListView.getItems().size() > 2) {
            getMapController().removeWaypoint(currentWaypoints.getLast());
            waypointListView.getItems().remove(waypointListView.getItems().size() - 2);
            return currentWaypoints.removeLast();
        } else {
            return null;
        }
    }

    private void insertCurrentLocationCell() {
        HBox currentLocationBox = new HBox();
        currentLocationBox.setPrefWidth(318);
        currentLocationBox.setAlignment(Pos.CENTER_LEFT);

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
        //removeWaypointIconView.setOnMouseClicked(event -> removeWaypoint(node));

        currentLocationBox.getChildren().addAll(crosshairImageView, label, removeWaypointIconView);

        HBox.setMargin(label, new Insets(0, 10, 0, 10));

        waypointListView.getItems().add(waypointListView.getItems().size() - 1, currentLocationBox);
    }


    /**
     * create add waypoint list cell
     */
    //TODO make addwaypointbox always the last one
    private void insertAddNewWaypointCell() {
        HBox addWaypointBox = new HBox();
        addWaypointBox.setMaxWidth(318);
        addWaypointBox.setAlignment(Pos.CENTER_LEFT);
        addWaypointBox.setOnMouseClicked(event -> isAddingWaypoint = true);

        TextField addWaypointLabel = new TextField();
        addWaypointLabel.setPromptText(
                SystemSettings.getInstance().getResourceBundle().getString("my.searchprompt"));
        addWaypointLabel.setPrefWidth(350);
        addWaypointLabel.setStyle("-fx-font-weight:bold; -fx-font-size: 12pt; ");
        addWaypointLabel.setOnMouseMoved(e -> resetTimer());
        addWaypointLabel.setOnMousePressed(e -> resetTimer());

        systemSettings.addObserver((o, arg) -> {
            addWaypointLabel.setText(systemSettings.getResourceBundle().getString("my.searchprompt"));
        });

        ImageView addWaypointIconView = new ImageView(
                ResourceManager.getInstance().getImage("/images/icons/pathfinding/plus-circle.png")
        );
        addWaypointIconView.setPickOnBounds(true);
        addWaypointIconView.setFitWidth(24);
        addWaypointIconView.setFitHeight(24);
        addWaypointIconView.setCursor(Cursor.HAND);

        addWaypointBox.getChildren().addAll(addWaypointIconView, addWaypointLabel);

        // Set margins
        HBox.setMargin(addWaypointLabel, new Insets(0, 34, 0, 10));

        waypointListView.getItems().add(addWaypointBox);
    }
}