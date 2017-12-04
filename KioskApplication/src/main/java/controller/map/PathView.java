package controller.map;

import database.objects.Edge;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;

public class PathView extends AnchorPane {

    Edge edge;

    Point2D start, end;

    @FXML
    AnchorPane container;
    @FXML
    Line line;


    public PathView(Edge edge, Point2D start, Point2D end) {
        this.edge = edge;
        this.start = start;
        this.end = end;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PathView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        line.setEndX(end.getX() - start.getX());
        line.setEndY(end.getY() - start.getY());

        AnchorPane.setLeftAnchor(container, start.getX());
        AnchorPane.setTopAnchor(container, start.getY());

        Circle circle = new Circle(500);
        circle.setStyle("-fx-background-color: #ffffff;");

        PathTransition navigationTransition = new PathTransition();
        navigationTransition.setNode(circle);
        navigationTransition.setDuration(Duration.seconds(3));
        navigationTransition.setPath(line);
        navigationTransition.setCycleCount(PathTransition.INDEFINITE);
        navigationTransition.play();
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }
}
