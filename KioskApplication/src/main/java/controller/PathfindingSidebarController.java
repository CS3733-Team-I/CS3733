package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.Path;
import entity.SystemSettings;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.*;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private GridPane waypointsContainer;
    @FXML private JFXListView<HBox> waypointListView;
    @FXML private Label exceptionText;

    @FXML private ImageView addIconView;
    @FXML private ImageView removeIconView;
    @FXML private JFXButton btNavigate;
    private Boolean isAddingWaypoint;

    private LinkedList<Node> currentNodes;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentNodes = new LinkedList<>();
        isAddingWaypoint = true;
    }

    @FXML
    void initialize() {
        // Set containers to be transparent to mouse events
        System.out.println("initializing");
        ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
        getMapController().setFloorSelector(NodeFloor.THIRD);
        container.setPickOnBounds(false);
        waypointsContainer.setPickOnBounds(false);
        waypointListView.setPickOnBounds(false);
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

        // for setting the pathfinding sidebar to the internationalized language
        //btnSubmit.setText(rB.getString("my.search"));
        //searchBar.setPromptText(rB.getString("my.search"));
        //clearButton.setText(rB.getString("my.clear"));
        btNavigate.setText(rB.getString("my.navigate"));
        //waypointLabel.setText(rB.getString("my.waypoints"));
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
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
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
        // Set the map size
        getMapController().setAnchor(0, 400, 0, 0);

        // Reset mapcontroller
        onResetPressed();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
        ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
        // for setting the pathfinding sidebar to the internationalized language

        //btnSubmit.setText(rB.getString("my.search"));
        //searchBar.setText(rB.getString("my.search"));
        //clearButton.setText(rB.getString("my.clear"));
        btNavigate.setText(rB.getString("my.navigate"));
        //waypointLabel.setText(rB.getString("my.waypoints"));
    }

    private void addPressAndHoldHandler(javafx.scene.Node node, Duration holdTime,
                                        EventHandler<MouseEvent> handler) {

        class Wrapper<T> { T content ; }
        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();

        PauseTransition holdTimer = new PauseTransition(holdTime);
        holdTimer.setOnFinished(event -> handler.handle(eventWrapper.content));


        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.content = event ;
            holdTimer.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());
        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    /**
     * create new waypoint list cell
     */
    private void newWaypointBox(Node node) {
        HBox waypointBox = new HBox();

        TextField nodeNameLabel = new TextField("(Click to Search) " + node.getLongName());
        nodeNameLabel.setPromptText("(Click to Search) " + node.getLongName());
//        nodeNameLabel.setTextFill(Color.BLACK);
        nodeNameLabel.setStyle("-fx-font-weight:bold; "+
                "-fx-font-size: 12pt; ");
        nodeNameLabel.setPrefWidth(300);

        JFXButton btRemoveWaypoint = new JFXButton("x");
        btRemoveWaypoint.setStyle("-fx-background-color: #ff000e;"+
                "-fx-font-size: 16pt; "+
                "-fx-font-weight:bold; "+
                "-fx-background-radius: 3em; ");
//            btRemoveWaypoint.setGraphic(removeIconView);
        btRemoveWaypoint.setPrefHeight(8);
        btRemoveWaypoint.setPrefWidth(8);
        btRemoveWaypoint.setTooltip(new Tooltip("Remove"));
        btRemoveWaypoint.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeWaypoint(node);
            }
        });

        waypointBox.getChildren().addAll(btRemoveWaypoint, nodeNameLabel);
        waypointBox.setAccessibleText(node.getNodeID());
        waypointBox.setAccessibleHelp("waypointCell");
        waypointBox.setAccessibleRoleDescription(node.getLongName());

        waypointBox.setMargin(btRemoveWaypoint, new Insets(1,1,1,1));
        waypointBox.setMargin(nodeNameLabel, new Insets(10,1,1,10));

        waypointBox.setStyle("-fx-background-color: #DDDED0;");

        waypointListView.getItems().add(waypointBox);

        waypointBox.setOnDragDetected(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* allow MOVE transfer mode */
                Dragboard db = waypointBox.startDragAndDrop(TransferMode.MOVE);

                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                content.putString(Integer.toString(waypointListView.getItems().indexOf(waypointBox)));
                db.setContent(content);

                event.consume();
            }
        });
        waypointBox.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != waypointBox &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        waypointBox.setOnDragEntered(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture entered the target */
                /* show to the user that it is an actual gesture target */
                if (event.getGestureSource() != waypointBox &&
                        event.getDragboard().hasString()) {
                    waypointBox.setStyle("-fx-background-color: #4e9f49;");
                }
                event.consume();
            }
        });
        waypointBox.setOnDragExited(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* mouse moved away, remove the graphical cues */
                waypointBox.setStyle("-fx-background-color:  #DDDED0;");
                event.consume();
            }
        });
        waypointBox.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
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
            }
        });
        waypointBox.setOnDragDone(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                System.out.println("onDragDone");
                if (event.getTransferMode() == TransferMode.MOVE) {
                }

                event.consume();
            }
        });
    }

    /**
     * replace the waypoint cells with text direction
     */
    private void addTextDirection() {
        for(HBox waypointCell : waypointListView.getItems()) {
            if(waypointCell.getAccessibleHelp() != null) {
                if(waypointCell.getAccessibleHelp().equals("waypointCell")) {
                    waypointCell.getChildren().clear();

                    VBox directionLabelBox = new VBox();

                    Label waypointLabel = new Label(waypointListView.getItems().indexOf(waypointCell)+1 + ". " + waypointCell.getAccessibleRoleDescription());
                    waypointLabel.setTextFill(Color.RED);
                    waypointLabel.setStyle("-fx-font-weight:bold; "+
                            "-fx-font-size: 16pt; ");
                    waypointLabel.setPrefWidth(300);
                    directionLabelBox.getChildren().add(waypointLabel);

                    if(getMapController().getIndexedDirection(waypointListView.getItems().indexOf(waypointCell)) != null) {
                        for(String textDirection : getMapController().getIndexedDirection(waypointListView.getItems().indexOf(waypointCell))) {
                            Label directionLabel = new Label(textDirection);
                            directionLabel.setStyle("-fx-font-weight:bold; "+
                                    "-fx-font-size: 12pt; "+
                                    " -fx-underline: true;");
                            directionLabel.setTextFill(getMapController().getsSegmentColorList().get(waypointListView.getItems().indexOf(waypointCell)));
                            directionLabelBox.getChildren().add(directionLabel);
                        }
                    }
                    if(directionLabelBox.getChildren().size() == 1) {
                        Label destinationLabel = new Label();
                        destinationLabel.setText("*DESTINATION*");
                        destinationLabel.setStyle("-fx-font-weight:bold; "+
                                "-fx-font-size: 16pt; ");
                        directionLabelBox.getChildren().add(destinationLabel);
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
//        int addWaypointBoxIndex = 0;
        HBox addWaypointBox = new HBox();

        TextField addWaypointLabel = new TextField();
        addWaypointLabel.setPromptText("Click + to Add, Here to Search");
//        addWaypointLabel.setTextFill(Color.BLACK);
        addWaypointLabel.setPrefWidth(300);
        addWaypointLabel.setStyle("-fx-font-weight:bold; "+
                "-fx-font-size: 12pt; ");
        JFXButton btNewWayPoint = new JFXButton("+");
        btNewWayPoint.setStyle("-fx-background-color: #00589F;"+
                "-fx-font-size: 16pt; "+
                "-fx-font-weight: bold; "+
                "-fx-font-weight: 900;"+
                "-fx-background-radius: 3em; ");
        btNewWayPoint.setPrefHeight(8);
        btNewWayPoint.setPrefWidth(8);
//                      btNewWayPoint.setGraphic(addIconView);
        btNewWayPoint.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isAddingWaypoint = true;
            }
        });
        btNewWayPoint.setTooltip(new Tooltip("Add Waypoint"));
        addWaypointBox.getChildren().addAll(btNewWayPoint, addWaypointLabel);
        addWaypointBox.setAccessibleText("add waypoint");
        addWaypointBox.setMargin(btNewWayPoint, new Insets(1,1,1,1));
        addWaypointBox.setMargin(addWaypointLabel, new Insets(10,1,1,10));
//        waypointListView.getItems().remove(addWaypointBoxIndex);
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