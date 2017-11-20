package controller;

import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import database.objects.Node;
import entity.Administrator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;
import java.util.HashMap;

public class MainWindowController {

    @FXML Button switchButton;
    @FXML AnchorPane contentWindow;
    @FXML AnchorPane loginPopup;
    @FXML Label lbAdminInfo;

    @FXML JFXTabPane tabPane;

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
                switchButton.setText("Admin viewLoginScreen");
                switchButton.requestFocus();
                //serviceTab.setDisable(true);
                //managerTab.setDisable(true);
                //builderTab.setDisable(true);
                break;
            default:
                break;
        }

        javafx.scene.Node contentView = controller.getContentView();

        // Display view with new controller
        contentWindow.getChildren().clear();
        contentWindow.getChildren().add(mapView);
        contentWindow.getChildren().add(contentView);

        // Fit sidebar to window
        AnchorPane.setTopAnchor(contentView, 0.0);
        AnchorPane.setBottomAnchor(contentView, 0.0);
        AnchorPane.setLeftAnchor(contentView, 0.0);

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

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab oldValue, Tab newValue) {
                switch (newValue.getText()) { // TODO make this more modular/language independent
                    case "Map":
                        switchToScreen(ApplicationScreen.PATHFINDING);
                        break;
                    case "Map Builder":
                        switchToScreen(ApplicationScreen.ADMIN_MENU);
                        break;
                    case "Request Manager":
                        switchToScreen(ApplicationScreen.ADMIN_VIEWREQUEST);
                        break;
                    case "Request Submit":
                        // TODO implement new request screen
                        //switchToScreen(ApplicationScreen.ADMIN_MENU);
                        break;
                    case "Settings":
                        // TODO implement settings screen
                        //switchToScreen(ApplicationScreen.ADMIN_MENU);
                        break;
                }
            }
        });

        this.switchToScreen(ApplicationScreen.PATHFINDING);
    }

    @FXML
    private void viewLoginScreen() throws IOException{
        // TODO this isn't great OO, rewrite at some point
        LoginController loginController = new LoginController(this);
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/view/AdminLoginWindow.fxml"));
        loader.setController(loginController);
        loginPopup.getChildren().clear();
        loginPopup.getChildren().add(loader.load());
        loginController.tfEmail.requestFocus();
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
                this.viewLoginScreen();
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
