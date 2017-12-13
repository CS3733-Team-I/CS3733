package controller.map;

import database.objects.Edge;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import utility.ResourceManager;

import java.io.IOException;

public class EdgeView extends AnchorPane {

    Edge edge;

    Point2D start, end;

    Line line;

    public EdgeView(Edge edge, Point2D start, Point2D end) {
        this.setMouseTransparent(true);

        this.edge = edge;
        this.start = start;
        this.end = end;

        line = new Line();
        line.setStrokeWidth(10);
        line.setStroke(Color.DARKGRAY);

        setPosition(start, end);

        this.getChildren().add(line);
    }

    public void setPosition(Point2D start, Point2D end) {
        line.setEndX(end.getX() - start.getX());
        line.setEndY(end.getY() - start.getY());

        AnchorPane.setLeftAnchor(this, start.getX());
        AnchorPane.setTopAnchor(this, start.getY());
    }
}