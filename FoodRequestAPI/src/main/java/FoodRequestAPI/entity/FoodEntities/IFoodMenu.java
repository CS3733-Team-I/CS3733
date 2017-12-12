package FoodRequestAPI.entity.FoodEntities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import FoodRequestAPI.utility.FoodType;

public abstract class IFoodMenu {
    ObservableList<FoodMenuItem> menuItems;

    public IFoodMenu() {
        menuItems = FXCollections.observableArrayList();
    }

    public ObservableList<FoodMenuItem> getMenu() {
        return menuItems;
    }


    public ObservableList<FoodMenuItem> getFoodType(FoodType type) {
        ObservableList<FoodMenuItem> foodList = FXCollections.observableArrayList();
        for (FoodMenuItem item: menuItems){
            if(item.getType().equals(type)){
                foodList.add(item);
            }
        }
        return foodList;
    }

}
