package KioskApplication.controller;

import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

@DefaultProperty(value = "adminWindow")
public class AdminWindowController extends MapWindowController {

    public AdminWindowController() throws IOException {
        super();

        FXMLLoader loader = new FXMLLoader(MapWindowController.class.getResource("/KioskApplication/view/AdminSidebarView.fxml"));
        loader.setRoot(getSidebarPane());
        loader.setController(new AdminSidebarController());
        loader.load();

        getMapController().setParent(this);
    }

    void mapLocationClicked(double x, double y) {
        System.out.println(String.format("Admin Map Clicked: %f %f\n", x, y));
    }

    void mapNodeClicked() {}
}
