package KioskApplication.controller;

import KioskApplication.utility.MathUtility;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MapController {
    @FXML private ImageView mapView;

    private MapWindowController parent = null;
    private Rectangle2D defaultViewport = new Rectangle2D(2250, 875, 600, 500);
    private Point2D lastMousePosition;

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
        mapView.setViewport(defaultViewport);
    }

    @FXML
    public void onMapDragBegin(MouseEvent e) {
        lastMousePosition = new Point2D(e.getX(), e.getY());
    }

    @FXML
    public void onMapDragged(MouseEvent e) {
        // Get current viewport
        Rectangle2D viewport = mapView.getViewport();

        // Convert mouse (x,y) to image (x,y) and then compute the amount dragged
        Point2D draggedPoint = viewCoordinatesToImageCoordinates(e.getX(), e.getY());
        Point2D dragDelta = draggedPoint.subtract(lastMousePosition);

        // Clamp viewport to valid locations within the image
        double width = mapView.getImage().getWidth();
        double height = mapView.getImage().getHeight();

        double maxX = width - viewport.getWidth();
        double maxY = height - viewport.getHeight();

        double minX = MathUtility.clamp(dragDelta.getX(), 0, maxX);
        double minY = MathUtility.clamp(dragDelta.getY(), 0, maxY);

        // Update viewport and lastMousePosition
        mapView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));

        lastMousePosition = new Point2D(e.getX(), e.getY());
    }

    private Point2D viewCoordinatesToImageCoordinates(double x, double y) {
        double xProportion = x / mapView.getBoundsInLocal().getWidth();
        double yProportion = y / mapView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = mapView.getViewport();
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(),
                viewport.getMinY() + yProportion * viewport.getHeight());

    }
}
