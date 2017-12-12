package entity.FoodEntities;

import utility.FoodType;

public class VendingMachine extends IFoodMenu {

    public VendingMachine() {
        FoodMenuItem chips = new FoodMenuItem("Chips", 1.50, FoodType.SIDE);
        FoodMenuItem candyBar = new FoodMenuItem("Candy Bar", 0.99, FoodType.SIDE);
        FoodMenuItem skittles = new FoodMenuItem("Skittles", 0.99, FoodType.SIDE);
        FoodMenuItem pretzels = new FoodMenuItem("Pretzels",0.99,FoodType.SIDE);
        FoodMenuItem penuts = new FoodMenuItem("Penuts", 0.75, FoodType.SIDE);
        FoodMenuItem granolaBar = new FoodMenuItem("Granola Bar", 1.00,FoodType.SIDE);

        menuItems.addAll(chips,candyBar,skittles,pretzels,penuts,granolaBar);
    }
}

