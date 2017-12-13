package controller.map;

import database.objects.Node;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import utility.ResourceManager;
import utility.node.NodeFloor;
import java.util.LinkedList;


public class PreviewMap extends AnchorPane {
    NodeFloor floor;
    ImageView mapImage;
    LinkedList<PathSection> pathSections;
    MapController mapController;
    LinkedList<Node> allNodes;
    public static final int PREVIEW_WIDTH = 356;
    public static final int PREVIEW_HEIGHT = 200;



    public PreviewMap(NodeFloor floor, MapController mapController) {
        this.allNodes = new LinkedList<>();
        this.mapController = mapController;
        this.pathSections = new LinkedList<>();
        this.floor = floor;
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.mapImage = new ImageView();
        this.mapImage.setImage(resourceManager.getImage(this.floor.toImagePath()));
        this.getChildren().add(this.mapImage);
        this.mapImage.setSmooth(true);
//        this.mapImage.setPreserveRatio(true);
        this.mapImage.setFitHeight(200);
        this.mapImage.setFitWidth(356);

        //If the map image is clicked, change the main map over to this map's floor
        this.mapImage.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            mapController.setFloorSelector(this.floor);
            mapController.zoomOnSelectedNodes(this.allNodes);
        });

        this.mapImage.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(10.0);
            this.setEffect(dropShadow);
        });
        this.mapImage.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            this.setEffect(null);
        });
    }

    public void addPathSection(PathSection pathSection) {
        this.pathSections.add(pathSection);
        for(Node node: pathSection.getNodes())
            this.allNodes.add(node);
    }

    public LinkedList<PathSection> getPathSections() {
        return this.pathSections;
    }

    public void drawSections(){
        if(this.pathSections.isEmpty())
            return; //Nothing to do.
        this.setViewPort();
        //Calculate scaling factors
        double xScale = this.PREVIEW_WIDTH / this.mapImage.getViewport().getWidth();
        double yScale = this.PREVIEW_HEIGHT / this.mapImage.getViewport().getHeight();
        double xOffset = this.mapImage.getViewport().getMinX();
        double yOffset = this.mapImage.getViewport().getMinY();

        //First, draw lines.
        LinkedList<Node> usedNodes = new LinkedList<>();
        Node lastNode = this.pathSections.getFirst().getNodes().getFirst();
        for(PathSection section: this.pathSections){
            for(Node node: section.getNodes()){
                if(node.equals(lastNode))
                    continue;
                Line line = new Line((lastNode.getXcoord() - xOffset) * xScale, (lastNode.getYcoord() - yOffset) * yScale,
                        (node.getXcoord() - xOffset) * xScale, (node.getYcoord() - yOffset) * yScale);
                line.setStroke(section.getColor());
                line.setStrokeWidth(4);
                this.getChildren().add(line);
                lastNode = node;
            }
        }
        for(PathSection section: this.pathSections) {
            Circle waypointIndicator = new Circle(6);
            waypointIndicator.setFill(Color.RED);
            waypointIndicator.setCenterX((section.getNodes().getFirst().getXcoord() - xOffset) * xScale);
            waypointIndicator.setCenterY((section.getNodes().getFirst().getYcoord() - yOffset) * yScale);
            this.getChildren().add(waypointIndicator);
        }
        Circle waypointIndicator = new Circle(6);
        waypointIndicator.setFill(Color.RED);
        waypointIndicator.setCenterX((this.pathSections.getLast().getNodes().getLast().getXcoord() - xOffset) * xScale);
        waypointIndicator.setCenterY((this.pathSections.getLast().getNodes().getLast().getYcoord() - yOffset) * yScale);
        this.getChildren().add(waypointIndicator);
        //Then, draw waypoints

        //Then, TODO: add animation.
//        int waypointIndex = 0;
//        for (Node node : waypoints) {
//            Node lastNode = node;
//            for (Node thisNode : path.get().getNodesInSegment(node)) {
//                // Don't draw a line between the same nodes
//                if (thisNode.getUniqueID() != lastNode.getUniqueID() &&
//                        thisNode.getFloor() == mapController.getCurrentFloor() &&
//                        lastNode.getFloor() == mapController.getCurrentFloor()) {
//
//                    Line line = new Line(lastNode.getXcoord(), lastNode.getYcoord(),
//                            thisNode.getXcoord(), thisNode.getYcoord());
//                    line.setStroke(path.get().getSegmentColor(waypointIndex));
//                    line.setStrokeWidth(2);
//                    pathPane.getChildren().add(line);
//                }
//                lastNode = thisNode;
//            }
//            waypointIndex++;
//        }
    }

    private void setViewPort(){
        if(this.pathSections.isEmpty())
            return; //Nothing to do.
        //Set zoom.
        //Get the coordinates of a random point along the path as a starting point.
        int xMin = this.pathSections.getFirst().getNodes().getFirst().getXcoord();
        int xMax = this.pathSections.getFirst().getNodes().getFirst().getXcoord();
        int yMin = this.pathSections.getFirst().getNodes().getFirst().getYcoord();
        int yMax = this.pathSections.getFirst().getNodes().getFirst().getYcoord();
        //Now, go through all the nodes on the path and find the extremes for each coordinate.
        for(PathSection section: this.pathSections){
            for(Node node: section.getNodes()){
                xMin = (node.getXcoord() < xMin) ? node.getXcoord() : xMin;
                xMax = (node.getXcoord() > xMax) ? node.getXcoord() : xMax;
                yMin = (node.getYcoord() < yMin) ? node.getYcoord() : yMin;
                yMax = (node.getYcoord() > yMax) ? node.getYcoord() : yMax;
            }
        }
        //Now, use the min/max values to calculate the viewport size needed to display the whole path.
        int width = xMax - xMin;
        int height = yMax - yMin;

        //calculate aspect ratio
        int resizeX = (int)((16.0 / 9.0) * height);
        int resizeY = (int)((9.0 / 16.0) * width);
        int xDiff = resizeX - width;
        int yDiff = resizeY - height;

        if(xDiff > 0) {
            xMin = ((xMin - (xDiff / 2)) > 0) ? (xMin - (xDiff / 2)) : 0;
            xMax = ((xMax + (xDiff / 2)) < this.mapImage.getImage().getWidth()) ? (xMax + (xDiff / 2)) : (int)this.mapImage.getImage().getWidth();
            width = xMax - xMin;
        }
        else {
            yMin = ((yMin - (yDiff / 2)) > 0) ? (yMin - yDiff / 2) : 0;
            yMax = ((yMax + (yDiff / 2)) < this.mapImage.getImage().getHeight()) ? (yMax + (yDiff / 2)) : (int)this.mapImage.getImage().getHeight();
            height = yMax - yMin;
        }
        //Add some padding around the edges, being careful to stay within the image bounds
        xMin = (xMin >= 50) ? (xMin - 50) : 0;
        yMin = (yMin >= 50) ? (yMin - 50) : 0;
        width = ((this.mapImage.getImage().getWidth() - xMax) >= 100) ? (width + 100) : width;
        height = ((this.mapImage.getImage().getHeight() - yMax) >= 100) ? (height + 100) : height;

        //Now, reset the viewport.
        this.mapImage.setViewport(new Rectangle2D(xMin, yMin, width, height));
    }
}