package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import com.jfoenix.transitions.hamburger.HamburgerNextArrowBasicTransition;
import database.objects.Edge;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends ScreenController {
    @FXML
    private JFXHamburger settingHam;

    @FXML
    private JFXTabPane settingTabPane;

    @FXML
    private JFXDrawer settingDrawer;

    @FXML
    private JFXButton btPathfinding;

    @FXML private Label SALabel;

    @FXML private RadioButton AstarBtn;

    @FXML private RadioButton DijkstraBtn;

    @FXML private RadioButton BFSBtn;

    @FXML private RadioButton DFSBtn;

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }
    ToggleGroup g = new ToggleGroup();


    public void initialize() throws IOException{
//        AstarBtn.setToggleGroup(g);
//        AstarBtn.setUserData("Astar");
//        DijkstraBtn.setToggleGroup(g);
//        DijkstraBtn.setUserData("Dijkstra");
//        BFSBtn.setToggleGroup(g);
//        BFSBtn.setUserData("BFS");
//        DFSBtn.setToggleGroup(g);
//        DFSBtn.setUserData("DFS");

        AnchorPane settingView = FXMLLoader.load(getClass().getResource("view/SettingsView.fxml"));
        HamburgerNextArrowBasicTransition t1 = new HamburgerNextArrowBasicTransition(settingHam);
        t1.setRate(-1);
        settingHam.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            t1.setRate(t1.getRate() * -1);
            t1.play();
            if (settingDrawer.isShown()) {
                System.out.println("HERE1");
                settingDrawer.close();
            } else {
                System.out.println("HERE2");
                settingDrawer.open();
            }
        });
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
            contentView = loadView("/view/AdvancedView.fxml");
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
