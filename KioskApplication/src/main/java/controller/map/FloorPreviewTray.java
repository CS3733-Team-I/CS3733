package controller.map;

import database.objects.Node;
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

import java.util.LinkedList;

/**
 * Given a list of floors, displays previews of those floors.
 */
public class FloorPreviewTray extends ScrollPane {
    private ObservableList<PreviewMap> previews;
    private static final double MAP_SPACING = 20;  //Distance between PreviewMaps
    HBox tray;

    public FloorPreviewTray() {
        this.previews = FXCollections.observableArrayList();
        this.setPrefHeight(220);
        this.tray = new HBox();
        this.tray.setAlignment(Pos.CENTER);
        this.setContent(this.tray);

        this.setFitToHeight(true);
        this.setFitToWidth(true);

        this.previews.addListener((ListChangeListener<PreviewMap>) listener -> {
            while(listener.next()){
                if(listener.wasAdded()){
                    for(PreviewMap preview: listener.getAddedSubList()){
                        //If this isn't the first floor in the tray, put an arrow before it.
                        if(!this.tray.getChildren().isEmpty()) {
                            ResourceManager resourceManager = ResourceManager.getInstance();
                            Image arrowImage = resourceManager.getImage("/images/icons/arrow-button.png");
                            ImageView arrow = new ImageView(arrowImage);
                            arrow.setRotate(90);    //Point right
                            HBox.setMargin(arrow, new Insets(this.MAP_SPACING / 2));
                            this.tray.getChildren().add(arrow);
                        }
                        HBox.setMargin(preview, new Insets(this.MAP_SPACING / 2));
                        this.tray.getChildren().add(preview);//TODO: add floor labels
                    }
                }
                else if(listener.wasRemoved()) {
                    for (PreviewMap preview : listener.getRemoved()) {
                        //If this is the only element, just remove it.
                        if(this.tray.getChildren().size() == 1){
                            this.tray.getChildren().remove(preview);
                            continue;
                        }
                        //If preview is not the only element, there's an arrow to remove.
                        int previewIndex = this.tray.getChildren().indexOf(preview);
                        //If preview is the last element, remove it and the arrow before it.
                        if(this.tray.getChildren().size() == (previewIndex + 1)){
                            this.tray.getChildren().remove(previewIndex--);
                            this.tray.getChildren().remove(previewIndex);
                            continue;
                        }
                        //Otherwise, remove preview and the arrow after it.
                        this.tray.getChildren().remove(previewIndex);
                        //Removing preview should shift the arrow into preview's index.
                        this.tray.getChildren().remove(previewIndex);
                    }
                }
                else{
                    //list updated or permutated
                }
            }
        });//TODO: fix listener (add previews to HBox)
    }

    /**
     * Remove all floors from the display.
     */
    public void clearPreviews(){
        this.previews.clear();
    }

    /**
     * Splits the path into continuous sections on single floors.
     * Some clarification: a "segment" is a path between two waypoints; a "section" is a continuous section of a
     * segment in which all nodes are on the same floor.  A floorGroup is a continuous set of sections that are all
     * on the same floor.
     */
    public void generatePreviews(Path path, MapController mapController) {
        LinkedList<Node> waypoints = path.getWaypoints();
        LinkedList<Node> nodes = path.getListOfAllNodes();
        NodeFloor currentFloor = path.getWaypoints().getFirst().getFloor();

        int numNodes = nodes.size();
        int segmentIndex = 0;

        LinkedList<Node> nodesInSection = new LinkedList<>();
        PreviewMap currentPreview = new PreviewMap(currentFloor, mapController); //Path sections on the current floor

        for (int nodeIndex = 0; nodeIndex < numNodes; nodeIndex++) {
            Node currentNode = nodes.get(nodeIndex);

            //If we've changed floors, end the section and move to a new floor group.
            if (!currentNode.getFloor().equals(currentFloor)) {
                //Use the accumulated nodes to build a path section, then clear the list for the next section.
                currentPreview.addPathSection(new PathSection(nodesInSection, path.getSegmentColor(segmentIndex)));
                nodesInSection = new LinkedList<>();

                //Use the accumulated sections to create a Preview, then clear the list for the next preview.
                this.previews.add(currentPreview);
                currentFloor = currentNode.getFloor();  //update floor
                currentPreview = new PreviewMap(currentFloor, mapController);
            }
            nodesInSection.add(currentNode);

            //If we've reached the next waypoint, we're at the end of a section.
            if (currentNode.equals(waypoints.get(segmentIndex + 1))) {
                //We've reached the end of the segment, so split into a new section.
                //Use the accumulated nodes to build a path section, then clear the list for the next section.
                currentPreview.addPathSection(new PathSection(nodesInSection, path.getSegmentColor(segmentIndex++)));
                nodesInSection = new LinkedList<>();
            }
        }

        //We've reached the end of the path.
        //Add remaining nodes to a section, any remaining sections to a preview, & any remaining previews to the list.
        if (!nodesInSection.isEmpty())
            currentPreview.addPathSection(new PathSection(nodesInSection, path.getSegmentColor(waypoints.size() - 1)));

        if (!currentPreview.getPathSections().isEmpty())
            this.previews.add(currentPreview);
    }
}