package entity.FoodEntities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utility.FoodType;

public class AuBonPain extends IFoodMenu {


    public AuBonPain() {
        FoodMenuItem filetMignon = new FoodMenuItem("Filet de Boeuf", 31.99, FoodType.ENTREE);
        FoodMenuItem cassoulet = new FoodMenuItem("Cassoulet",18.99, FoodType.ENTREE);
        FoodMenuItem souffle = new FoodMenuItem("Chocolate Souffle", 8.99, FoodType.SIDE);
        FoodMenuItem flamiche = new FoodMenuItem("Flamiche", 7.99, FoodType.ENTREE);
        FoodMenuItem soup = new FoodMenuItem("Soupe a l'ognon", 3.99, FoodType.SIDE);
        FoodMenuItem pastis = new FoodMenuItem("Pastis", 2.00, FoodType.DRINK);
        FoodMenuItem waterBottle = new FoodMenuItem("Water Bottle", 1.50, FoodType.DRINK);
        FoodMenuItem wine = new FoodMenuItem("Chateau Peymouton", 21.99, FoodType.DRINK);

        menuItems.addAll(filetMignon, souffle, cassoulet, flamiche, soup, pastis, waterBottle, wine);
    }
}
