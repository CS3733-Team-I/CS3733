package KioskApplication.controller;

import javafx.beans.DefaultProperty;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

import static KioskApplication.controller.AdminWindowController.SidebarType.SIDEBAR_MENU;

public class AdminWindowController extends MapWindowController {

    String curr_admin_email;

    public enum SidebarType {
        SIDEBAR_ADD,
        SIDEBAR_EDIT,
        SIDEBAR_MENU,
        SIDEBAR_INTERPRETER
    }

    AdminAddNodeController addNodeController = null;
    AdminEditNodeController editNodeController = null;
    AdminAddEdgeController addEdgeController = null;
    InterpreterRequestController interpreterRequestController = null;

    public AdminWindowController() throws IOException {
        super();

        switchTo(SIDEBAR_MENU);

        curr_admin_email = "";
        getMapController().setParent(this);
    }

    public void reset() {
        this.curr_admin_email = "";
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
                addNodeController = new AdminAddNodeController(this);
                loader.setController(addNodeController);
                break;

            case SIDEBAR_EDIT:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/editNode.fxml"));
                editNodeController = new AdminEditNodeController(this);
                loader.setController(editNodeController);
                break;

            case SIDEBAR_INTERPRETER:
                loader = new FXMLLoader(getClass().getResource("/KioskApplication/view/InterpreterRequestView.fxml"));
                interpreterRequestController = new InterpreterRequestController(this);
                loader.setController(interpreterRequestController);
                break;
        }

        if(loader != null) {
            getSidebarPane().getChildren().clear();
            getSidebarPane().getChildren().add(loader.load());
        }
    }

    void mapNodeClicked() {}
}
