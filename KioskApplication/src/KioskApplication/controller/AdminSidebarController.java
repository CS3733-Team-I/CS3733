package KioskApplication.controller;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.util.CSVFileUtil;
import KioskApplication.entity.MapEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;

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
    private boolean isDisplay;
    @FXML
    Label AdminInfo;
    @FXML
    Button infoButton;

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
    void onInterpreterPressed() throws IOException{
        System.out.println("Interpreter Request Pressed\n");

        this.parent.switchTo(SIDEBAR_INTERPRETER);
    }

    @FXML
    void viewRequests() throws IOException {
        System.out.println("Request Manager Pressed\n");

        this.parent.switchTo(AdminWindowController.SidebarType.SIDEBAR_VIEWREQUEST);
//        activeRequests.getItems().clear();
//        activeRequests.getItems().addAll( DatabaseController.getAllRequests());
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
