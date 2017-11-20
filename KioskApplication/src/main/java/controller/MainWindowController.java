package controller;

import database.objects.Edge;
import database.objects.Node;
import entity.Administrator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;
import java.util.HashMap;

public class MainWindowController {

    @FXML Button switchButton;
    @FXML AnchorPane contentWindow;
    @FXML AnchorPane LoginPopup;
    @FXML Label lbAdminInfo;
    Administrator curr_admin;

    ApplicationScreen currentScreen = ApplicationScreen.PATHFINDING;

    AnchorPane mapView;
    MapController mapController;

    HashMap<ApplicationScreen, ScreenController> controllers;

    public MainWindowController() {
        controllers = new HashMap<>();

        mapView = new AnchorPane();
    }

    public void switchToScreen(ApplicationScreen screen) {
        ScreenController controller = controllers.get(screen);

        // Initialize controller if it doesn't exist
        if (controller == null) {
            switch (screen) {
                case ADMIN_MENU:
                    controller = new AdminSidebarController(this, mapController);
                    break;

                case PATHFINDING:
                    controller = new PathfindingSidebarController(this, mapController);
                    break;

                case ADMIN_ADD_NODE:
                    controller = new AdminAddNodeController(this, mapController);
                    break;

                case ADMIN_EDIT_NODE:
                    controller = new AdminEditNodeController(this, mapController);
                    break;

                case ADMIN_ADD_EDGE:
                    controller = new AdminAddEdgeController(this, mapController);
                    break;

                case ADMIN_DEL_EDGE:
                    controller = new AdminDeleteEdgeController(this, mapController);
                    break;

                case ADMIN_VIEWREQUEST:
                    controller = new RequestManagerController(this, mapController);
                    break;

                case ADMIN_INTERPRETER:
                    controller = new InterpreterRequestController(this, mapController);
                    break;

                default:
                    break;
            }

            controllers.put(screen, controller);
        }

        // Additional actions on screen switch
        switch (screen) {
            case ADMIN_MENU:
                switchButton.setText("Logoff");
                switchButton.requestFocus();
                break;
            case PATHFINDING:
                switchButton.setText("Admin Login");
                switchButton.requestFocus();
                break;
            default:
                break;
        }

        // Display view with new controller
        contentWindow.getChildren().clear();
        contentWindow.getChildren().add(mapView);
        contentWindow.getChildren().add(controller.getContentView());

        // Reset controller's view
        controller.resetScreen();

        currentScreen = screen;
    }

    @FXML
    protected void initialize() throws IOException
    {
        // Initialize MapView with MapController
        mapController = new MapController();
        mapController.setParent(this);


        FXMLLoader mapPaneLoader = new FXMLLoader(getClass().getResource("/view/MapView.fxml"));
        mapPaneLoader.setRoot(mapView);
        mapPaneLoader.setController(mapController);
        mapPaneLoader.load();
        mapController.ScrollGroupInit();
        this.switchToScreen(ApplicationScreen.PATHFINDING);
    }

    @FXML
    private void Login() throws IOException{
        LoginController LC = new LoginController(this);
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/view/AdminLoginWindow.fxml"));
        loader.setController(LC);
        LoginPopup.getChildren().clear();
        LoginPopup.getChildren().add(loader.load());
        LC.tfEmail.requestFocus();
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (currentScreen) {
            case ADMIN_VIEWREQUEST:
            case ADMIN_INTERPRETER:
            case ADMIN_EDIT_NODE:
            case ADMIN_DEL_EDGE:
            case ADMIN_ADD_NODE:
            case ADMIN_ADD_EDGE:
            case ADMIN_MENU:
                this.switchToScreen(ApplicationScreen.PATHFINDING);
                controllers.get(currentScreen).resetScreen();
                this.lbAdminInfo.setText("");
                break;
            case PATHFINDING:
                this.Login();
                break;
        }
    }

    public void onMapNodeClicked(Node n) {
        controllers.get(currentScreen).onMapNodeClicked(n);
    }

    public void onMapEdgeClicked(Edge e) {
        controllers.get(currentScreen).onMapEdgeClicked(e);
    }

    public void onMapLocationClicked(Point2D location) {
        controllers.get(currentScreen).onMapLocationClicked(location);
    }

    public void onMapFloorChanged(NodeFloor selectedFloor) {
        controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
    }
}
