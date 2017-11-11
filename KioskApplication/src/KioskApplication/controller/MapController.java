package KioskApplication.controller;

import KioskApplication.model.MapModel;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MapController {
    @FXML private ImageView mapImage;
    @FXML private ImageView pinIcon;
    private MapModel model;
    private MapWindowController parent = null;

    public MapController() {
        this.model = new MapModel();
    }

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    @FXML
    void onMapClicked(MouseEvent event) {
        if (!parent.equals(null)) parent.mapLocationClicked(event.getX(), event.getY());
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
        System.out.println("Recenter pressed");
    }
}
