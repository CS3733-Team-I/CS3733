package FoodRequestAPI.entity.FoodEntities;

import FoodRequestAPI.utility.FoodType;

public class Cafe extends IFoodMenu {


    public Cafe() {
        FoodMenuItem coffee = new FoodMenuItem("Coffee", 1.99, FoodType.DRINK);
        FoodMenuItem decaf = new FoodMenuItem("Decaf Coffee", 1.99, FoodType.DRINK);
        FoodMenuItem muffin = new FoodMenuItem("Muffin", 1.29, FoodType.SIDE);
        FoodMenuItem donut = new FoodMenuItem("Donut", 0.99, FoodType.SIDE);
        FoodMenuItem bagel = new FoodMenuItem("Bagel with CC", 2.99, FoodType.ENTREE);
        FoodMenuItem sandwich = new FoodMenuItem("Breakfast Sandwich",5.99, FoodType.ENTREE);

        menuItems.addAll(coffee,decaf,muffin,donut,bagel,sandwich);
    }

}
