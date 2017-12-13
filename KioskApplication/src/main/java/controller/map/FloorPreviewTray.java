package controller.map;

import entity.Path;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.util.HashMap;

/**
 * Given a list of floors, displays previews of those floors.
 */
public class FloorPreviewTray extends ScrollPane {

    private static final double MAP_SPACING = 20;  //Distance between PreviewMaps
    ObservableList<NodeFloor> displayedFloors;  //The floors to display
    HashMap<NodeFloor, PreviewMap> floorMap;    //A map to connect the name of a floor to that floor's display object
    HBox tray;

    public FloorPreviewTray() {
        this.setPrefHeight(220);
        this.displayedFloors = FXCollections.observableArrayList();
        this.tray = new HBox();
        this.tray.setAlignment(Pos.CENTER);
        this.setContent(this.tray);
//        this.tray.setStyle("-fx-background-color: red");  //makes HBox visible for debugging
        this.setFitToHeight(true);
        this.setFitToWidth(true);

        this.floorMap = new HashMap<>();

        this.displayedFloors.addListener((ListChangeListener<NodeFloor>) listener -> {
            while(listener.next()){
                if(listener.wasPermutated()){
                    //do stuff
                }
                else if(listener.wasUpdated()){
                    //do stuff
                }
                else if(listener.wasRemoved()) {
                    for (NodeFloor floor : listener.getRemoved()) {
                        int floorIndex = this.tray.getChildren().indexOf(floorMap.get(floor));
                        //If there's more than one
                        this.tray.getChildren().remove(floorMap.get(floor));
                        this.floorMap.remove(floor);
                        //If floor wasn't the first element , then the element right before it is an arrow that
                        //is now pointing to nothing.  That needs to get removed, too.
                        if(floorIndex != 0)
                            this.tray.getChildren().remove(floorIndex - 1);
                            //If floor was the 1st element, the extra arrow is afterwards (if there were multiple elements)
                        else if((floorIndex == 0) && !this.tray.getChildren().isEmpty())
                            this.tray.getChildren().remove(floorIndex);//since floor was removed, arrow should now be 0.
                    }
                }
                else{
                    for(NodeFloor floor: listener.getAddedSubList()){
                        //If this isn't the first floor in the tray, put an arrow before it.
                        if(!this.tray.getChildren().isEmpty()) {
                            ResourceManager resourceManager = ResourceManager.getInstance();
                            Image arrowImage = resourceManager.getImage("/images/icons/arrow-button.png");
                            ImageView arrow = new ImageView(arrowImage);
                            arrow.setRotate(90);    //Point right
                            HBox.setMargin(arrow, new Insets(this.MAP_SPACING / 2));
                            this.tray.getChildren().add(arrow);
                        }
                        //TODO: make sure the order of the maps is always correct.
                        PreviewMap map = new PreviewMap(floor);
                        HBox.setMargin(map, new Insets(this.MAP_SPACING / 2));
                        this.floorMap.put(floor, map);
                        this.tray.getChildren().add(map);
                    }
                }
            }
        });
    }

    /**
     * Add a new floor to the floors being displayed in the preview tray
     * @param floor
     */
    public void addFloor(NodeFloor floor){
        this.displayedFloors.add(floor);
    }

    /**
     * Stop remove a floor from the preview display.
     * @param floor
     */
    public void removeFloor(NodeFloor floor){
        if(this.displayedFloors.contains(floor))
            this.displayedFloors.remove(floor);
    }

    /**
     * Remove all floors from the display.
     */
    public void clearTray(){
        this.displayedFloors.clear();
    }

    public void generatePreviews(Path path) {
        this.displayedFloors.addAll(path.getFloorsOccurrences());
    }
}