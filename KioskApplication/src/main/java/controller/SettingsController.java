package controller;

import database.objects.Edge;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import utility.ApplicationScreen;
import utility.NodeFloor;

public class SettingsController extends ScreenController {

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    @FXML
    void onBackPressed() {
        System.out.println("Back Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
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
