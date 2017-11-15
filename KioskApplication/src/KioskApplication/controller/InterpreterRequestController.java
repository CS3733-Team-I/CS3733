package KioskApplication.controller;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.InterpreterRequest;
import KioskApplication.database.connection.Connector;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_INTERPRETER;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class InterpreterRequestController {

    AdminWindowController parent;

    public InterpreterRequestController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnCancel;

    @FXML
    private TextField txtLocation;

    @FXML
    private ChoiceBox langMenu;


    @FXML
    void onInterpreterPressed() throws IOException {
        System.out.println("Interpreter Request Pressed\n");

        this.parent.switchTo(SIDEBAR_INTERPRETER);
    }

    @FXML
    void onCancelPressed() throws IOException{
        System.out.println("Cancel Pressed\n");

        this.parent.switchTo(SIDEBAR_MENU);
    }

    @FXML
    public void addRequest() throws IOException {
        int interpID;
        String location = txtLocation.getText();

        //sets nodeLocation to default my location
        Node nodeLocation = DatabaseController.getNode(location);

        String language = "None";
        if(langMenu.getValue().toString().equals("Spanish")){
            language = "Spanish";
        }else if(langMenu.getValue().toString().equals("Chinese")) {
            language = "Chinese";
        }

        //Finds current admin that is logged in
        String adminEmail = parent.curr_admin_email;

        if(DatabaseController.getAllInterpreterRequests().isEmpty()){
            interpID = 0;
        }else{
            interpID = DatabaseController.getAllInterpreterRequests().get(DatabaseController.getAllInterpreterRequests().size()-1).getInterpreterID() + 1;
        }


        System.out.println("location: " + nodeLocation.getLongName() + ". language: " + language + ". Admin Email: " + adminEmail + ". Interpreter ID: " + interpID);

        //Adds the Interpreter request to the database
        DatabaseController.addRequest(interpID,nodeLocation.getNodeID(), adminEmail);
        DatabaseController.addIntepreterRequest(language, interpID, interpID);
        System.out.println(DatabaseController.getAllInterpreterRequests());

        this.parent.switchTo(SIDEBAR_MENU);
    }

    public void onMapNodeClicked(Node n) {
        txtLocation.setText(n.getNodeID());
    }
}
