package KioskApplication.controller;

import KioskApplication.database.util.CSVFileUtil;
import KioskApplication.entity.MapEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static KioskApplication.controller.AdminWindowController.SidebarType.*;

public class AdminSidebarController {

    AdminWindowController parent;

    private boolean isDisplay;

    @FXML Label AdminInfo;
    @FXML Button infoButton;
    @FXML private CheckBox showNodes;
    @FXML private CheckBox showEdges;

    AdminSidebarController(AdminWindowController parent) {
        this.parent = parent;
        this.isDisplay = false;
    }
    @FXML
    void displayAdminInfo() {
        infoButton.setText("Display My Information");
        if(!isDisplay) {
            this.AdminInfo.setText(parent.curr_admin_email);
            infoButton.setText("Hide My Information");
            isDisplay = true;
        }
        else {
            this.AdminInfo.setText("");
            infoButton.setText("Display My Information");
            isDisplay = false;
        }
    }

    @FXML
    void showNodes(){
        boolean isS = showNodes.isSelected();
        System.out.println(isS);
        this.parent.showNodes(isS);
    }

    @FXML
    void showEdges(){
        boolean isS = showEdges.isSelected();
        this.parent.showEdges(isS);
    }

    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        this.parent.switchTo(SIDEBAR_ADD_NODE);
    }

    @FXML
    void onEditPressed() throws IOException {
        System.out.println("Edit Pressed\n");

        this.parent.switchTo(SIDEBAR_EDIT_NODE);
    }

    @FXML
    void onAddEdgePressed() throws IOException{
        this.parent.switchTo(SIDEBAR_ADD_EDGE);
    }

    @FXML
    void onDeleteEdgePressed() throws IOException{
        this.parent.switchTo(SIDEBAR_DEL_EDGE);
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
            CSVFileUtil.writeNodesCSV(mapINodes.getPath());

            URI mapIEdges = new URI(getClass().getResource("/KioskApplication/resources/csv/MapIedges.csv").toString());
            CSVFileUtil.writeEdgesCSV(mapINodes.getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
