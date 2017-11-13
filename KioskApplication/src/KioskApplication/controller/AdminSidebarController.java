package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_EDIT;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;
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


    AdminWindowController parent;

    AdminSidebarController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD);
    }

    @FXML
    void onRemovePressed()  {
        System.out.println("Remove Pressed\n");
    }

    @FXML
    void onEditPressed() throws IOException {
        System.out.println("Edit Pressed\n");

        this.parent.switchTo(SIDEBAR_EDIT);
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
