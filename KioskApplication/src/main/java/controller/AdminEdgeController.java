package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;

import java.io.IOException;


public class AdminEdgeController extends ScreenController {

    AdminEdgeController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    private boolean isDelete = false;
    @FXML private TextField node1ID;
    @FXML private TextField node2ID;
    @FXML private TextField edgeID;
    @FXML private Button submitButton;
    @FXML private Label errorMsg;

    @FXML
    void updateEdgeID() throws IOException{
        updateEdgeIDonP();
    }

    void updateEdgeIDonP(){
        System.out.println("Node1: " + (node1ID.getText().toString().isEmpty()) + " Node2: " + node2ID.getText()); // ?
        if(!node1ID.getText().toString().isEmpty() && !node2ID.getText().toString().isEmpty()) // ?
            edgeID.setText(node1ID.getText() + "_" + node2ID.getText());
        else
            edgeID.setText("Enter Nodes");
    }

    @FXML
    void onBackPressed() throws IOException{
        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
    }

    @FXML
    void onSubmitClicked() throws IOException{
        if (isDelete) {
            // Delete edge
            Edge edge = new Edge(edgeID.getText(), node1ID.getText(), node2ID.getText());
            Edge edge2 = new Edge(node2ID.getText()+"_"+node1ID.getText(), node2ID.getText(), node1ID.getText());

            if (MapEntity.getInstance().getEdge(edge.getEdgeID()) != null) { // Check for edge version 1
                MapEntity.getInstance().removeEdge(edge.getEdgeID());
                System.out.println("Removed Edge: " + edge.getEdgeID());
            } else if (MapEntity.getInstance().getEdge(edge2.getEdgeID()) != null) { // Check for edge verson 2
                MapEntity.getInstance().removeEdge(edge2.getEdgeID());
                System.out.println("Removed Edge: " + edge2.getEdgeID());
            } else {
                System.out.println("Edge doesn't exist in the database: " + edge.getEdgeID() + ", " + edge2.getEdgeID());
            }
            //After Delete
            isDelete = false;
            submitButton.setText("Add");
            resetScreen();
        }
        else {
            // ADD EDGE
            // Create Edge
            Edge edge = new Edge(edgeID.getText(), node1ID.getText(), node2ID.getText());
            // Check to see if the edge Exists (!!bidirectional!!)
            if (MapEntity.getInstance().getEdge(node1ID.getText() + "_" + node2ID.getText()) == null && MapEntity.getInstance().getEdge(node2ID.getText() + "_" + node1ID.getText()) == null) {
                // If not then add edge
                MapEntity.getInstance().addEdge(edge);
                System.out.println("Added Edge: " + edge.getEdgeID());
                resetScreen();
            } else {
                System.out.println("Edge already in the database: " + edge.getEdgeID());

            }
        }
    }

    int lastChanged = 1;
    @Override
    public void onMapNodeClicked(Node node){
        if(isDelete) {
            isDelete = false;
            submitButton.setText("Add");
            resetScreen();
        }

        if (node1ID.getText() != node.getNodeID() && node2ID.getText() != node.getNodeID()) { // If node is not already one of the ones selected
            if (node1ID.getText().toString().isEmpty()) {
                node1ID.setText(node.getNodeID());
            } else if (node2ID.getText().toString().isEmpty()) {
                node2ID.setText(node.getNodeID());
            } else { // If both already have a node ID filled in
                // Select last filled one
                if (lastChanged == 1) { // If 2 was filled more recently
                    node1ID.setText(node.getNodeID());
                    lastChanged = 2;
                } else if (lastChanged == 2) { // If 1 was filled more recently
                    node2ID.setText(node.getNodeID());
                    lastChanged = 1;
                }
            }
        }
        updateEdgeIDonP();
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/edgeSidebar.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapEdgeClicked(Edge edge) {
        node1ID.setText(edge.getNode1ID());
        node2ID.setText(edge.getNode2ID());
        edgeID.setText(edge.getEdgeID());
        isDelete = true;
        submitButton.setText("Delete");
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void onMapLocationClicked(Point2D location) {
        // TODO disable location markers
    }

    @Override
    public void resetScreen() {
        node1ID.setText("");
        node2ID.setText("");
        edgeID.setText("");
        errorMsg.setText("");
        // TODO refresh edges
    }
}
