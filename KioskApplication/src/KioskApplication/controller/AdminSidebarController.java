package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class AdminSidebarController {

    @FXML
    private MenuButton requestMenu;

    @FXML
    private MenuItem interpreterSelect;

    @FXML
    void onAddPressed() {
        System.out.println("Add Pressed\n");
    }

    @FXML
    void onRemovePressed()  {
        System.out.println("Remove Pressed\n");
    }

    @FXML
    void onEditPressed() {
        System.out.println("Edit Pressed\n");
    }

    //Use the request menu dropdown to select interpreter
    @FXML
    void selectService(){
        interpreterSelect.setOnAction(e -> interpreterViewer(e));
    }

    //will eventually: switch sidebar to interpreter request window
    @FXML
    void interpreterViewer(javafx.event.ActionEvent e){
        System.out.println("Switch view to interpreter request");
    }
}
