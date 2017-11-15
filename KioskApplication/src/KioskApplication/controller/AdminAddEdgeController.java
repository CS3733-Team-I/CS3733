package KioskApplication.controller;
import KioskApplication.database.objects.Node;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD_EDGE;



public class AdminAddEdgeController {

    AdminWindowController parent;

    AdminAddEdgeController(AdminWindowController parent) {
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
    public void onMapNodePressed(Node node){
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

    @FXML
    void onBackPressed() throws IOException{
        this.parent.switchTo(SIDEBAR_MENU);
    }

    @FXML
    void onSubmitClicked() throws IOException{

    }
}
