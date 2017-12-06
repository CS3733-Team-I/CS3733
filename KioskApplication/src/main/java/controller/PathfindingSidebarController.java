package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.Path;
import entity.SystemSettings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    @FXML private GridPane waypointsContainer;
    @FXML private JFXListView<HBox> waypointListView;
    @FXML private Label exceptionText;

    @FXML private ImageView addIconView;
    @FXML private ImageView removeIconView;
    @FXML private JFXButton btNavigate, clearButton;
    private Boolean isAddingWaypoint;

    private LinkedList<Node> currentNodes;

    private SystemSettings systemSettings;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentNodes = new LinkedList<>();
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
        waypointsContainer.setPickOnBounds(false);
        waypointListView.setPickOnBounds(false);
        waypointListView.setSelectionModel(new NoSelectionModel<>());
        exceptionText.setText("");

        Image addIcon = ResourceManager.getInstance().getImage("/images/icons/plus.png");
        ImageView infoIconView = new ImageView(addIcon);
        infoIconView.setFitHeight(24);
        infoIconView.setFitWidth(24);

        Image removeIcon = ResourceManager.getInstance().getImage("/images/icons/delete.png");
        ImageView removeView = new ImageView(removeIcon);
        removeView.setFitHeight(24);
        removeView.setFitWidth(24);

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
            clearButton.setText(resB.getString("my.clear"));
            btNavigate.setText(resB.getString("my.navigate"));
            //waypointLabel.setText(resB.getString("waypoints"));
        });
    }

    @FXML
    void onResetPressed() {
        currentNodes.clear();
        waypointListView.getItems().clear();
        addWaypointBox();
        exceptionText.setText("");

        getMapController().setPath(null);
        getMapController().clearMap();
        getMapController().reloadDisplay();
    }

    @FXML
    void btGeneratePathPressed() throws IOException {
        if(getMapController().isPathShowing()) {
            onResetPressed();
        }
        exceptionText.setText("");
        if (currentNodes.size() > 0) {
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());
            try{
                Path path = pathfinder.generatePath(currentNodes);
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
            if (!currentNodes.contains(node)) {
                currentNodes.add(node);

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
            currentNodes.add(node);
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
        clearButton.setText(rB.getString("my.clear"));
        btNavigate.setText(rB.getString("my.navigate"));
        //waypointLabel.setText(rB.getString("my.waypoints"));
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
        btRemoveWaypoint.setGraphic(new ImageView(ResourceManager.getInstance().getImage("/images/icons/close-circle.png")));
        btRemoveWaypoint.setStyle("-fx-background-color: transparent");
        btRemoveWaypoint.setTooltip(new Tooltip("Remove"));
        btRemoveWaypoint.setOnAction(event -> removeWaypoint(node));

        waypointBox.getChildren().addAll(btRemoveWaypoint, nodeNameLabel);
        waypointBox.setAccessibleText(node.getNodeID());
        waypointBox.setAccessibleHelp("waypointCell");
        waypointBox.setAccessibleRoleDescription(node.getLongName());

        waypointListView.getItems().add(waypointBox);

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

                    if (getMapController().getIndexedDirection(waypointListView.getItems().indexOf(waypointCell)) != null) {
                        for(String textDirection : getMapController().getIndexedDirection(waypointListView.getItems().indexOf(waypointCell))) {
                            Label directionLabel = new Label(textDirection);
                            directionLabel.setTextFill(Color.BLACK);
                            directionLabel.setStyle("-fx-font-weight:bold; "+
                                    "-fx-font-size: 12pt; "+
                                    " -fx-underline: true;");
                            directionLabelBox.getChildren().add(directionLabel);
                        }
                    }

                    waypointCell.getChildren().add(directionLabelBox);
                }
            }
        }
    }

    /**
     * remove the target waypoint bounded with input node
     */
    private void removeWaypoint(Node node) {
        if(waypointListView.getItems().size()>=2) {
            getMapController().removeWaypoint(currentNodes.get(currentNodes.size()-1));
            waypointListView.getItems().remove(waypointListView.getItems().size()-2);
            currentNodes.remove(currentNodes.size()-1);
        }
    }
    /**
     * create add waypoint list cell
     */
    //TODO make addwaypointbox always the last one
    private void addWaypointBox() {
        HBox addWaypointBox = new HBox();

        addWaypointBox.setAlignment(Pos.CENTER_LEFT);

        TextField addWaypointLabel = new TextField();
        addWaypointLabel.setPromptText(SystemSettings.getInstance().getResourceBundle().getString("my.searchprompt"));
        addWaypointLabel.setPrefWidth(300);
        addWaypointLabel.setStyle("-fx-font-weight:bold; -fx-font-size: 12pt; ");
        addWaypointLabel.setPadding(new Insets(0, 0, 0, 10));

        JFXButton btNewWayPoint = new JFXButton("");
        btNewWayPoint.setGraphic(new ImageView(ResourceManager.getInstance().getImage("/images/icons/plus-circle.png")));
        btNewWayPoint.setStyle("-fx-background-color: transparent");
        btNewWayPoint.setOnAction(event -> isAddingWaypoint = true);
        btNewWayPoint.setTooltip(new Tooltip("Add Waypoint"));

        addWaypointBox.getChildren().addAll(btNewWayPoint, addWaypointLabel);
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
}