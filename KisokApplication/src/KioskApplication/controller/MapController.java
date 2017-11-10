package KioskApplication.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MapController {
    @FXML private ImageView mapImage;
    @FXML private ImageView pinIcon;

    private MapWindowController parent = null;

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    @FXML
    void onMapClicked(MouseEvent event) {
        if (!parent.equals(null)) parent.mapLocationClicked(event.getX(), event.getY());
    }
}
