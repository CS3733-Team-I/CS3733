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
import javafx.scene.control.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedController extends ScreenController {

    @FXML
    private AnchorPane tabAnchor;

    @FXML
    private JFXHamburger settingHam;

    @FXML
    private JFXTabPane settingTabPane;

    @FXML
    private JFXDrawer settingDrawer;

    @FXML
    private Label SALabel;

    @FXML private RadioButton AstarBtn;

    @FXML private RadioButton DijkstraBtn;

    @FXML private RadioButton BFSBtn;

    @FXML private RadioButton DFSBtn;

    ToggleGroup g = new ToggleGroup();

    public AdvancedController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }


    public void initialize() throws IOException{
        AstarBtn.setToggleGroup(g);
        AstarBtn.setUserData("Astar");
        DijkstraBtn.setToggleGroup(g);
        DijkstraBtn.setUserData("Dijkstra");
        BFSBtn.setToggleGroup(g);
        BFSBtn.setUserData("BFS");
        DFSBtn.setToggleGroup(g);
        DFSBtn.setUserData("DFS");

        tabAnchor.setVisible(false);

        GridPane SettingsAnchor= FXMLLoader.load(getClass().getResource("/view/SettingsView.fxml"));
        SingleSelectionModel<Tab> selectionModel = settingTabPane.getSelectionModel();
        for(Node node : SettingsAnchor.getChildren()) {
            if(node.getAccessibleText() != null) {
                node.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e)->{
                    switch (node.getAccessibleText())
                    {
                        case "pathfinding":
                            selectionModel.select(0);
                            break;
                        case "display":
                            selectionModel.select(1);
                            break;
                        case "about":
                            selectionModel.select(2);
                            break;
                        default:
                            System.out.println("Tab not accessible, index: " + selectionModel.getSelectedIndex());
                            throw new IndexOutOfBoundsException();
                    }
                });
            }
        }

        HamburgerBackArrowBasicTransition t1 = new HamburgerBackArrowBasicTransition(settingHam);
        t1.setRate(-1);
        settingHam.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            t1.setRate(t1.getRate() * -1);
            t1.play();
            if (settingDrawer.isShown()) {
                //System.out.println("HERE1");
                settingDrawer.close();
                tabAnchor.setVisible(false);
            } else {
                //System.out.println("HERE2");
                settingDrawer.open();
                tabAnchor.setVisible(true);
            }
        });
        settingDrawer.setSidePane(SettingsAnchor);
    }

    @FXML
    void OnSASel(){
        SALabel.setText("Search Algorithm: " + g.getSelectedToggle().getUserData().toString());
    }

    @FXML
    void onBackPressed() {
        System.out.println("Back Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
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
        getMapController().setAnchor(0, 0, 0, 0);
    }
}
