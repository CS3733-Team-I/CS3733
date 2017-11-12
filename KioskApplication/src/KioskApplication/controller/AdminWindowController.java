package KioskApplication.controller;

import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminWindowController extends MapWindowController {

    public enum SidebarType {
        SIDEBAR_ADD,
        SIDEBAR_EDIT,
        SIDEBAR_MENU
    }

    AdminAddNodeController addNodeController = null;

    public AdminWindowController() throws IOException {
        super();

        switchTo(SIDEBAR_MENU);

        getMapController().setParent(this);
    }

    void mapLocationClicked(double x, double y) {
        System.out.println(String.format("Admin Map Clicked: %f %f\n", x, y));

        if (addNodeController != null) addNodeController.setCoords(x, y);
    }

    public void switchTo(SidebarType sidebar) throws IOException {
        FXMLLoader loader = null;

        switch (sidebar) {
            case SIDEBAR_MENU:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/AdminSidebarView.fxml"));
                loader.setController(new AdminSidebarController(this));
                break;
            case SIDEBAR_ADD:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/addNode.fxml"));
                loader.setController(new AdminAddNodeController(this));
                break;

            case SIDEBAR_EDIT:
                break;
        }

        if(loader != null) {
            getSidebarPane().getChildren().clear();
            getSidebarPane().getChildren().add(loader.load());
        }
    }

    void mapNodeClicked() {}
}
