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

//    @FXML
//    public void addRequest(){
//        btnSubmit.setOnAction(e -> sendRequest(e));
//    }

    @FXML
    public InterpreterRequest addRequest(){
        int interpID;
        String location = txtLocation.getText();
        //default is "My Location"

        //sets nodeLocation to default my location
        Node nodeLocation = DatabaseController.getNode("IDEPT00303");

        //searches for node name
        ArrayList<Node> nodes = DatabaseController.getAllNodes();
        for(int i=0; i<nodes.size(); i++){
            if(nodes.get(i).getLongName().equals(location) || nodes.get(i).getLongName().equals(location)){
                nodeLocation = nodes.get(i);
            }
        }

        String language = "None";
        if(langMenu.getValue().toString().equals("Spanish")){
            language = "Spanish";
        }else if(langMenu.getValue().toString().equals("Chinese")) {
            language = "Chinese";
        }

        String adminEmail = parent.curr_admin_email;

        //Recommend isolating to InterpreterRequest Class
        if(DatabaseController.getAllInterpreterRequests().isEmpty()){
            interpID = 0;
        }else{
            interpID = DatabaseController.getAllInterpreterRequests().get(DatabaseController.getAllInterpreterRequests().size()-1).getInterpreterID() + 1;

        }


        System.out.println("location: " + location + ". language: " + language + ". Admin Email: " + adminEmail + ". Interpreter ID: " + interpID);
        //Adds the Interpreter request to the database.... but doesn't work
        //Also can't understand how to get the nodes
//        DatabaseController.addIntepreterRequest(language, interpID, interpID);


//        InterpreterRequest newRequest = new InterpreterRequest(adminEmail, language, interpID, interpID);
//        return newRequest;

        return null;

    }
}
