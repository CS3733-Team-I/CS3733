package controller.map;

import database.objects.Node;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.util.LinkedList;


public class PreviewMap extends StackPane {
    NodeFloor floor;
    ImageView mapImage;
    LinkedList<PathSection> pathSections;

    public PreviewMap(NodeFloor floor) {
        this.pathSections = new LinkedList<>();
        this.floor = floor;
        ResourceManager resourceManager = ResourceManager.getInstance();
        this.mapImage = new ImageView();
        this.mapImage.setImage(resourceManager.getImage(this.floor.toImagePath()));
        this.getChildren().add(this.mapImage);
        Rectangle2D viewport = new Rectangle2D(2000,2000,1600,900);
        this.mapImage.setSmooth(true);
        this.mapImage.setViewport(viewport);
        this.mapImage.setPreserveRatio(true);
        this.mapImage.setFitHeight(200);
        this.setAlignment(Pos.CENTER);

        //If the map image is clicked, change the main map over to this map's floor
        this.mapImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //TODO: tell the MapController to change floors
                //I think I this may actually need to be an EventFilter in the MapController.
                //Need to figure out how to access PreviewMap.floor from within this handler.
            }
        });

        this.mapImage.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //TODO: add mouseover effect (probably a DropShadow)
            }
        });
        this.mapImage.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //TODO: remove mouseover effect when mouse is no longer hovering
            }
        });

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

    public void addPathSection(PathSection pathSection) {
        this.pathSections.add(pathSection); //TODO: actually draw path & resize to display
    }

    public LinkedList<PathSection> getPathSections() {
        return this.pathSections;
    }
}
