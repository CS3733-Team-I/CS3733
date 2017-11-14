package KioskApplication.controller;

import KioskApplication.entity.InterpreterRequest;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
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
        String location = txtLocation.getText();
        //default should be "Current Location"

        //Following code is to send a node instead of a string
//        Node nodeLocation; //store location here
//
//        ArrayList<Node> nodes = MapEntity.getAllNodes();
//        for(int i=0; i<MapEntity.getAllNodes().size(); i++){
//            if(nodes.longName.equals(location) || nodes.shortName.equls(location)){
//                nodeLocation = nodes.get(i);
//            }
//        }

        String language = "None";
        if(langMenu.getValue().toString().equals("Spanish")){
            language = "Spanish";
        }else if(langMenu.getValue().toString().equals("Chinese")) {
            language = "Chinese";
        }

        //get from login information
        //LoginController LC = new LoginController();
        //String adminEmail = LoginController

        /*creates the request object
        InterpreterRequest newRequest = new InterpreterRequest(location, adminEmail, language);
        return newRequest;
        */
        System.out.println("location: " + location + ". language: " + language);
        return null;
    }
}
