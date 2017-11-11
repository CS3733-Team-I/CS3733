package KioskApplication.controller;

import javafx.fxml.FXML;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD;

public class AdminAddNodeController {
    AdminWindowController parent;

    AdminAddNodeController(AdminWindowController parent) {
        this.parent = parent;
    }

    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD);
    }
}
