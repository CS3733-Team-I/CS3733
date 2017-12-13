package entity.FoodEntities;

import utility.FoodType;

public class PatsPlace extends IFoodMenu {


    public PatsPlace() {
        FoodMenuItem burger = new FoodMenuItem("Burger", 7.99, FoodType.ENTREE);
        FoodMenuItem chburger = new FoodMenuItem("Cheese Burger", 8.49, FoodType.ENTREE);
        FoodMenuItem bBurger = new FoodMenuItem("Bacon Burger", 9.99, FoodType.ENTREE);
        FoodMenuItem pizza = new FoodMenuItem("Cheese Pizza", 12.25, FoodType.ENTREE);
        FoodMenuItem fries = new FoodMenuItem("French Fries", 2.49, FoodType.SIDE);
        FoodMenuItem nachos = new FoodMenuItem("Nachos", 2.99, FoodType.SIDE);
        FoodMenuItem potato = new FoodMenuItem("Potato", 2.75, FoodType.SIDE);
        FoodMenuItem water = new FoodMenuItem("Water", 1.49, FoodType.DRINK);
        FoodMenuItem lemonade = new FoodMenuItem("Lemonade", 2.49, FoodType.DRINK);
        FoodMenuItem soda = new FoodMenuItem("Soda", 1.99, FoodType.DRINK);

        menuItems.addAll(burger,chburger,bBurger,pizza,fries,nachos,potato,water,lemonade,soda);

    }

}
