package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.MapEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

import static controller.AdminWindowController.SidebarType.SIDEBAR_MENU;


public class AdminDeleteEdgeController {

    AdminWindowController parent;

    AdminDeleteEdgeController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML
    private TextField node1ID;

    @FXML
    private TextField node2ID;

    @FXML
    private TextField edgeID;

    @FXML
    private Button submitButton;

    @FXML
    private Label errorMsg;

    @FXML
    void updateEdgeID() throws IOException{
        System.out.println("Node1: " + (node1ID.getText().toString().isEmpty()) + " Node2: " + node2ID.getText()); // ?
        if(!node1ID.getText().toString().isEmpty() && !node2ID.getText().toString().isEmpty()) // ?
            edgeID.setText(node1ID.getText() + "_" + node2ID.getText());
        else
            edgeID.setText("Enter Nodes");
    }

    void updateEdgeIDonP(){
        System.out.println("Node1: " + (node1ID.getText().toString().isEmpty()) + " Node2: " + node2ID.getText()); // ?
        if(!node1ID.getText().toString().isEmpty() && !node2ID.getText().toString().isEmpty()) // ?
            edgeID.setText(node1ID.getText() + "_" + node2ID.getText());
        else
            edgeID.setText("Enter Nodes");
    }

    int lastChanged = 1;
    public void onMapNodePressed(Node node){ }

    public void onMapEdgeClicked(Edge edge) {
        node1ID.setText(edge.getNode1ID());
        node2ID.setText(edge.getNode2ID());
        edgeID.setText(edge.getEdgeID());
    }

    @FXML
    void onBackPressed() throws IOException{
        this.parent.switchTo(SIDEBAR_MENU);
    }

    @FXML
    void onSubmitClicked() throws IOException{
        Edge edge = new Edge(edgeID.getText(), node1ID.getText(), node2ID.getText());
        Edge edge2 = new Edge(node2ID.getText()+"_"+node1ID.getText(), node2ID.getText(), node1ID.getText());

        if (MapEntity.getInstance().getEdge(edge.getEdgeID()) != null) { // Check for edge version 1
            MapEntity.getInstance().removeEdge(edge.getEdgeID());
            System.out.println("Removed Edge: " + edge.getEdgeID());
            this.parent.switchTo(SIDEBAR_MENU);
        } else if (MapEntity.getInstance().getEdge(edge2.getEdgeID()) != null) { // Check for edge verson 2
            MapEntity.getInstance().removeEdge(edge2.getEdgeID());
            System.out.println("Removed Edge: " + edge2.getEdgeID());
            this.parent.switchTo(SIDEBAR_MENU);
        } else {
            System.out.println("Edge doesn't exist in the database: " + edge.getEdgeID() + ", " + edge2.getEdgeID());
        }
    }
}
