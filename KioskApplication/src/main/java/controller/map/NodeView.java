package controller.map;

import database.objects.Node;
import entity.MapEntity;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utility.node.NodeSelectionType;
import utility.node.NodeType;

import java.io.IOException;

public class NodeView extends StackPane {

    NodesEdgesView parent;

    Node node;
    NodeSelectionType selectionType;
    boolean editable;

    @FXML private StackPane container;
    @FXML private Circle circle;
    @FXML private ImageView imageView;

    public NodeView(NodesEdgesView parent, Node node, boolean editable) {
        this.parent = parent;

        this.node = node;
        this.selectionType = NodeSelectionType.NORMAL;
        this.editable = editable;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NodeView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        circle.setOnMouseClicked(e -> {
            parent.mapNodeClicked(node);
        });

        // Set X and Y
        container.setLayoutX(node.getXcoord() - circle.getRadius());
        container.setLayoutY(node.getYcoord() - circle.getRadius());

        // Show tooltip based on the current tab
        if (this.editable) {
            Tooltip nodeInfo = new Tooltip(node.getLongName() + "\n\nConnections:\n");
            for (Node connectingNode : MapEntity.getInstance().getConnectedNodes(node)) {
                nodeInfo.setText(nodeInfo.getText() + connectingNode.getLongName() + "\n");
            }
            Tooltip.install(this.circle, nodeInfo);
        } else {
            Tooltip nodeInfo = new Tooltip(node.getLongName());
            Tooltip.install(this.circle, nodeInfo);
        }

        this.circle.setOnDragDetected(this::dragDetected);
        this.circle.setOnDragOver(this::dragOver);
        this.circle.setOnDragDropped(this::dragDropped);
        this.circle.setOnDragDone(this::dragDone);
        this.circle.setOnDragEntered(this::dragEntered);
        this.circle.setOnDragExited(this::dragExited);
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
        if (this.editable) {
            if (node.getNodeType() == NodeType.ELEV) {
                // do something
                // parent.controllers.get(ApplicationScreen.MAP_BUILDER).showFloors();
            }

            Dragboard db = container.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(node.getNodeID());
            db.setContent(content);

            circle.setFill(Color.GREEN);
            e.consume();
        }
    }

    private void dragOver(DragEvent event) {
        if (this.editable && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.LINK);
            event.consume();
        }
    }

    private void dragDone(DragEvent e) {
        if (this.editable) {
            setSelectionType(selectionType);
            parent.mapNodeClicked(node);
            e.consume();
        }
    }

    private void dragDropped(DragEvent e) {
        if (this.editable) {
            // TODO Figure out how to broadcast a message about drag/drop being done
            // parent.controllers.get(ApplicationScreen.MAP_BUILDER).addConnectionByNodes(nodeView.getAccessibleText(), event.getDragboard().getString());
            setSelectionType(selectionType);

            e.setDropCompleted(true);
            e.consume();
        }
    }
    private void dragEntered(DragEvent event) {
        if (this.editable && event.getDragboard().hasString()) {
            if (event.getDragboard().getString().equals(node.getNodeID())) {
                return;
            }

            circle.setFill(Color.DARKGREEN);
            event.consume();
        }
    }

    private void dragExited(DragEvent event) {
        if (this.editable) {
            if (event.getDragboard().hasString()) {
                if (event.getDragboard().getString().equals(node.getNodeID())) {
                    return;
                }
            }

            setSelectionType(selectionType);
            event.consume();
        }
    }
}
