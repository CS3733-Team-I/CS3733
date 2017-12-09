package controller.map;

import database.objects.Node;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utility.ResourceManager;
import utility.node.NodeSelectionType;
import utility.node.NodeType;

public class NodeView extends StackPane {

    NodesEdgesView parent;

    Node node;
    NodeSelectionType selectionType;
    boolean editable;

    private Circle circle;
    private ImageView imageView;

    public NodeView(NodesEdgesView parent, Node node, boolean editable) {
        this.parent = parent;

        this.node = node;
        this.selectionType = NodeSelectionType.NORMAL;
        this.editable = editable;

        this.setPrefWidth(28);
        this.setPrefHeight(28);

        circle = new Circle();
        circle.setRadius(14);
        circle.setStrokeWidth(3);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.GRAY);

        imageView = new ImageView();

        circle.setOnMouseClicked(e -> {
            parent.mapNodeClicked(node);
        });

        // Set X and Y
        this.setLayoutX(node.getXcoord() - circle.getRadius());
        this.setLayoutY(node.getYcoord() - circle.getRadius());

        // Show tooltip based on the current tab
        if (this.editable) {
            Tooltip nodeInfo = new Tooltip(node.getLongName() + "\nID: " + node.getUniqueID() + "\n\nConnections:\n");
            for (Node connectingNode : MapEntity.getInstance().getConnectedNodes(node)) {
                nodeInfo.setText(nodeInfo.getText() + connectingNode.getLongName() + "\n");
            }
            Tooltip.install(this.circle, nodeInfo);
        } else {
            Tooltip nodeInfo = new Tooltip(node.getLongName() + "\nID: " + node.getUniqueID());
            Tooltip.install(this.circle, nodeInfo);
        }

        this.circle.setOnDragDetected(this::dragDetected);
        this.circle.setOnDragOver(this::dragOver);
        this.circle.setOnDragEntered(this::dragEntered);
        this.circle.setOnDragExited(this::dragExited);
        this.circle.setOnDragDropped(this::dragDropped);

        this.getChildren().addAll(circle, imageView);
    }

    /**
     * Set the selection type of the node and update the view to match
     * @param type the selection type
     */
    public void setSelectionType(NodeSelectionType type) {
        this.selectionType = type;
        switch (type) {
            case SELECTED:
                circle.setFill(Color.BLUE);
                break;
            case CHANGED:
                circle.setFill(Color.RED);
                break;
            case NORMAL:
                circle.setFill(Color.GRAY);
                break;
            case SELECTEDANDCHANGED:
                circle.setFill(Color.PURPLE);
                break;
            case NEW:
                circle.setFill(Color.YELLOW);
                break;
        }
    }

    /**
     * Get the current selection type
     * @return selection type
     */
    public NodeSelectionType getSelectionType() {
        return selectionType;
    }

    private void dragDetected(MouseEvent e) {
        System.out.println("DRAG DETECTED");
        if (this.editable) {
            if (node.getNodeType() == NodeType.ELEV) {
                // do something
                // parent.controllers.get(ApplicationScreen.MAP_BUILDER).showFloors();
            }

            Dragboard db = this.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString(Integer.toString(this.node.getUniqueID()));
            db.setContent(content);

            circle.setFill(Color.GREEN);
            e.consume();
        }
    }

    private void dragOver(DragEvent event) {
        System.out.println("DRAG OVER");
        if (this.editable && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        }
    }

    private void dragDone(DragEvent e) {
        System.out.println("DRAG DONE");
        if (this.editable) {
            setSelectionType(selectionType);
            parent.mapNodeClicked(node);
            e.consume();
        }

    }

    private void dragDropped(DragEvent e) {
        System.out.println("DRAG DROPPED");
        if (this.editable) {
            setSelectionType(selectionType);

            boolean success = false;
            if(e.getDragboard().hasString()){
                success = true;

                System.out.println("ADDING CONNECTION: " + e.getDragboard().getString() +'_'+ node.getNodeID());

                parent.nodesConnected(Integer.toString(this.node.getUniqueID()), e.getDragboard().getString());
            }
            e.setDropCompleted(success);
            e.consume();
        }
    }

    private void dragEntered(DragEvent event) {
        System.out.println("DRAG ENTERED");
        if (this.editable && event.getDragboard().hasString()) {
            if (event.getDragboard().getString().equals(node.getNodeID())) {
                return;
            }

            circle.setFill(Color.DARKGREEN);
            event.consume();
        }
    }

    private void dragExited(DragEvent event) {
        System.out.println("DRAG EXITED");
        if (this.editable) {
            if (event.getDragboard().hasString()) {
                if (event.getDragboard().getString().equals(node.getNodeID())) {
                    return;
                }
            }
            // Skip if edge has been added
            circle.setFill(Color.IVORY);

            setSelectionType(selectionType);
            event.consume();
        }
    }

    /**
     * sets an image for default location
     * @param node
     */
    public void setImage(Node node){
       Image img = ResourceManager.getInstance().getImage("/images/crosshairs-gps2x.png");
        imageView.setImage(img);
        imageView.setX(node.getXcoord());
        imageView.setY(node.getYcoord());
    }
}
