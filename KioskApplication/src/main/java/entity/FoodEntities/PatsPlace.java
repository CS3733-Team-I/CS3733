package entity.FoodEntities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utility.FoodType;

public class PatsPlace extends IFoodMenu {


    public PatsPlace() {
        FoodMenuItem burger = new FoodMenuItem("Burger", 7.99, FoodType.ENTREE);
        FoodMenuItem chburger = new FoodMenuItem("Cheese Burger", 8.49, FoodType.ENTREE);
        FoodMenuItem bBurger = new FoodMenuItem("Bacon Burger", 9.99, FoodType.ENTREE);
        FoodMenuItem pizza = new FoodMenuItem("Cheese Pizza", 12.00, FoodType.ENTREE);
        FoodMenuItem fries = new FoodMenuItem("French Fries", 2.50, FoodType.SIDE);
        FoodMenuItem nachos = new FoodMenuItem("Nachos", 3.00, FoodType.SIDE);
        FoodMenuItem potato = new FoodMenuItem("Potato", 2.75, FoodType.SIDE);
        FoodMenuItem water = new FoodMenuItem("Water", 1.50, FoodType.DRINK);
        FoodMenuItem lemonade = new FoodMenuItem("Lemonade", 2.50, FoodType.DRINK);
        FoodMenuItem soda = new FoodMenuItem("Soda", 1.99, FoodType.DRINK);

        menuItems.addAll(burger,chburger,bBurger,pizza,fries,nachos,potato,water,lemonade,soda);

    }

}
