package entity;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleBooleanProperty;

public class FoodMenuItem extends RecursiveTreeObject<FoodMenuItem> {
    String name;
    double cost;
    SimpleBooleanProperty selected;

    public FoodMenuItem(String name, double cost) {
        this.name = name;
        this.cost = cost;
        this.selected = new SimpleBooleanProperty(false);
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }
}
