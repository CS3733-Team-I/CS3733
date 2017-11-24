package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import database.objects.Node;
import entity.Administrator;
import entity.Path;
import javafx.animation.*;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.util.Duration;
import utility.ApplicationScreen;
import utility.Node.NodeFloor;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainWindowController {

    @FXML JFXTabPane tabPane;

    @FXML AnchorPane contentWindow;
    @FXML AnchorPane LoginPopup;
    @FXML JFXButton switchButton;
    @FXML Tab tabMap;
    @FXML Tab tabMB;
    @FXML Tab tabRS;
    @FXML Tab tabRM;
    @FXML Tab tabSettings;
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
                case ADMIN_MENU:
                    controller = new AdminSidebarController(this, mapController);
                    break;

                case PATHFINDING:
                    controller = new PathfindingSidebarController(this, mapController);
                    break;

                case ADMIN_NODE:
                    controller = new AdminNodeController(this, mapController);
                    break;

                case ADMIN_EDGE:
                    controller = new AdminEdgeController(this, mapController);
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
                //shows all but the Map tab for logged in Users
                tabPane.getTabs().remove(tabMap);
                tabPane.getTabs().addAll(tabMB,tabRM,tabRS,tabSettings);
                break;
            case PATHFINDING:
                switchButton.setText("Staff Login");
                switchButton.requestFocus();
                //hides all but the Map tab from non logged in users
                tabPane.getTabs().removeAll(tabMap,tabMB,tabRM,tabRS,tabSettings);
                tabPane.getTabs().add(tabMap);
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
    private void login() throws IOException{
        LoginController loginController = new LoginController(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginWindow.fxml"));
        loader.setController(loginController);
        javafx.scene.Node view = loader.load();

        BorderPane loginContainer = new BorderPane();
        //AnchorPane.setTopAnchor(loginContainer, 0.0);
        //AnchorPane.setLeftAnchor(loginContainer, 0.0);
        //AnchorPane.setBottomAnchor(loginContainer, 0.0);
        //AnchorPane.setRightAnchor(loginContainer, 0.0);

        loginContainer.setCenter(view);

        contentWindow.getChildren().add(loginContainer);

        javafx.scene.shape.Path path = new javafx.scene.shape.Path();
        path.getElements().add(new MoveTo(800,-300));
        path.getElements().add(new LineTo( 800, 200));
        //path.getElements().add(new CubicCurveTo(0, 120, 0, 240, 380, 240));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(4000));
        pathTransition.setPath(path);
        pathTransition.setNode(loginContainer.getCenter());
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(Timeline.INDEFINITE);
        pathTransition.setAutoReverse(true);
        pathTransition.play();
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (currentScreen) {
            case ADMIN_VIEWREQUEST:
                /*
            case ADMIN_NODE:
            case ADMIN_EDGE:
            */
            case REQUEST_INTERFACE:
            case ADMIN_SETTINGS:
            case ADMIN_MENU:
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

    public void onMapLocationClicked(Point2D location) {
        controllers.get(currentScreen).onMapLocationClicked(location);
    }

    public void onMapFloorChanged(NodeFloor selectedFloor) {
        controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
    }
}
