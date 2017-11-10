package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

abstract class MapWindowController extends SplitPane {
    @FXML private AnchorPane sidebarPane;
    @FXML private ImageView mapView;

    private MapController mapController;

    public MapWindowController() throws IOException {
        FXMLLoader loader = new FXMLLoader(MapWindowController.class.getResource("/UIFramework/KioskApplication.view/MapWindowView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        mapController = new MapController();

        FXMLLoader mapPaneLoader = new FXMLLoader(MapWindowController.class.getResource("/UIFramework/KioskApplication.view/MapView.fxml"));
        mapPaneLoader.setRoot(mapView);
        mapPaneLoader.setController(mapController);
        mapPaneLoader.load();
    }

    protected final AnchorPane getSidebarPane() {
        return sidebarPane;
    }

    protected final MapController getMapController() {
        return mapController;
    }

    abstract void mapLocationClicked(double x, double y);
    abstract void mapNodeClicked();
}
