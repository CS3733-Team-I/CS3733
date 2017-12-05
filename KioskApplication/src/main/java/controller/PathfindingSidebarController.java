package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.SystemSettings;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import pathfinder.Pathfinder;
import pathfinder.PathfinderException;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public class PathfindingSidebarController extends ScreenController {

    @FXML private AnchorPane container;
    @FXML private GridPane waypointsContainer;
    @FXML private JFXListView<HBox> waypointListView;

    @FXML private Label exceptionText;

    @FXML
    private ImageView addIconView;
    @FXML
    private ImageView removeIconView;
    @FXML
    private JFXButton btNavigate;
    private Boolean isAddingWaypoiny;

    private LinkedList<Node> currentNodes;

    public PathfindingSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        currentNodes = new LinkedList<>();
        isAddingWaypoiny = true;
    }

    @FXML
    void initialize() {
        // Set containers to be transparent to mouse events
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

        waypointListView.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change c) {
                while (c.next()) {
                    if(waypointListView.getItems().size() < 2) {
                        btNavigate.setDisable(true);
                    }
                    else {
                        btNavigate.setDisable(false);
                    }
                }
            }
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
        exceptionText.setText("");
        if (currentNodes.size() > 0) {
            Pathfinder pathfinder = new Pathfinder(SystemSettings.getInstance().getAlgorithm());
            try{
                getMapController().setPath(pathfinder.generatePath(currentNodes));
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
        if (isAddingWaypoiny) {
            if (!currentNodes.contains(node)) {
                currentNodes.add(node);

                newWaypointBox(node);

                getMapController().addWaypoint(new Point2D(node.getXcoord(), node.getYcoord()), node);

                isAddingWaypoiny = false;
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
        waypointBox.setMargin(btRemoveWaypoint, new Insets(1,1,1,1));
        waypointBox.setMargin(nodeNameLabel, new Insets(10,1,1,10));
        waypointListView.getItems().add(waypointBox);
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
        btNewWayPoint.setStyle("-fx-background-color: #00ff02;"+
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
                isAddingWaypoiny = true;
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