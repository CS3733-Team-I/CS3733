package KioskApplication.controller;

import KioskApplication.utility.MathUtility;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MapController {
    @FXML private ImageView mapView;

    private MapWindowController parent = null;
    private Rectangle2D defaultViewport = new Rectangle2D(2250, 875, 600, 500);
    private Point2D lastMousePosition;

    private static int MIN_PIXELS = 600;

    public void setParent(MapWindowController controller) {
        parent = controller;
    }

    @FXML
    void onMapClicked(MouseEvent event) {
        if (!parent.equals(null)) {
            Point2D imageCoordinates = viewCoordinatesToImageCoordinates(event.getX(), event.getY());
            parent.mapLocationClicked(imageCoordinates.getX(), imageCoordinates.getY());
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
        Point2D dragDelta = lastMousePosition.subtract(e.getX(), e.getY());

        // Clamp viewport to valid locations within the image
        double width = mapView.getImage().getWidth();
        double height = mapView.getImage().getHeight();

        double maxX = width - viewport.getWidth();
        double maxY = height - viewport.getHeight();

        double minX = MathUtility.clamp(viewport.getMinX() + dragDelta.getX(), 0, maxX);
        double minY = MathUtility.clamp(viewport.getMinY() + dragDelta.getY(), 0, maxY);

        // Update viewport and lastMousePosition
        mapView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));

        lastMousePosition = new Point2D(e.getX(), e.getY());
    }

    @FXML
    public void onMouseScroll(ScrollEvent e) {
        // Adapted from https://stackoverflow.com/questions/37709800/zooming-and-panning-javafx-change-scroll

        double delta = -e.getDeltaY();
        Rectangle2D viewport = mapView.getViewport();

        // limit scale to MIN_PIXELS in either direction (whichever is smallest)
        double minScale = Math.min(MIN_PIXELS / viewport.getWidth(), MIN_PIXELS / viewport.getHeight());
        // limit scale to the maximum height of the image
        double maxScale = mapView.getImage().getHeight() / viewport.getHeight();
        double scale = MathUtility.clamp(Math.pow(1.01, delta), minScale, maxScale);

        Point2D mouse = viewCoordinatesToImageCoordinates(e.getX(), e.getY());

        double newWidth = viewport.getWidth() * scale;
        double newHeight = viewport.getHeight() * scale;

        // To keep the visual point under the mouse from moving, we need
        // (x - newViewportMinX) / (x - currentViewportMinX) = scale
        // where x is the mouse X coordinate in the image

        // solving this for newViewportMinX gives

        // newViewportMinX = x - (x - currentViewportMinX) * scale

        // we then clamp this value so the image never scrolls out
        // of the imageview:

        double newMinX = MathUtility.clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale,
                0, mapView.getImage().getWidth() - newWidth);
        double newMinY = MathUtility.clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale,
                0, mapView.getImage().getHeight() - newHeight);

        mapView.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
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
