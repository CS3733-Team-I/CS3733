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
        getMapController().setShowNodes(showNodes.isSelected());
        getMapController().setShowEdges(showEdges.isSelected());
        getMapController().setAnchor(0, 200, 0, 0);
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
        // TODO implement this better
        // Load nodes
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapAnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapBnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapCnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapDnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapEnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapFnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapGnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapHnodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapInodes.csv"));
        CSVFileUtil.readNodesCSV(getClass().getResourceAsStream("/csv/MapWnodes.csv"));

        // Load edges
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapAedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapBedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapCedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapDedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapEedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapFedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapGedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapHedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapIedges.csv"));
        CSVFileUtil.readEdgesCSV(getClass().getResourceAsStream("/csv/MapWedges.csv"));

        MapEntity.getInstance().readAllFromDatabase();

        getMapController().setShowNodes(showNodes.isSelected());
        getMapController().setShowEdges(showEdges.isSelected());
    }

    @FXML
    void onSaveClicked() {
        // TODO Implement SaveCSV with different team letters
        /*
        try {
            URI mapINodes = new URI(getClass().getResource("/csv/MapInodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapINodes.getPath(), false);

            URI mapWNodes = new URI(getClass().getResource("/csv/MapWnodes.csv").toString());
            CSVFileUtil.writeNodesCSV(mapWNodes.getPath(), true);

            URI mapIEdges = new URI(getClass().getResource("/csv/MapIedges.csv").toString());
            CSVFileUtil.writeEdgesCSV(mapIEdges.getPath(), false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
    }
}
