package controller.map;

import database.objects.Node;
import javafx.scene.paint.Color;

import java.util.LinkedList;

public class PathSection {
    private LinkedList<Node> nodes;
    private Color color;

    public PathSection(LinkedList<Node> nodes, Color color) {
        this.nodes = nodes;
        this.color = color;
    }

    public LinkedList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(LinkedList<Node> nodes) {
        this.nodes = nodes;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
