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

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_ADD;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_EDIT;
import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

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
    void onRequestPressed() {
        System.out.println("Request Pressed\n");
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
            CSVFileUtil.writeNodesCSV(mapINodes.getPath(), false);

            URI mapWNodes = new URI(getClass().getResource("/KioskApplication/resources/csv/MapWnodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapWNodes.getPath(), true);


            URI mapIEdges = new URI(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").toString());
            CSVFileUtil.writeEdgesCSV(mapIEdges.getPath(), false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
