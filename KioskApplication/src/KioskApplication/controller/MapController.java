package KioskApplication.controller;

import KioskApplication.entity.MapEntity;
import KioskApplication.utility.MathUtility;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MapController {
    @FXML private ImageView mapView;
    @FXML private ScrollPane scrollPane;

    private MapWindowController parent = null;
    private Rectangle2D defaultViewport = new Rectangle2D(2250, 875, 600, 500);

    private static int MIN_PIXELS = 600;

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    @FXML
    public void initialize() {
        MapEntity mapEntity = MapEntity.getInstance();
    }

    @FXML
    void onMapClicked(MouseEvent event) {
        if (!parent.equals(null)) {
            parent.mapLocationClicked(event.getX(), event.getY());
        }
    }

    @FXML
    public void zoomInPressed() {
        System.out.println("Zoom in pressed.");
    }

    @FXML
    public void zoomOutPressed() {
        System.out.println("Zoom out pressed.");
    }

    @FXML
    public void recenterPressed() {
        mapView.setViewport(defaultViewport);
    }
}
