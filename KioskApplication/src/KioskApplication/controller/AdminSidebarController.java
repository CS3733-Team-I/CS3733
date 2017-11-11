package KioskApplication.controller;

import KioskApplication.model.AdminModel;
import javafx.fxml.FXML;

public class AdminSidebarController {
    /* admin model */
    private AdminModel model;

    public AdminSidebarController() {
        this.model = new AdminModel();
    }

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
    void onRequestPressed() {
        System.out.println("Request Pressed\n");
    }
}
