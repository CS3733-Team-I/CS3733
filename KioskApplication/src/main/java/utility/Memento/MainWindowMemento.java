package utility.Memento;

import javafx.scene.control.Tab;

import java.util.ArrayList;

public class MainWindowMemento {
    public ArrayList<Tab> tabs;

    public MainWindowMemento(ArrayList<Tab> tabs) {
        this.tabs = tabs;
    }

    public MainWindowMemento getState(){
        return this;
    }

    public void setState(ArrayList<Tab> tabs){
        this.tabs = tabs;
    }

}
