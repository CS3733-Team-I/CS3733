package controller;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
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
    }
}
