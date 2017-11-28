package controller;

import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import entity.LoginEntity;
import entity.SystemSettings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import utility.csv.CsvFileUtil;
import utility.node.NodeFloor;

import java.io.IOException;

public class SettingsController extends ScreenController {

    @FXML private JFXTabPane settingTabPane;

    @FXML private Tab aboutTab;
    @FXML private Tab displayTab;
    @FXML private Tab pathfindingTab;
    @FXML private Tab userTab;

    @FXML private Tab databaseTab;
    @FXML private Tab employeesTab;


    @FXML private RadioButton astarButton;
    @FXML private RadioButton dijkstraButton;
    @FXML private RadioButton bfsButton;
    @FXML private RadioButton dfsButton;

    @FXML private AnchorPane userPane;
    @FXML private AnchorPane employeesPane;

    ToggleGroup searchAlgToggleGroup = new ToggleGroup();

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    public void initialize() throws IOException{
        SystemSettings systemSettings = SystemSettings.getInstance();
        astarButton.setToggleGroup(searchAlgToggleGroup);
        astarButton.setUserData("A*");
        dijkstraButton.setToggleGroup(searchAlgToggleGroup);
        dijkstraButton.setUserData("Dijkstra");
        bfsButton.setToggleGroup(searchAlgToggleGroup);
        bfsButton.setUserData("BFS");
        dfsButton.setToggleGroup(searchAlgToggleGroup);
        dfsButton.setUserData("DFS");
        //Load saved selection; select appropriate radio button.
        for(Toggle toggle: searchAlgToggleGroup.getToggles()) {
            if(toggle.getUserData().equals(systemSettings.getPrefs().get("searchAlgorithm", "A*")))
                searchAlgToggleGroup.selectToggle(toggle);
        }

        // Add Employee Settings Screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeSettingsView.fxml"));
        loader.setRoot(employeesPane);
        loader.load();

        // Add Users Settings Screen
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/view/UserSettingsView.fxml"));
        loader2.setRoot(userPane);
        loader2.load();
    }

    public void checkPermissions() {
        switch (LoginEntity.getInstance().getPermission()) {
            case NONEMPLOYEE:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, displayTab, pathfindingTab, userTab);
                break;
            case EMPLOYEE:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, displayTab, pathfindingTab, userTab, databaseTab);
                break;
            case SUPER_USER:
            case ADMIN:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, displayTab, pathfindingTab, userTab, databaseTab, employeesTab);
                break;
        }
    }

    @FXML
    void onSearchAlgorithmSelected(){
        SystemSettings systemSettings = SystemSettings.getInstance();
        systemSettings.setAlgorithm(searchAlgToggleGroup.getSelectedToggle().getUserData().toString());
    }

    @FXML
    void readCSV() {
        try {
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapAnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapBnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapCnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapDnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapEnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapFnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapGnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapHnodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapInodes.csv").toURI().getPath());
            CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapWnodes.csv").toURI().getPath());

            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapAedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapBedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapCedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapDedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapEedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapFedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapGedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapHedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapIedges.csv").toURI().getPath());
            CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapWedges.csv").toURI().getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getMapController().reloadDisplay();
    }

    @FXML
    void saveCSV() {

    }

    @Override
    public Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/SettingsView.fxml");
        }
        return contentView;
    }

    private void resetPressed() {
        System.out.println("Reset");
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) { }

    @Override
    public void onMapNodeClicked(database.objects.Node node) { }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0, 0, 0, 0);
    }
}
