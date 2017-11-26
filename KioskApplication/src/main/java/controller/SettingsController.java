package controller;

import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import entity.SystemSettings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class SettingsController extends ScreenController {

    @FXML private JFXTabPane settingTabPane;
    @FXML private Tab databaseTab;
    @FXML private Tab displayTab;
    @FXML private Tab pathfindingTab;
    @FXML private Tab aboutTab;

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
        astarButton.setUserData("A*");
        dijkstraButton.setToggleGroup(searchAlgToggleGroup);
        dijkstraButton.setUserData("Dijkstra");
        bfsButton.setToggleGroup(searchAlgToggleGroup);
        bfsButton.setUserData("BFS");
        dfsButton.setToggleGroup(searchAlgToggleGroup);
        dfsButton.setUserData("DFS");
    }

    @FXML
    void onSearchAlgorithmSelected(){
        SystemSettings systemSettings = SystemSettings.getInstance();
        systemSettings.setAlgorithm(searchAlgToggleGroup.getSelectedToggle().getUserData().toString());
    }

    @FXML
    void onBackPressed() {
        System.out.println("Back Pressed\n");

        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
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
