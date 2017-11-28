package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
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

public class MainWindowController {

    @FXML private AnchorPane contentWindow;
          private javafx.scene.Node contentNode;
    @FXML private BorderPane loginPopup;
    @FXML private JFXButton switchButton;

    @FXML private JFXTabPane tabPane;
    @FXML private Tab tabMap;
    @FXML private Tab tabMB;
    @FXML private Tab tabRS;
    @FXML private Tab tabRM;
    @FXML private Tab tabSettings;

    private LoginEntity loginEntity;

    private ApplicationScreen currentScreen = ApplicationScreen.PATHFINDING;

    private AnchorPane mapView;
    private MapController mapController;

    private HashMap<ApplicationScreen, ScreenController> controllers;

    public MainWindowController() {
        loginEntity = LoginEntity.getInstance();
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

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
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
    }

    //checks permissions of user and adjusts visible tabs and screens
    void checkPermissions() {
        switch (loginEntity.getCurrentPermission()) {
            case NONEMPLOYEE:
                switchButton.setText("Staff Login");

                //hides all but the Map tab from non logged in users
                tabPane.getTabs().clear();
                tabPane.getTabs().add(tabMap);

                mapController.showEdgesBox.setSelected(false);
                mapController.showNodesBox.setSelected(false);
                break;

            case EMPLOYEE:
                switchButton.setText("Logoff");

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabRS);
                break;

            case SUPER_USER:
            case ADMIN:
                switchButton.setText("Logoff");

                //default to showing all nodes and edges
                mapController.showEdgesBox.setSelected(true);
                mapController.showNodesBox.setSelected(true);

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabMB, tabRM, tabRS, tabSettings);
                break;
        }
    }

    void switchToScreen(ApplicationScreen screen) {
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
        controllers.get(currentScreen).onMapNodeClicked(n);
    }

    public void onMapEdgeClicked(Edge e) {
        controllers.get(currentScreen).onMapEdgeClicked(e);
    }

    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
        controllers.get(currentScreen).onMapLocationClicked(e, location);
    }

    public void onMapFloorChanged(NodeFloor selectedFloor) {
        controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
    }
}
