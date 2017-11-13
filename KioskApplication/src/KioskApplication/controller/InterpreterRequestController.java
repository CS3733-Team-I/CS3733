package KioskApplication.controller;

import KioskApplication.entity.InterpreterRequest;
import KioskApplication.database.objects.Node;
import KioskApplication.entity.MapEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

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
    private MenuButton langSelect;

    @FXML
    private MenuItem spanish;

    @FXML
    private MenuItem chinese;

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
    public void addRequest(){
        btnSubmit.setOnAction(e -> sendRequest(e));
    }

    @FXML
    public InterpreterRequest sendRequest(ActionEvent e){
        String location = txtLocation.getText();
        //default should be "Current Location"

        Node nodeLocation; //store location here

        //ArrayList<Node> nodes = MapEntity.getAllNodes();
//        for(int i=0; i<nodes.size(); i++){
//            if(nodes.longName.equals(location) || nodes.shortName.equls(location)){
//                return nodes.get(i); //this line or next line?
//                nodeLocation = nodes.get(i);
//            }
//        }
        //find the node with the same location name
        String language = "None";
        if(langSelect.getItems()==spanish){
            language = "Spanish";
        }else if(langSelect.getItems()==chinese) {
            language = "Chinese";
        }

        String employee; //get from login information

        /*creates the request object
        InterpreterRequest newRequest = new InterpreterRequest(nodeLocation,employee, language);
        return newRequest;
        */
        System.out.println("location: " + location + "language: " + language);
        return null;
    }
}
