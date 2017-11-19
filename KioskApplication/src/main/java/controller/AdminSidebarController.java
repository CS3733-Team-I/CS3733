package controller;

import database.objects.Edge;
import database.util.CSVFileUtil;
import entity.MapEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AdminSidebarController extends ScreenController {
    @FXML private Label AdminInfo;
    @FXML private Button infoButton;
    @FXML private CheckBox showNodes;
    @FXML private CheckBox showEdges;
    @FXML private MenuButton requestMenu;
    @FXML private MenuItem interpreterSelect;

    private boolean isDisplay;

    AdminSidebarController(MainWindowController parent, MapController map) {
        super(parent, map);
        this.isDisplay = false;
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/AdminSidebarView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) { }

    @Override
    public void onMapNodeClicked(database.objects.Node node) { }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        // TODO: implement this
    }

    @FXML
    protected void initialize() {
        getMapController().setShowNodes(showNodes.isSelected());
        getMapController().setShowEdges(showEdges.isSelected());
    }

    @FXML
    void displayAdminInfo() {
        infoButton.setText("Display My Information");
        if(!isDisplay) {
            this.AdminInfo.setText(""); // TODO: redo admin mail field
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
        getMapController().setShowNodes(isS);
    }

    @FXML
    void showEdges(){
        boolean isS = showEdges.isSelected();
        getMapController().setShowEdges(isS);
    }

    @FXML
    void onAddPressed() throws IOException {
        System.out.println("Add Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_ADD_NODE);
    }

    @FXML
    void onEditPressed() throws IOException {
        System.out.println("Edit Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_EDIT_NODE);
    }

    @FXML
    void onAddEdgePressed() throws IOException{
        getParent().switchToScreen(ApplicationScreen.ADMIN_ADD_EDGE);
    }

    @FXML
    void onDeleteEdgePressed() throws IOException{
        getParent().switchToScreen(ApplicationScreen.ADMIN_DEL_EDGE);
    }

    @FXML
    void onInterpreterPressed() throws IOException{
        System.out.println("Interpreter Request Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_INTERPRETER);
    }

    @FXML
    void viewRequests() throws IOException {
        System.out.println("Request Manager Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_VIEWREQUEST);
    }

    @FXML
    void onReadClicked() {
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapInodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapWnodes.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapIedges.csv"));

        MapEntity.getInstance().readAllFromDatabase();

        getMapController().setShowNodes(showNodes.isSelected());
        getMapController().setShowEdges(showEdges.isSelected());
    }

    @FXML
    void onSaveClicked() {
        try {
            URI mapINodes = new URI(getClass().getResource("/csv/MapInodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapINodes.getPath(), false);

            URI mapWNodes = new URI(getClass().getResource("/csv/MapWnodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapWNodes.getPath(), true);

            URI mapIEdges = new URI(getClass().getResource("/csv/MapIedges.csv").toString());
            CSVFileUtil.writeEdgesCSV(mapIEdges.getPath(), false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
