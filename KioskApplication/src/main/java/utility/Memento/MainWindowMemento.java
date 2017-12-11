package utility.Memento;

import javafx.scene.control.Tab;
import utility.node.NodeFloor;

import java.util.ArrayList;

public class MainWindowMemento {
    private ArrayList<Tab> tabs;
    private NodeFloor floor;
    double zoom;
    String language;

    public MainWindowMemento(ArrayList<Tab> tabs, NodeFloor floor, double zoom, String language) {
        this.tabs = tabs;
        this.floor = floor;
        this.zoom = zoom;
        this.language = language;
    }

    public void setState(ArrayList<Tab> tabs, NodeFloor floor, double zoom, String language){
        this.tabs = tabs;
        this.floor = floor;
        this.zoom = zoom;
        this.language = language;
    }

    public NodeFloor getFloor() {
        return floor;
    }

    public ArrayList<Tab> getTabs(){
        return tabs;
    }

    public double getZoom() {
        return zoom;
    }

    public String getLanguage() {
        return language;
    }
}
