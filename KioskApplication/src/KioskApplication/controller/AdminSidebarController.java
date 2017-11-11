package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD;

public class AdminSidebarController {

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
    void onEditPressed() {
        System.out.println("Edit Pressed\n");
    }

    @FXML
    void onRequestPressed() {
        System.out.println("Request Pressed\n");
    }
}
