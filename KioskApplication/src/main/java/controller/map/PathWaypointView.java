package controller.map;

import database.connection.NotFoundException;
import database.objects.Node;
import entity.MapEntity;
import entity.Path;
import entity.SystemSettings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PathWaypointView extends AnchorPane {

    private ObservableList<Node> waypointList;
    protected Path currentPath;

    private HashMap<Node, WaypointView> wayPointViewsMap;

    private AnchorPane pathView;
    private AnchorPane wayPointView;
    private AnchorPane floorChangeView;
    private AnchorPane popupView;
    private ImageView kioskLocationView;
    private Node kiosk;

    private javafx.scene.image.ImageView upView;
    private javafx.scene.image.ImageView downView;

    MapController parent;

    public PathWaypointView(MapController parent) throws NotFoundException {
        this.setPickOnBounds(false);
        wayPointView = new AnchorPane();
        wayPointViewsMap = new HashMap<>();
        wayPointView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(wayPointView, 0.0);
        AnchorPane.setLeftAnchor(wayPointView, 0.0);
        AnchorPane.setBottomAnchor(wayPointView, 0.0);
        AnchorPane.setRightAnchor(wayPointView, 0.0);

        pathView = new AnchorPane();
        pathView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(pathView, 0.0);
        AnchorPane.setLeftAnchor(pathView, 0.0);
        AnchorPane.setBottomAnchor(pathView, 0.0);
        AnchorPane.setRightAnchor(pathView, 0.0);


        popupView = new AnchorPane();
        popupView.setPickOnBounds(false);

        AnchorPane.setTopAnchor(popupView, 0.0);
        AnchorPane.setLeftAnchor(popupView, 0.0);
        AnchorPane.setBottomAnchor(popupView, 0.0);
        AnchorPane.setRightAnchor(popupView, 0.0);

        floorChangeView = new AnchorPane();
        floorChangeView.setPickOnBounds(false);

        this.getChildren().addAll(pathView, wayPointView, floorChangeView, popupView);

        //Kiosk init
        kiosk = SystemSettings.getInstance().getKioskLocation();
        kioskLocationView = getYouAreHereIcon(kiosk);

        if(kiosk.getFloor() == parent.getCurrentFloor()){
            this.getChildren().add(kioskLocationView);
        }

        waypointList = FXCollections.observableArrayList();

        this.parent = parent;

        Image arrowButtonIcon = ResourceManager.getInstance().getImage("/images/icons/arrow-button.png");
        upView = new javafx.scene.image.ImageView(arrowButtonIcon);
        upView.setFitHeight(48);
        upView.setFitWidth(48);

        downView = new javafx.scene.image.ImageView(arrowButtonIcon);
        downView.setRotate(180);
        downView.setFitHeight(48);
        downView.setFitWidth(48);

        waypointList.addListener((ListChangeListener<Node>) listener -> {
            while (listener.next()) {
                if(listener.wasRemoved()) {
                    for (Node removedWaypoint : listener.getRemoved()) {
                        WaypointView removedView = this.wayPointViewsMap.get(removedWaypoint);
                        this.wayPointViewsMap.remove(removedWaypoint);
                        this.wayPointView.getChildren().remove(removedView);

                        int index = 0;
                        for (Node node : waypointList) {
                            if(wayPointViewsMap.containsKey(node)) wayPointViewsMap.get(node).setWaypointCount(index++);
                        }
                    }
                }
                if(listener.wasAdded()) {
                    for(Node addedWaypoint : listener.getAddedSubList()) {
                        WaypointView waypointView = new WaypointView(this, addedWaypoint, waypointList.indexOf(addedWaypoint));
                        this.wayPointViewsMap.put(addedWaypoint, waypointView);
                        this.wayPointView.getChildren().add(waypointView);

                        waypointView.playWaypointPutTransition();
                    }
                }
            }
        });

        SystemSettings.getInstance().kioskLocationProperty().addListener((obj, oldValue, newValue) -> {
            if (newValue == null) return;

            kioskLocationView = getYouAreHereIcon(newValue);
            kiosk = newValue;

            getChildren().clear();
            getChildren().addAll(pathView, wayPointView, floorChangeView, popupView);
            if(kiosk.getFloor() == parent.getCurrentFloor()){
                getChildren().add(kioskLocationView);
            }
        });

        clearPath();
        clearWaypoint();
    }

    private ImageView getYouAreHereIcon(Node node) {
        Image youarehereIcon = ResourceManager.getInstance().getImage("/images/crosshairs-gps.png");
        ImageView youarehereView = new javafx.scene.image.ImageView(youarehereIcon);
        youarehereView.setFitHeight(48);
        youarehereView.setFitWidth(48);
        youarehereView.setMouseTransparent(true);

        youarehereView.setLayoutX(node.getXcoord() - (youarehereIcon.getWidth() / 2));
        youarehereView.setLayoutY(node.getYcoord() - (youarehereIcon.getHeight() / 2));

        return youarehereView;
    }

    public void showPopup(Node node, NodeFloor floor, MouseEvent e){ // Add mouse event to get offset
        System.out.println("Show Popup");
        closePopup();
        // Adjust popup
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: transparent;");
        HBox hbox = new HBox();
        PreviewMap iv = new PreviewMap(floor, parent);
        iv.setOnMouseClicked(ev -> {
            parent.setFloorSelector(floor);
            closePopup();
        });
        hbox.getChildren().add(iv);
        iv.drawSections();
        hbox.setOnMouseExited(ev -> closePopup());
        hbox.setStyle("-fx-background-color: transparent;");
        VBox vbox = new VBox();
        vbox.setStyle("-fx-background-color: transparent;");
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(hbox);
        Image tri = ResourceManager.getInstance().getImage("/images/downTriangle.png");
        ImageView triView = new ImageView(tri);
        vbox.getChildren().add(triView);
        anchorPane.getChildren().add(vbox);
        hbox.setStyle("-fx-border-color: black; -fx-border-width: 2px");
        vbox.setOnMouseMoved(ev -> parent.resetTimer());

        // Show popup
        System.out.println("Show Popup");
        System.out.println("X: " + e.getSceneX() + ", Y: " + e.getSceneY());
        popupView.getChildren().add(anchorPane);
        double xadj = 0-(anchorPane.getWidth()/2);
        double yadj = 0-(anchorPane.getHeight()+20);
        anchorPane.setLayoutX(node.getXcoord()+xadj);
        anchorPane.setLayoutY(node.getYcoord()+yadj);
    }

    public void closePopup(){
        System.out.println("Close Popup");
        popupView.getChildren().clear();
    }

    /**
     * draw a path
     */
    //TODO rename this
    public void setPath(Path path) {
        this.currentPath = path;
    }

    /**
     * Clear drawn path
     */
    public void clearPath() {
//        LinkedList<Node> allNodes = this.currentPath.getListOfAllNodes();
//        for(Node node: allNodes){
//            this.parent.removeWaypoint(node);
//        }
        this.currentPath = null;
        pathView.getChildren().clear();
        popupView.getChildren().clear();
    }
    /**
     * Clear drawn waypoints
     */
    public void clearWaypoint() {
        this.waypointList.clear();
        wayPointView.getChildren().clear();
    }
    /**
     * Clear both waypoints and path
     */
    public void clearAll() {
        clearWaypoint();
        clearPath();
        floorChangeView.getChildren().clear();
    }

    public void drawPath(Path path) {
        floorChangeView.getChildren().clear();
        popupView.getChildren().clear();
        if (getChildren().contains(kioskLocationView)) {
            getChildren().remove(kioskLocationView);
        }
        if(kiosk.getFloor() == parent.getCurrentFloor()){
            getChildren().add(kioskLocationView);
        }

        this.currentPath = path;

        double totalDistance = 0;

        int waypointIndex = 0;
        for (Node node : waypointList) {
            Node lastNode = node;
            LinkedList<Node> nodesInSegment = path.getNodesInSegment(node);
            for (Node thisNode : nodesInSegment) {
                // Don't draw a line between the same nodes
                if (thisNode.getUniqueID() == lastNode.getUniqueID()) {
                    lastNode = thisNode;
                    continue;
                }

                if (thisNode.getFloor() == parent.getCurrentFloor() &&
                        lastNode.getFloor() == parent.getCurrentFloor()) {

                    Line line = new Line(lastNode.getXcoord(), lastNode.getYcoord(),
                                         thisNode.getXcoord(), thisNode.getYcoord());
                    line.setStroke(path.getSegmentColor(waypointIndex));
                    line.setStrokeWidth(8);
                    line.setStrokeLineCap(StrokeLineCap.ROUND);
                    pathView.getChildren().add(line);

                    totalDistance += Math.sqrt(Math.pow(thisNode.getXcoord() - lastNode.getXcoord(), 2.0)
                                                + Math.pow(thisNode.getYcoord() - lastNode.getYcoord(), 2.0));
                } else if (thisNode.getFloor() != lastNode.getFloor() &&
                            (thisNode.getFloor() == parent.getCurrentFloor() ||
                            lastNode.getFloor() == parent.getCurrentFloor())) {
                    Circle circle = new Circle(20, Color.RED);

                    final NodeFloor thisFloor = thisNode.getFloor();
                    final NodeFloor lastFloor = lastNode.getFloor();
                    final Node lNode = lastNode;

                    Image upDownImg;
                    ImageView upDownImgView;

                    if(thisFloor == parent.getCurrentFloor()) {
                        if (thisFloor.ordinal() < lastFloor.ordinal()) {
                            upDownImg = ResourceManager.getInstance().getImage("/images/chevron-up.png");
                            upDownImgView = new ImageView(upDownImg);
                            upDownImgView.setLayoutX(thisNode.getXcoord());
                            upDownImgView.setLayoutY(thisNode.getYcoord());

                            circle.setCenterX(thisNode.getXcoord());
                            circle.setCenterY(thisNode.getYcoord());
                        } else {
                            upDownImg = ResourceManager.getInstance().getImage("/images/chevron-down.png");
                            upDownImgView = new ImageView(upDownImg);
                            upDownImgView.setLayoutX(lastNode.getXcoord());
                            upDownImgView.setLayoutY(lastNode.getYcoord());

                            circle.setCenterX(lastNode.getXcoord());
                            circle.setCenterY(lastNode.getYcoord());
                        }
                    } else {
                        if (thisFloor.ordinal() < lastFloor.ordinal()) {
                            upDownImg = ResourceManager.getInstance().getImage("/images/chevron-down.png");
                            upDownImgView = new ImageView(upDownImg);
                            upDownImgView.setLayoutX(lastNode.getXcoord());
                            upDownImgView.setLayoutY(lastNode.getYcoord());

                            circle.setCenterX(lastNode.getXcoord());
                            circle.setCenterY(lastNode.getYcoord());
                        } else {
                            upDownImg = ResourceManager.getInstance().getImage("/images/chevron-up.png");
                            upDownImgView = new ImageView(upDownImg);
                            upDownImgView.setLayoutX(thisNode.getXcoord());
                            upDownImgView.setLayoutY(thisNode.getYcoord());

                            circle.setCenterX(thisNode.getXcoord());
                            circle.setCenterY(thisNode.getYcoord());
                        }
                    }

                    circle.setOnMouseClicked(e -> {
                        System.out.println("FC Clicked!");
                        if(thisFloor == parent.getCurrentFloor()) {
                            parent.setFloorSelector(lastFloor);
                        } else {
                            parent.setFloorSelector(thisFloor);
                        }
                    });

                    circle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            System.out.println("Open Tooltip");
                            if (thisFloor == parent.getCurrentFloor()) {
                                showPopup(thisNode, lastFloor, event);
                            } else {
                                showPopup(lNode, thisFloor, event);
                            }
                        }
                    });

                    floorChangeView.getChildren().add(circle);
                    floorChangeView.getChildren().add(upDownImgView);
                    upDownImgView.setLayoutX(upDownImgView.getLayoutX()-18);
                    upDownImgView.setLayoutY(upDownImgView.getLayoutY()-18);
                }

                lastNode = thisNode;
            }
            waypointIndex++;
        }

        // Animate circles
        int distCont = 30;
        int totalCircles = (int)Math.ceil(totalDistance / (double)distCont);

        Node firstNode = waypointList.get(0);
        for(int i = 0; i < totalCircles; i++) {
            Circle circle = new Circle(5.0D, new Color(1, 1, 1, 0.5));
            circle.setCenterX(firstNode.getXcoord());
            circle.setCenterY(firstNode.getYcoord());

            Timeline timeline = new Timeline();
            timeline.setCycleCount(-1);
            Duration time = Duration.ZERO;
            double speed = 0.1D;

            Node lastNode = null;
            LinkedList<Node> nodesOnPath = path.getListOfAllNodes();
            for (Node node : nodesOnPath) {
                if (node.getFloor().equals(parent.getCurrentFloor())) {
                    if (lastNode != null) {
                        double distance = Math.sqrt(Math.pow(lastNode.getXcoord() - node.getXcoord(), 2.0)
                                + Math.pow(lastNode.getYcoord() - node.getYcoord(), 2.0));
                        time = time.add(new Duration(distance / speed));
                    }

                    timeline.getKeyFrames().add(new KeyFrame(time,
                            new KeyValue(circle.centerXProperty(), node.getXcoord()),
                            new KeyValue(circle.centerYProperty(), node.getYcoord())));

                    lastNode = node;
                }
            }

            Duration offset = timeline.getCycleDuration().divide((double)totalCircles);
            timeline.playFrom(offset.multiply((double)i));

            pathView.getChildren().add(circle);
        }
    }

    public Path getPath() {
       return this.currentPath;
    }

    public void addWaypoint(Node node) {
        waypointList.add(node);
    }

    public void removeWaypoint(Node node) {
        Iterator<Node> waypointIterator = waypointList.iterator();
        while(waypointIterator.hasNext()) {
            Node removedWaypoint = waypointIterator.next();
            if(removedWaypoint.getNodeID().equals(node.getNodeID())) {
                waypointIterator.remove();
                break;
            }
        }
    }

    public void reloadDisplay() {
        pathView.getChildren().clear();

        if(currentPath != null) {
            drawPath(currentPath);
        }
        for(Node node : waypointList){
            if(!MapEntity.getInstance().isNodeOnFloor(node, parent.getCurrentFloor())) {
                this.wayPointViewsMap.get(node).setVisible(false);
            }
            else {
                this.wayPointViewsMap.get(node).setVisible(true);
            }
        }
        if(kiosk.getFloor() == parent.getCurrentFloor()){
            pathView.getChildren().add(kioskLocationView);
        }
    }

    /**
     * swap the waypoints at targeted indexes
     */
    public void swapWaypoint(int index1, int index2) {
        Node temp1 = waypointList.get(index1);
        Node temp2 = waypointList.get(index2);

        WaypointView tempWaypointView1 = wayPointViewsMap.get(temp1);
        WaypointView tempWaypointView2 = wayPointViewsMap.get(temp2);

        waypointList.set(index1, temp2);
        waypointList.set(index2, temp1);

        wayPointViewsMap.get(waypointList.get(index1)).setWaypointCount(index2);
        wayPointViewsMap.get(waypointList.get(index2)).setWaypointCount(index1);

        wayPointViewsMap.put(temp1, tempWaypointView1);
        wayPointViewsMap.put(temp2, tempWaypointView2);

        for(Node node : waypointList){
            if(!MapEntity.getInstance().isNodeOnFloor(node, parent.getCurrentFloor())) {
                this.wayPointViewsMap.get(node).setVisible(false);
            }
            else {
                this.wayPointViewsMap.get(node).setVisible(true);
            }
        }
    }

    /**
     * give the starting waypoint in path(first one)
     */
    public Node getStartWaypoint() {
        if(this.waypointList.size() != 0) {
            return waypointList.get(0);
        }
        else return null;
    }

    /**
     * return true if there's path displaying
     */
    public boolean isPathShowing() {
        return this.currentPath != null;
    }

    /**
     * return the text direction for targeted index
     */
    public LinkedList<String> getDirectionForWaypointIndex(int i) {
        if(currentPath != null && i < currentPath.getDirectionsList().size()) {
            return currentPath.getDirectionsList().get(i);
        }
        else return null;
    }
}
