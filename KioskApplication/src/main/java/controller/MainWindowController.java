package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import controller.map.MapBuilderController;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import entity.SystemSettings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import utility.ApplicationScreen;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainWindowController {

    @FXML private AnchorPane contentWindow;
          private javafx.scene.Node contentNode;
          private LoginController loginController;
    @FXML private BorderPane loginPopup;
          private RequestTrackingDataController reqTrackController;
    @FXML private BorderPane histogram;
    @FXML private JFXButton switchButton;

    @FXML protected JFXTabPane tabPane;
    @FXML private Tab tabMap;
    @FXML private Tab tabMB;
    @FXML private Tab tabRS;
    @FXML private Tab tabRM;
    @FXML private Tab tabSettings;

    private LoginEntity loginEntity;
    private SystemSettings systemSettings;

    private ApplicationScreen currentScreen = ApplicationScreen.PATHFINDING;

    private AnchorPane mapView;
    private MapController mapController;

    private HashMap<ApplicationScreen, ScreenController> controllers;

    public MainWindowController() {
        systemSettings = SystemSettings.getInstance();
        loginEntity = LoginEntity.getInstance();
        controllers = new HashMap<>();
        mapView = new AnchorPane();
    }

    @FXML
    protected void initialize() throws IOException {
        // Initialize MapView with MapController
        mapController = new MapController();
        mapController.setParent(this);
        ResourceBundle languageBundle= systemSettings.getResourceBundle();
        FXMLLoader mapPaneLoader = new FXMLLoader(getClass().getResource("/view/MapView.fxml"));
        mapPaneLoader.setRoot(mapView);
        mapPaneLoader.setController(mapController);
        mapPaneLoader.load();

        // Default to third floor
        mapController.setFloorSelector(NodeFloor.THIRD);

        // Pre-load all controllers/views
        for (ApplicationScreen screen : ApplicationScreen.values()) {
            ScreenController controller = null;

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

            if (controller != null) {
                // load content view
                controller.getContentView();

                // cache controller
                controllers.put(screen, controller);
            }
        }

        tabMap.setText(languageBundle.getString("my.map"));
        tabMB.setText(languageBundle.getString("my.mapbuilder"));
        tabRM.setText(languageBundle.getString("my.requestmanager"));
        tabRS.setText(languageBundle.getString("my.requestsubmit"));
        tabSettings.setText(languageBundle.getString("my.setting"));
        // attaches observer to the systemSettings
        systemSettings.addObserver((o, arg) -> {
            ResourceBundle rB = systemSettings.getResourceBundle();
            switch (loginEntity.getCurrentPermission()) {
                case NONEMPLOYEE:
                    switchButton.setText(systemSettings.getResourceBundle().getString("my.stafflogin"));
                    break;

                case EMPLOYEE:
                case SUPER_USER:
                case ADMIN:
                    switchButton.setText(systemSettings.getResourceBundle().getString("my.stafflogoff"));
                    break;
            }

            tabMap.setText(languageBundle.getString("my.map"));
            tabMB.setText(languageBundle.getString("my.mapbuilder"));
            tabRM.setText(languageBundle.getString("my.requestmanager"));
            tabRS.setText(languageBundle.getString("my.requestsubmit"));
            tabSettings.setText(languageBundle.getString("my.setting"));
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.getId().toString()) {
                case "tabMap":
                    switchToScreen(ApplicationScreen.PATHFINDING);
                    break;
                case "tabMB":
                    switchToScreen(ApplicationScreen.MAP_BUILDER);
                    break;
                case "tabRM":
                    switchToScreen(ApplicationScreen.REQUEST_MANAGER);
                    break;
                case "tabRS":
                    switchToScreen(ApplicationScreen.REQUEST_SUBMITTER);
                    break;
                case "tabSettings":
                    switchToScreen(ApplicationScreen.ADMIN_SETTINGS);
                    break;
            }
        });

        initializeLoginPopup();
        initializeTrackingTable();

        checkPermissions();
    }

    private void initializeTrackingTable() throws IOException{
        reqTrackController = new RequestTrackingDataController(this);
        FXMLLoader reqTrackingLoader = new FXMLLoader(getClass().getResource("/view/RequestTrackingDataView.fxml"));
        reqTrackingLoader.setController(reqTrackController);
        javafx.scene.Node reqTrackingView = reqTrackingLoader.load();
        this.histogram = new BorderPane();

        AnchorPane.setTopAnchor(histogram, 0.0);
        AnchorPane.setLeftAnchor(histogram, 0.0);
        AnchorPane.setBottomAnchor(histogram, 0.0);
        AnchorPane.setRightAnchor(histogram, 0.0);
        histogram.setCenter(reqTrackingView);
        histogram.setVisible(false);
    }

    public void closeRequestTrackingTable(){
        histogram.setVisible(false);
        this.switchButton.setDisable(false);
        this.tabPane.setDisable(false);
        this.tabRM.setDisable(false);
        this.contentNode.setDisable(false);
        this.mapView.setDisable(false);
    }

    public void openRequestTrackingTable(){
        reqTrackController.disableCancelButton(false);
        histogram.setVisible(true);
        this.switchButton.setDisable(true);
        this.tabPane.setDisable(true);
        this.tabRM.setDisable(true);
        this.contentNode.setDisable(true);
        this.mapView.setDisable(true);
    }

    //checks permissions of user and adjusts visible tabs and screens
    void checkPermissions() {
        switch (loginEntity.getCurrentPermission()) {
            case NONEMPLOYEE:
                switchButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.stafflogin"));

                //hides all but the Map tab from non logged in users
                tabPane.getTabs().clear();
                tabPane.getTabs().add(tabMap);
                tabPane.getTabs().add(tabSettings);

                mapController.setNodesVisible(false);
                mapController.setEdgesVisible(false);
                break;

            case EMPLOYEE:
                switchButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.stafflogoff"));

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabRM, tabRS);
                break;

            case SUPER_USER:
            case ADMIN:
                switchButton.setText(systemSettings.getResourceBundle().getString("my.stafflogoff"));

                //default to showing all nodes and edges
                mapController.setNodesVisible(true);
                mapController.setEdgesVisible(true);

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabMB, tabRM, tabRS, tabSettings);
                break;
        }
    }

    public void switchToScreen(ApplicationScreen screen) {
        ScreenController currentScreen = controllers.get(this.currentScreen);
        if (currentScreen != null) {
            currentScreen.onScreenChanged();
        }

        ScreenController controller = controllers.get(screen);

        contentNode = controller.getContentView();

        // Display view with new controller
        contentWindow.getChildren().clear();
        contentWindow.getChildren().add(mapView);
        contentWindow.getChildren().add(loginPopup);
        contentWindow.getChildren().add(contentNode);
        contentWindow.getChildren().add(histogram);

        // Fit sidebar to window
        AnchorPane.setTopAnchor(contentNode, 0.0);
        AnchorPane.setBottomAnchor(contentNode, 0.0);
        AnchorPane.setLeftAnchor(contentNode, 0.0);

        // Reset controller's view
        controller.resetScreen();

        this.currentScreen = screen;
    }

    private void initializeLoginPopup() throws IOException{
        loginController = new LoginController(this);
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
        loginController.disableCancelButton(true);
    }

    void closeLoginPopup(){
        this.loginPopup.setVisible(false);
        this.switchButton.setDisable(false);
        this.tabPane.setDisable(false);
        this.tabMap.setDisable(false);
        this.contentNode.setDisable(false);
        this.mapView.setDisable(false);
    }

    @FXML
    private void openLoginPopup() throws IOException{
        loginController.disableCancelButton(false);
        this.loginPopup.setVisible(true);
        this.switchButton.setDisable(true);
        this.tabPane.setDisable(true);
        this.tabMap.setDisable(true);
        this.contentNode.setDisable(true);
        this.mapView.setDisable(true);
    }

    @FXML
    public void switchButtonClicked() throws IOException {
        switch (loginEntity.getCurrentPermission()) {
            case SUPER_USER:
            case ADMIN:
            case EMPLOYEE:
                loginEntity.logOut();
                checkPermissions();
                break;
            case NONEMPLOYEE:
                this.openLoginPopup();
                break;
        }
    }

    public void onMapNodeClicked(Node n) {
        if (controllers.containsKey(currentScreen))
            controllers.get(currentScreen).onMapNodeClicked(n);
    }

    public void onMapEdgeClicked(Edge e) {
        if (controllers.containsKey(currentScreen))
            controllers.get(currentScreen).onMapEdgeClicked(e);
    }

    public void onMapLocationClicked(javafx.scene.input.MouseEvent e) {
        if (controllers.containsKey(currentScreen))
            controllers.get(currentScreen).onMapLocationClicked(e);
    }

    public void onMapFloorChanged(NodeFloor selectedFloor) {
        if (controllers.containsKey(currentScreen))
            controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
    }

    protected String getCurrentTabName() {
        return tabPane.getSelectionModel().getSelectedItem().getText();
    }

    public void nodesConnected(String nodeID1, String nodeID2) {
        MapBuilderController mbc = (MapBuilderController)this.controllers.get(ApplicationScreen.MAP_BUILDER);
        mbc.addConnectionByNodes(nodeID1, nodeID2);
    }
}
