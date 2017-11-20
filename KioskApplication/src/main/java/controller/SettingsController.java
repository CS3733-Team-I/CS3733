package controller;

import database.objects.Edge;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import utility.ApplicationScreen;
import utility.NodeFloor;

public class SettingsController extends ScreenController {
    @FXML private Label SALabel;

    @FXML private RadioButton AstarBtn;

    @FXML private RadioButton DijkstraBtn;

    @FXML private RadioButton BFSBtn;

    @FXML private RadioButton DFSBtn;

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }
    ToggleGroup g = new ToggleGroup();

    @FXML
    void initialize(){
        AstarBtn.setToggleGroup(g);
        AstarBtn.setUserData("Astar");
        DijkstraBtn.setToggleGroup(g);
        DijkstraBtn.setUserData("Dijkstra");
        BFSBtn.setToggleGroup(g);
        BFSBtn.setUserData("BFS");
        DFSBtn.setToggleGroup(g);
        DFSBtn.setUserData("DFS");

    }

    @FXML
    void onBackPressed() {
        System.out.println("Back Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
    }

    @FXML
    void OnSASel(){
        SALabel.setText("Search Algorithm: " + g.getSelectedToggle().getUserData().toString());
    }

    @Override
    public Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/settings.fxml");
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
    }
}
