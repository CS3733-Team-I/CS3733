package KioskApplication.controller;

import KioskApplication.Entity.InterpreterRequest;
import KioskApplication.database.objects.Node;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

public class InterpreterRequestController {

    AdminWindowController parent;

    public InterpreterRequestController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML
    private Button btnSubmit;

    @FXML
    private TextField txtLocation;

    @FXML
    public void addRequest(){
        btnSubmit.setOnAction(e -> sendRequest(e));
    }

    @FXML
    public InterpreterRequest sendRequest(ActionEvent e){
        String location = txtLocation.getText();
        //find the node with the same location name
        Node nodeLocation; //store location here
        String language; //equals which ever selection they selected
        String employee; //get from login information

        /*creates the request object
        InterpreterRequest newRequest = new InterpreterRequest(nodeLocation,employee, language);
        return newRequest;
        */
        return null;
    }
}
