package controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import utility.node.NodeFloor;

import java.util.HashMap;

/**
 * Given a list of floors, displays previews of those floors.
 */
public class FloorPreviewTray extends ScrollPane {

    ObservableList<NodeFloor> displayedFloors;  //The floors to display
    HashMap<NodeFloor, PreviewMap> floorMap;    //A map to connect the name of a floor to that floor's display object
    HBox tray;

    public FloorPreviewTray() {
        this.displayedFloors = FXCollections.observableArrayList();
        this.tray = new HBox();
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
                        this.tray.getChildren().remove(floorMap.get(floor));
                        this.floorMap.remove(floor);
                    }
                }
                else{
                    for(NodeFloor floor: listener.getAddedSubList()){
                        this.floorMap.put(floor, new PreviewMap(floor));
                        this.tray.getChildren().add(floorMap.get(floor));
                    }
                }
            }
        });
    }

    public void addFloor(NodeFloor floor){
        this.displayedFloors.add(floor);
    }
}
