package KioskApplication.controller;

import KioskApplication.database.util.CSVFileUtil;
import KioskApplication.entity.MapEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
        try {
            URI mapINodes = new URI(getClass().getResource("/KioskApplication/resources/csv/MapInodes.csv").toString());
            CSVFileUtil.readNodesCSV(mapINodes.getPath());

            URI mapWNodes = new URI(getClass().getResource("/KioskApplication/resources/csv/MapWnodes.csv").toString());
            CSVFileUtil.readNodesCSV(mapWNodes.getPath());

            URI mapIEdges = new URI(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").toString());
            CSVFileUtil.readEdgesCSV(mapIEdges.getPath());

            MapEntity.getInstance().readAllFromDatabase();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }



    @FXML
    void onSaveClicked() {
        try {
            URI mapINodes = new URI(getClass().getResource("/KioskApplication/resources/csv/MapInodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapINodes.getPath());

            URI mapIEdges = new URI(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").toString());
            CSVFileUtil.writeEdgesCSV(mapINodes.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
