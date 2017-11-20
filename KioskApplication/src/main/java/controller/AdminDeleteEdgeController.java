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
import utility.NodeFloor;

import java.io.IOException;

public class AdminDeleteEdgeController extends ScreenController {

    AdminDeleteEdgeController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML private TextField node1ID;
    @FXML private TextField node2ID;
    @FXML private TextField edgeID;
    @FXML private Button submitButton;
    @FXML private Label errorMsg;

    int lastChanged = 1;

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
        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
    }

    @FXML
    void onSubmitClicked() throws IOException{
        Edge edge = new Edge(edgeID.getText(), node1ID.getText(), node2ID.getText());
        Edge edge2 = new Edge(node2ID.getText()+"_"+node1ID.getText(), node2ID.getText(), node1ID.getText());

        if (MapEntity.getInstance().getEdge(edge.getEdgeID()) != null) { // Check for edge version 1
            MapEntity.getInstance().removeEdge(edge.getEdgeID());
            System.out.println("Removed Edge: " + edge.getEdgeID());

            getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
        } else if (MapEntity.getInstance().getEdge(edge2.getEdgeID()) != null) { // Check for edge verson 2
            MapEntity.getInstance().removeEdge(edge2.getEdgeID());
            System.out.println("Removed Edge: " + edge2.getEdgeID());

            getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
        } else {
            System.out.println("Edge doesn't exist in the database: " + edge.getEdgeID() + ", " + edge2.getEdgeID());
        }
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/deleteEdge.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) { }

    @Override
    public void onMapNodeClicked(Node node) { }

    @Override
    public void onMapEdgeClicked(Edge edge) {
        node1ID.setText(edge.getNode1ID());
        node2ID.setText(edge.getNode2ID());
        edgeID.setText(edge.getEdgeID());
    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        node1ID.setText("");
        node2ID.setText("");
        edgeID.setText("");
        errorMsg.setText("");
    }
}
