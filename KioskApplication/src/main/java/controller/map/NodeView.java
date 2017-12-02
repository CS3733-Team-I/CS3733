package controller.map;

import database.objects.Node;
import entity.MapEntity;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import utility.node.NodeType;

import javax.swing.text.html.ImageView;

public class NodeView {

    Node node;
    boolean editable;

    @FXML private StackPane container;
    @FXML private Circle circle;
    @FXML private ImageView imageView;

    NodesEdgesView parent;

    public NodeView(NodesEdgesView parent, Node node, boolean editable) {
        this.parent = parent;
        this.node = node;
        this.editable = editable;
    }

    @FXML
    private void initialize() {
        // TODO find any way to do this better, this is a hack
        this.container.setAccessibleText(node.getXyz());
        this.container.setAccessibleHelp(node.getNodeType().toString());

        // Show tooltip based on the current tab
        if (this.editable) {
            Tooltip nodeInfo = new Tooltip(node.getLongName() + "\n\nConnections:\n");
            for (Node connectingNode : MapEntity.getInstance().getConnectedNodes(node)) {
                nodeInfo.setText(nodeInfo.getText() + connectingNode.getLongName() + "\n");
            }
            Tooltip.install(this.container, nodeInfo);
        } else {
            Tooltip nodeInfo = new Tooltip(node.getLongName());
            Tooltip.install(this.container, nodeInfo);
        }
    }

    @FXML
    private void dragDetected(MouseEvent e) {
        if (this.editable) {
            // TODO do this in some different way, parent.controllers cannot be public
            /*if (!parent.controllers.get(ApplicationScreen.MAP_BUILDER).isNewNodeEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error creating connection");
                alert.setHeaderText("Save new node before creating connections");
                alert.setContentText("Press save button to save any new node before connecting them with other nodes.");
                alert.showAndWait();
                event.consume();
                return;
            }*/

            if (node.getNodeType() == NodeType.ELEV) {
                // do something
                // parent.controllers.get(ApplicationScreen.MAP_BUILDER).showFloors();
            }

            Dragboard db = container.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(node.getNodeID());
            db.setContent(content);

            circle.setFill(Color.GREEN);
        }
    }

    @FXML
    private void dragDone(DragEvent e) {
        circle.setFill(Color.GRAY);

        parent.mapNodeClicked(node);
    }

    @FXML
    private void dragDropped(DragEvent e) {
        // TODO Figure out how to broadcast a message about drag/drop being done
        // parent.controllers.get(ApplicationScreen.MAP_BUILDER).addConnectionByNodes(nodeView.getAccessibleText(), event.getDragboard().getString());
    }

    @FXML
    private void dragEntered(DragEvent event) {
        if (event.getDragboard().hasString()) {
            if (event.getDragboard().getString().equals(node.getNodeID())) {
                return;
            }

            circle.setFill(Color.DARKGREEN);
        }
    }

    @FXML
    private void dragExited(DragEvent event) {
        if (event.getDragboard().hasString()) {
            if (event.getDragboard().getString().equals(node.getNodeID())) {
                return;
            }
        }

        circle.setFill(Color.GRAY);
    }

    @FXML
    private void dragOver(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.LINK);
        }
    }
}
