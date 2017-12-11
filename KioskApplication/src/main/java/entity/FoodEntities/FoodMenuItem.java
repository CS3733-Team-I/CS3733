package entity.FoodEntities;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleBooleanProperty;
import utility.FoodType;

public class FoodMenuItem extends RecursiveTreeObject<FoodMenuItem> {
    String name;
    double cost;
    FoodType type;
    SimpleBooleanProperty selected;

    public FoodMenuItem(String name, double cost, FoodType type) {
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.selected = new SimpleBooleanProperty(false);
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public FoodType getType() { return type; }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }
}
