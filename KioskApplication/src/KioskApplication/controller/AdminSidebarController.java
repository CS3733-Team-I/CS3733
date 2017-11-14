package KioskApplication.controller;

import KioskApplication.database.util.CSVFileUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.*;

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
    void onEditPressed() throws IOException {
        System.out.println("Edit Pressed\n");

        this.parent.switchTo(SIDEBAR_EDIT);
    }

    @FXML
    void onAddEdgePressed() throws IOException{
        this.parent.switchTo(SIDEBAR_ADD_EDGE);
    }

    @FXML
    void onRequestPressed() {
        System.out.println("Request Pressed\n");
    }

    @FXML
    void onReadClicked() {
        CSVFileUtil.readNodesCSV(getClass().getResource("/KioskApplication/resources/csv/MapInodes.csv").getFile());
        CSVFileUtil.readNodesCSV(getClass().getResource("/KioskApplication/resources/csv/MapWnodes.csv").getFile());
        CSVFileUtil.readEdgesCSV(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").getFile());
    }


    @FXML
    void onSaveClicked() {
        CSVFileUtil.writeNodesCSV(getClass().getResource("/KioskApplication/resources/csv/MapInodes.csv").getFile());
        CSVFileUtil.writeEdgesCSV(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").getFile());
    }
}
