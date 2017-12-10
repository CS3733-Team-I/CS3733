package entity;

import entity.FoodEntities.AuBonPain;
import entity.FoodEntities.FoodMenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;
import utility.FoodType;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class TestFoodEntity {

    FoodMenuItem filetMignon = new FoodMenuItem("Filet de Boeuf", 31.99, FoodType.ENTREE);
    FoodMenuItem cassoulet = new FoodMenuItem("Cassoulet",18.99, FoodType.ENTREE);
    FoodMenuItem souffle = new FoodMenuItem("Chocolate Souffle", 8.99, FoodType.SIDE);
    FoodMenuItem flamiche = new FoodMenuItem("Flamiche", 7.99, FoodType.ENTREE);
    FoodMenuItem soup = new FoodMenuItem("Soupe a l'ognon", 3.99, FoodType.SIDE);
    FoodMenuItem pastis = new FoodMenuItem("Pastis", 2.00, FoodType.DRINK);
    FoodMenuItem waterBottle = new FoodMenuItem("Water Bottle", 1.50, FoodType.DRINK);
    FoodMenuItem wine = new FoodMenuItem("Chateau Peymouton", 21.99, FoodType.DRINK);

    @Test
    public void testABPMenu(){
        AuBonPain auBonPain = new AuBonPain();
        ObservableList<FoodMenuItem> abpMenut = auBonPain.getMenu();
        ObservableList<FoodMenuItem> menuItems = FXCollections.observableArrayList();

        LinkedList<String> abpNames = new LinkedList<>();
        for(FoodMenuItem item: abpMenut){
            abpNames.add(item.getName());
        }

        menuItems.addAll(filetMignon, souffle, cassoulet, flamiche, soup, pastis, waterBottle, wine);

        LinkedList<String> menuNames = new LinkedList<>();
        for(FoodMenuItem item: menuItems){
            menuNames.add(item.getName());
        }

        assertEquals(menuNames,abpNames);
    }

    @Test
    public void testsides(){
        AuBonPain auBonPain = new AuBonPain();
        ObservableList<FoodMenuItem> abpSides = auBonPain.getFoodType(FoodType.SIDE);
        ObservableList<FoodMenuItem> menuItems = FXCollections.observableArrayList();

        LinkedList<String> abpNames = new LinkedList<>();
        for(FoodMenuItem item: abpSides){
            abpNames.add(item.getName());
        }

        menuItems.addAll(souffle,soup);
        LinkedList<String> menuNames = new LinkedList<>();
        for(FoodMenuItem item: menuItems){
            menuNames.add(item.getName());
        }

        assertEquals(menuNames,abpNames);
    }
}
