package entity;

import entity.FoodEntities.AuBonPain;
import entity.FoodEntities.FoodMenuItem;
import entity.FoodEntities.VendingMachine;
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

    //vending
    FoodMenuItem chips = new FoodMenuItem("Chips", 1.50, FoodType.SIDE);
    FoodMenuItem candyBar = new FoodMenuItem("Candy Bar", 0.99, FoodType.SIDE);
    FoodMenuItem skittles = new FoodMenuItem("Skittles", 0.99, FoodType.SIDE);
    FoodMenuItem pretzels = new FoodMenuItem("Pretzels",0.99,FoodType.SIDE);
    FoodMenuItem penuts = new FoodMenuItem("Penuts", 0.75, FoodType.SIDE);
    FoodMenuItem granolaBar = new FoodMenuItem("Granola Bar", 1.00,FoodType.SIDE);



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

    @Test
    public void testvendingMenu(){
        VendingMachine vendingMachine = new VendingMachine();
        ObservableList<FoodMenuItem> vMenu = vendingMachine.getMenu();
        ObservableList<FoodMenuItem> menuItems = FXCollections.observableArrayList();

        LinkedList<String> abpNames = new LinkedList<>();
        for(FoodMenuItem item: vMenu){
            abpNames.add(item.getName());
        }

        LinkedList<String> menuNames = new LinkedList<>();
        menuNames.add("Chips");
        menuNames.add("Candy Bar");
        menuNames.add("Skittles");
        menuNames.add("Pretzels");
        menuNames.add("Penuts");
        menuNames.add("Granola Bar");


        assertEquals(menuNames,abpNames);
    }
}
