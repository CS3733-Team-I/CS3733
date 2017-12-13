package controller.map;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import utility.ResourceManager;
import utility.node.NodeFloor;



public class PreviewMap extends StackPane {
    NodeFloor floor;
    ImageView mapImage;

    public PreviewMap(NodeFloor floor) {
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
    }
}
