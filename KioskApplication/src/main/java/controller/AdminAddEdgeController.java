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


public class AdminAddEdgeController extends ScreenController {

    AdminAddEdgeController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

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
        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
    }

    @FXML
    void onSubmitClicked() throws IOException{
        // Create Edge
        Edge edge = new Edge(edgeID.getText(), node1ID.getText(), node2ID.getText());
        // Check to see if the edge Exists (!!bidirectional!!)
        if (MapEntity.getInstance().getEdge(node1ID.getText() + "_" + node2ID.getText()) == null && MapEntity.getInstance().getEdge(node2ID.getText() + "_" + node1ID.getText()) == null) {
            // If not then add edge
            MapEntity.getInstance().addEdge(edge);
            System.out.println("Added Edge: " + edge.getEdgeID());

            getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
        } else{
            System.out.println("Edge already in the database: " + edge.getEdgeID());

        }
    }

    int lastChanged = 1;
    @Override
    public void onMapNodeClicked(Node node){
        if(node1ID.getText() != node.getNodeID() && node2ID.getText() != node.getNodeID()){ // If node is not already one of the ones selected
            if(node1ID.getText().toString().isEmpty()){
                node1ID.setText(node.getNodeID());
            }
            else if(node2ID.getText().toString().isEmpty()){
                node2ID.setText(node.getNodeID());
            }
            else { // If both already have a node ID filled in
                // Select last filled one
                if(lastChanged == 1){ // If 2 was filled more recently
                    node1ID.setText(node.getNodeID());
                    lastChanged = 2;
                }
                else if(lastChanged == 2){ // If 1 was filled more recently
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
            contentView = loadView("/view/addEdge.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void onMapLocationClicked(Point2D location) { }

    @Override
    public void resetScreen() {
        node1ID.setText("");
        node2ID.setText("");
        edgeID.setText("");
        errorMsg.setText("");
    }
}
