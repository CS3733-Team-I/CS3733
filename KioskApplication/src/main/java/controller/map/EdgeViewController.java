package controller.map;

import database.objects.Edge;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

public class EdgeViewController {

    Edge edge;
    NodesEdgesView parent;

    Point2D start, end;

    @FXML AnchorPane container;
    @FXML Line line;

    public EdgeViewController(Edge edge, Point2D start, Point2D end) {
        this.edge = edge;
        this.start = start;
        this.end = end;
    }

    @FXML
    private void initialize() {
        line.setEndX(end.getX() - start.getX());
        line.setEndY(end.getY() - start.getY());

        AnchorPane.setLeftAnchor(container, start.getX());
        AnchorPane.setTopAnchor(container, start.getY());

        line.setOnMouseClicked(event -> {
            // Let Parent know it was clicked
            parent.mapEdgeClicked(edge);
        });
    }
}
