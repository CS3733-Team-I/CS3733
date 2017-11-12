package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import javafx.event.ActionEvent;

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

    @FXML
    void selectService(){
        interpreterSelect.setOnAction(e -> interpreterViewer(e));
    }

    @FXML
    void interpreterViewer(javafx.event.ActionEvent e){
        System.out.println("Switch view to interpreter request");
    }
}
