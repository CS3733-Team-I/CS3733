package KioskApplication.controller;

import KioskApplication.database.util.CSVFileUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import static KioskApplication.controller.AdminWindowController.SidebarType.*;

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
    void onEditPressed() throws IOException {
        System.out.println("Edit Pressed\n");

        this.parent.switchTo(SIDEBAR_EDIT);
    }

    @FXML
    void onAddEdgePressed() throws IOException{
        this.parent.switchTo(SIDEBAR_ADD_EDGE);
    }

    @FXML
    void onInterpreterPressed() throws IOException{
        System.out.println("Interpreter Request Pressed\n");

        this.parent.switchTo(SIDEBAR_INTERPRETER);
    }

    @FXML
    void onReadClicked() {
        CSVFileUtil.readNodesCSV(getClass().getResource("/KioskApplication/resources/csv/MapInodes.csv").getFile());
        CSVFileUtil.readEdgesCSV(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").getFile());
    }



    @FXML
    void onSaveClicked() {
        CSVFileUtil.writeNodesCSV(getClass().getResource("/KioskApplication/resources/csv/MapInodes.csv").getFile());
        CSVFileUtil.writeEdgesCSV(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").getFile());
    }

}
