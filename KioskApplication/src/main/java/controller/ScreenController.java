package controller;

import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utility.node.NodeFloor;

import java.io.IOException;

public abstract class ScreenController {
    private MainWindowController parent;
    private MapController mapController;

    @FXML protected javafx.scene.Node contentView;

    public ScreenController(MainWindowController parent, MapController mapController) {
        this.parent = parent;
        this.mapController = mapController;
    }

    protected MainWindowController getParent() { return parent; }
    protected MapController getMapController() {
        return mapController;
    }

    public abstract javafx.scene.Node getContentView();
    protected javafx.scene.Node loadView(String path) {
        javafx.scene.Node view;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            loader.setController(this);
            view = loader.load();
        } catch (IOException e) {
            System.out.println("Load " + this + "view failed," + " initialize with empty view");
            e.printStackTrace();

            view = new AnchorPane(); // Initialize contentView as an empty view
        }

        return view;
    }

    public abstract void onMapLocationClicked(MouseEvent e);
    public abstract void onMapNodeClicked(Node node);
    public abstract void onMapEdgeClicked(Edge edge);
    public abstract void onMapFloorChanged(NodeFloor floor);
    //TODO temporary: to be changed Override by Map Builder
    public void addConnectionByNodes(String nodeXyz1, String nodeXyz2){}
    public boolean isNewNodeEmpty() {
        return true;
    }
    public void showFloors() {}

    public void onScreenChanged() {}
    public abstract void resetScreen();
}
