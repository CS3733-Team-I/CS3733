package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import database.objects.Node;
import entity.Administrator;
import entity.LoginEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import utility.ApplicationScreen;
import utility.KioskPermission;
import utility.Node.NodeFloor;

import java.io.IOException;
import java.util.HashMap;

public class MainWindowController {

    @FXML AnchorPane contentWindow;
    javafx.scene.Node contentNode;
    @FXML BorderPane loginPopup;
    @FXML JFXButton switchButton;

    @FXML JFXTabPane tabPane;
    @FXML Tab tabMap;
    @FXML Tab tabMB;
    @FXML Tab tabRS;
    @FXML Tab tabRM;
    @FXML Tab tabSettings;
    //@FXML Label lbAdminInfo;
    //@FXML JFXDrawer Sidebar;
    //@FXML JFXHamburger SidebarHam;

    //will be changed to refer to LoginEntity once completed
    LoginEntity l;

    ApplicationScreen currentScreen = ApplicationScreen.PATHFINDING;

    AnchorPane mapView;
    MapController mapController;

    HashMap<ApplicationScreen, ScreenController> controllers;

    public MainWindowController() {
        l= LoginEntity.getInstance();
        controllers = new HashMap<>();
        mapView = new AnchorPane();
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
            //NullPointerExceptions thrown by line below
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

        LoginController loginController = new LoginController(this);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginWindow.fxml"));
        loader.setController(loginController);
        javafx.scene.Node view = loader.load();

        this.loginPopup = new BorderPane();
        AnchorPane.setTopAnchor(loginPopup, 0.0);
        AnchorPane.setLeftAnchor(loginPopup, 0.0);
        AnchorPane.setBottomAnchor(loginPopup, 0.0);
        AnchorPane.setRightAnchor(loginPopup, 0.0);
        loginPopup.setRight(view);
        //hides the popup
        loginPopup.setVisible(false);

        checkPermissions();

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

    //checks permissions of user and adjusts visible tabs and screens
    public void checkPermissions() {
        System.out.println(l.getPermission());
        switch (l.getPermission()) {
            case NONEMPLOYEE:
                switchButton.setText("Staff Login");
                //hides all but the Map tab from non logged in users
                tabPane.getTabs().removeAll(tabPane.getTabs());//TODO: stop this specific line from throwing NullPointerExceptions
                tabPane.getTabs().add(tabMap);
                mapController.showEdgesBox.setSelected(false);
                mapController.showNodesBox.setSelected(false);
                break;
            case EMPLOYEE:
                switchButton.setText("Logoff");
                tabPane.getTabs().add(tabRS);
                break;
            case ADMIN:
                switchButton.setText("Logoff");
                //default to showing all nodes and edges
                mapController.showEdgesBox.setSelected(true);
                mapController.showNodesBox.setSelected(true);
                //shows all but the Map tab for logged in Users
                tabPane.getTabs().addAll(tabMB, tabRM, tabRS, tabSettings);
                break;
        }
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

        contentNode = controller.getContentView();

        // Display view with new controller
        contentWindow.getChildren().clear();
        contentWindow.getChildren().add(mapView);
        contentWindow.getChildren().add(loginPopup);
        contentWindow.getChildren().add(contentNode);

        // Fit sidebar to window
        AnchorPane.setTopAnchor(contentNode, 0.0);
        AnchorPane.setBottomAnchor(contentNode, 0.0);
        AnchorPane.setLeftAnchor(contentNode, 0.0);

        // Reset controller's view
        controller.resetScreen();

        this.currentScreen = screen;
    }

    protected void closeLoginPopup(){
        this.loginPopup.setVisible(false);
        this.switchButton.setDisable(false);
        this.tabPane.setDisable(false);
        this.tabMap.setDisable(false);
        this.contentNode.setDisable(false);
        this.mapView.setDisable(false);
    }

    @FXML
    private void openLoginPopup() throws IOException{
        this.loginPopup.setVisible(true);
        this.switchButton.setDisable(true);
        this.tabPane.setDisable(true);
        this.tabMap.setDisable(true);
        this.contentNode.setDisable(true);
        this.mapView.setDisable(true);

        /*//TODO: make this slide in transition code work
        double screenWidth=contentWindow.getWidth();
        javafx.scene.shape.Path path = new javafx.scene.shape.Path();
        path.getElements().add(new MoveTo(-200,0));
        path.getElements().add(new LineTo( 0, 0));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(500));
        contentWindow.getChildren().add(path);
        pathTransition.setPath(path);
        pathTransition.setNode(loginContainer.getCenter());
        pathTransition.setOrientation(PathTransition.OrientationType.NONE);
        pathTransition.setCycleCount(1);
        pathTransition.play();
        System.out.println(loginController.getLoginAnchorHeight());
        System.out.println(loginController.getLoginAnchorWidth());*/
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (l.getPermission()) {
            case ADMIN:
            case EMPLOYEE:
                l.logOut();
                checkPermissions();
                break;
            case NONEMPLOYEE:
                this.openLoginPopup();
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
//        System.out.println("controller: " + controllers);
//        System.out.println("currentScreen: " + currentScreen);
//        System.out.println("selectedFloor: "+ selectedFloor);
        controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
    }
}
