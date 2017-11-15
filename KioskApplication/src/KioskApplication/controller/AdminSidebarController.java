package KioskApplication.controller;

import KioskApplication.database.DatabaseController;
import KioskApplication.database.util.CSVFileUtil;
import KioskApplication.entity.MapEntity;
import KioskApplication.utility.NodeFloor;
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
    @FXML private Label AdminInfo;
    @FXML private Button infoButton;
    @FXML private CheckBox showNodes;
    @FXML private CheckBox showEdges;
    @FXML private MenuButton requestMenu;
    @FXML private MenuItem interpreterSelect;

    AdminWindowController parent;

    private boolean isDisplay;

    AdminSidebarController(AdminWindowController parent) {
        this.parent = parent;
        this.isDisplay = false;
    }

    @FXML
    protected void initialize() {
        parent.setShowNodes(showNodes.isSelected());
        parent.setShowEdges(showEdges.isSelected());
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
        this.parent.setShowNodes(isS);
    }

    @FXML
    void showEdges(){
        boolean isS = showEdges.isSelected();
        this.parent.setShowEdges(isS);
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
    }

    @FXML
    void onReadClicked() {
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/KioskApplication/resources/csv/MapInodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/KioskApplication/resources/csv/MapWnodes.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/KioskApplication/resources/csv/MapIedges.csv"));

        MapEntity.getInstance().readAllFromDatabase();

        parent.setShowNodes(showNodes.isSelected());
        parent.setShowEdges(showEdges.isSelected());
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
