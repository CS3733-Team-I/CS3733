package KioskApplication.controller;

import KioskApplication.entity.InterpreterRequest;
import KioskApplication.database.objects.Node;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
    private MenuButton langSelect;

    @FXML
    private MenuItem spanish;

    @FXML
    private MenuItem chinese;

    @FXML
    public void addRequest(){
        btnSubmit.setOnAction(e -> sendRequest(e));
    }

    @FXML
    public InterpreterRequest sendRequest(ActionEvent e){
        String location = txtLocation.getText();
        //find the node with the same location name
        String language = "None";
        if(langSelect.getItems()==spanish){
            language = "Spanish";
        }else if(langSelect.getItems()==chinese) {
            language = "Chinese";
        }
        Node nodeLocation; //store location here
        //gets language from the
        String employee; //get from login information

        /*creates the request object
        InterpreterRequest newRequest = new InterpreterRequest(nodeLocation,employee, language);
        return newRequest;
        */
        System.out.println("location: " + location + "language: " + language);
        return null;
    }
}
