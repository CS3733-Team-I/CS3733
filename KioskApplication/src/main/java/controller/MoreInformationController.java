package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import controller.map.MapController;
import database.objects.Edge;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import utility.node.NodeFloor;

public class MoreInformationController extends ScreenController {

    @FXML private JFXButton button1, button2, button3, button4, button5;
    @FXML private JFXTextArea displayNotes;

    String requestID;

    public MoreInformationController(MainWindowController parent, MapController mapController, String requestID) {
        super(parent, mapController);
        this.requestID = requestID;
    }

    @FXML
    public void initialize(){

    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerMoreInformationView.fxml");
        }
        return contentView;
    }

    @Override
    public void onMapLocationClicked(MouseEvent e, Point2D location) {

    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {

    }

    @Override
    public void onMapEdgeClicked(Edge edge) {

    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {

    }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,500,0,0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}
