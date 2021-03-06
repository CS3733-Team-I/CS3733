package FoodRequestAPI.entity.FoodEntities;

import FoodRequestAPI.utility.FoodType;

public class GiftShop extends IFoodMenu {

    public GiftShop() {
        FoodMenuItem chips = new FoodMenuItem("Chips", 1.75, FoodType.SIDE);
        FoodMenuItem candyBar = new FoodMenuItem("Candy Bar", 0.99, FoodType.SIDE);
        FoodMenuItem popcorn = new FoodMenuItem("Popcorn", 4.75,FoodType.SIDE);
        FoodMenuItem bigPretz = new FoodMenuItem("Big Pretzel", 2.49, FoodType.SIDE);
        FoodMenuItem water = new FoodMenuItem("Water Bottle", 1.49, FoodType.DRINK);
        FoodMenuItem gatorade = new FoodMenuItem("Gatorade", 1.99, FoodType.DRINK);
        FoodMenuItem lemonade = new FoodMenuItem("Lemonade", 2.25, FoodType.DRINK);

        menuItems.addAll(chips,candyBar,popcorn,bigPretz,water,gatorade,lemonade);
    }
}
