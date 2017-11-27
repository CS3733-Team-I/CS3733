package controller;

import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import entity.AlgorithmSetting;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import pathfinder.A_star;
import pathfinder.BreadthFirst;
import pathfinder.DepthFirst;
import pathfinder.Dijkstra;
import utility.csv.CsvFileUtil;
import utility.node.NodeFloor;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class SettingsController extends ScreenController {

    @FXML private JFXTabPane settingTabPane;
    @FXML private Tab databaseTab;
    @FXML private Tab displayTab;
    @FXML private Tab pathfindingTab;
    @FXML private Tab aboutTab;

    @FXML private Label searchAlgorithmLabel;
    @FXML private RadioButton astarButton;
    @FXML private RadioButton dijkstraButton;
    @FXML private RadioButton bfsButton;
    @FXML private RadioButton dfsButton;

    ToggleGroup searchAlgToggleGroup = new ToggleGroup();

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    public void initialize() throws IOException{
        astarButton.setToggleGroup(searchAlgToggleGroup);
        astarButton.setUserData("Astar");
        dijkstraButton.setToggleGroup(searchAlgToggleGroup);
        dijkstraButton.setUserData("Dijkstra");
        bfsButton.setToggleGroup(searchAlgToggleGroup);
        bfsButton.setUserData("BFS");
        dfsButton.setToggleGroup(searchAlgToggleGroup);
        dfsButton.setUserData("DFS");
    }

    @FXML
    void onSearchAlgorithmSelected(){
        AlgorithmSetting algorithmSetting = AlgorithmSetting.getInstance();
        switch(searchAlgToggleGroup.getSelectedToggle().getUserData().toString()){
            case "Astar":
                algorithmSetting.changeAlgorithm(new A_star());
                break;
            case "Dijkstra":
                algorithmSetting.changeAlgorithm(new Dijkstra());
                break;
            case "BFS":
                algorithmSetting.changeAlgorithm(new BreadthFirst());
                break;
            case "DFS":
                algorithmSetting.changeAlgorithm(new DepthFirst());
                break;
            default:
                break;
        }
        searchAlgorithmLabel.setText("Search Algorithm: " + searchAlgToggleGroup.getSelectedToggle().getUserData().toString());

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
