package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
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
import utility.NodeFloor;

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

                case REQUEST_INTERFACE:
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
            case ADMIN_MENU:
                switchButton.setText("Logoff");
                switchButton.requestFocus();
                break;
            case PATHFINDING:
                switchButton.setText("Employee Login");
                switchButton.requestFocus();
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
                        switchToScreen(ApplicationScreen.REQUEST_INTERFACE);
                        break;
                    case "Settings":
                        switchToScreen(ApplicationScreen.ADMIN_SETTINGS);
                        break;
                }
            }
        });

        this.switchToScreen(ApplicationScreen.PATHFINDING);

        //TODO FOR FUTURE REFERENCE, DO NOT REMOVE
        //Initialize Hamburger
//        HamburgerBackArrowBasicTransition BATransition = new HamburgerBackArrowBasicTransition(SidebarHam);
//        BATransition.setRate(-1);
//        SidebarHam.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
//            BATransition.setRate(BATransition.getRate()*-1);
//            BATransition.play();
//
//            if(Sidebar.isShown()) {
//                System.out.println("HERE1");
//                Sidebar.close();
//            }
//            else {
//                System.out.println("HERE2");
//                Sidebar.open();
//            }
//        });
    }

    @FXML
    private void Login() throws IOException{
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
            case ADMIN_VIEWREQUEST:
            case REQUEST_INTERFACE:
            case ADMIN_EDIT_NODE:
            case ADMIN_DEL_EDGE:
            case ADMIN_ADD_NODE:
            case ADMIN_ADD_EDGE:
            case ADMIN_SETTINGS:
            case ADMIN_MENU:
                this.switchToScreen(ApplicationScreen.PATHFINDING);
                controllers.get(currentScreen).resetScreen();
                currentScreen = ApplicationScreen.PATHFINDING;
                //this.lbAdminInfo.setText("");
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
