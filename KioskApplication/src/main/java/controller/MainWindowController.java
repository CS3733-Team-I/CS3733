package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import database.objects.Node;
import entity.Administrator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;

public class MainWindowController {

    @FXML JFXTabPane tabPane;

    @FXML AnchorPane contentWindow;
    @FXML AnchorPane LoginPopup;
    @FXML JFXButton switchButton;
    //@FXML Label lbAdminInfo;
    //@FXML JFXDrawer Sidebar;
    //@FXML JFXHamburger SidebarHam;
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
        ScreenController currentScreen = controllers.get(this.currentScreen);
        if (currentScreen != null) {
            currentScreen.onScreenChanged();
        }

        ScreenController controller = controllers.get(screen);

        // Initialize controller if it doesn't exist
        if (controller == null) {
            switch (screen) {
                case MAP_BUILDER:
                    controller = new MapBuilderController(this, mapController);
                    break;

                case PATHFINDING:
                    controller = new PathfindingSidebarController(this, mapController);
                    break;
                case REQUEST_MANAGER:
                    controller = new RequestManagerController(this, mapController);
                    break;

                case REQUEST_SUBMITTER:
                    controller = new RequestSubmitterController(this, mapController);
                    break;

                case ADMIN_SETTINGS:
                    controller = new SettingsController(this, mapController);
                    break;

                default:
                    break;
            }

            controllers.put(screen, controller);
        }

        // Additional actions on screen switch
        switch (screen) {
            case PATHFINDING:
                switchButton.setText("Employee login");
                switchButton.requestFocus();
                break;
            case MAP_BUILDER:
                switchButton.setText("Logoff");
                switchButton.requestFocus();
                //default to showing all nodes and edges
                mapController.showEdgesBox.setSelected(true);
                mapController.showNodesBox.setSelected(true);
                //change click on map event to be double click
                break;

            default:
                mapController.showEdgesBox.setSelected(false);
                mapController.showNodesBox.setSelected(false);
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

        this.currentScreen = screen;
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
                        switchToScreen(ApplicationScreen.MAP_BUILDER);
                        break;
                    case "Request Manager":
                        switchToScreen(ApplicationScreen.REQUEST_MANAGER);
                        break;
                    case "Request Submit":
                        switchToScreen(ApplicationScreen.REQUEST_SUBMITTER);
                        break;
                    case "Settings":
                        switchToScreen(ApplicationScreen.ADMIN_SETTINGS);
                        break;
                }
            }
        });

        this.switchToScreen(ApplicationScreen.PATHFINDING);
    }

    @FXML
    private void login() throws IOException{
        LoginController loginController = new LoginController(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminLoginWindow.fxml"));
        loader.setController(loginController);
        javafx.scene.Node view = loader.load();

        BorderPane loginContainer = new BorderPane();
        AnchorPane.setTopAnchor(loginContainer, 0.0);
        AnchorPane.setLeftAnchor(loginContainer, 0.0);
        AnchorPane.setBottomAnchor(loginContainer, 0.0);
        AnchorPane.setRightAnchor(loginContainer, 0.0);

        loginContainer.setCenter(view);

        contentWindow.getChildren().add(loginContainer);
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (currentScreen) {
            case REQUEST_MANAGER:
                /*
            case ADMIN_NODE:
            case ADMIN_EDGE:
            */
            case REQUEST_SUBMITTER:
            case ADMIN_SETTINGS:
            case MAP_BUILDER:
                this.switchToScreen(ApplicationScreen.PATHFINDING);
                controllers.get(currentScreen).resetScreen();
                currentScreen = ApplicationScreen.PATHFINDING;
                //this.lbAdminInfo.setText("");
                break;
            case PATHFINDING:
                this.login();
                break;
        }
    }

    public void onMapNodeClicked(Node n) {
        controllers.get(currentScreen).onMapNodeClicked(n);
    }

    public void onMapEdgeClicked(Edge e) {
        controllers.get(currentScreen).onMapEdgeClicked(e);
    }

    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
        controllers.get(currentScreen).onMapLocationClicked(e, location);
    }

    public void onMapFloorChanged(NodeFloor selectedFloor) {
//        System.out.println("controller: " + controllers);
//        System.out.println("currentScreen: " + currentScreen);
//        System.out.println("selectedFloor: "+ selectedFloor);
        controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
    }
}
