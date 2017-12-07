package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import controller.map.MapBuilderController;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import utility.ApplicationScreen;
import utility.KioskPermission;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;

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


    private Timer timer = new Timer();
    int countdown;
    int maxCountdown = 10;
    double defZoom;

    Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(10000),
            ae -> timeout()));

    public BooleanProperty timeout = new SimpleBooleanProperty(false);

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

        initializeLoginPopup();
        initializeTrackingTable();
        defZoom = mapController.getZoomSlider().getValue();
        countdown = maxCountdown;
        startTimer();

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

    void startTimer(){
        System.out.println("TIMER START");
        timeline.stop();
        timeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> perSec()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Reset time when countdown isnt at max or min values
     * (prevents from calling too often or when reset is occurring)
     */
    void resetTimer(){
        System.out.println("TIMER RESET?");
        if(countdown != maxCountdown && countdown != 0) {
            System.out.println("TIMER RESET");
            timeline.stop();
            timeline = new Timeline(new KeyFrame(
                    Duration.millis(1000),
                    ae -> perSec()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }

    /**
     * Called by the timeline each second
     */
    public void perSec(){
        System.out.println("perSec");
        if(countdown == 0){
            timeout();
            countdown = maxCountdown;
        }
        else {
            countdown--;
            System.out.println(countdown);
        }
    }

    /**
     * Called by peSec when the countdown reaches 0
     */
    public void timeout() {
        System.out.println("TIMEOUT");
        // Close the Login panel if open
        closeLoginPopup();
        // Log out
        LoginEntity.getInstance().logOut();
        switchButton.setText("Staff Login");
        System.out.println(switchButton.getText());
        // Clears Tabs
        tabPane.getTabs().clear();
        // Reset tabs
        tabPane.getTabs().add(tabMap);

        // Adjust node visability
        mapController.setNodesVisible(false);
        mapController.setEdgesVisible(false);

        // Reset floor
        mapController.setFloorSelector(NodeFloor.THIRD);
        // Recenter
        mapController.recenterPressed();
        // Adjust Zoom
        mapController.getZoomSlider().setValue(defZoom);

        System.out.println("TIMEOUT DONE");
    }

    //checks permissions of user and adjusts visible tabs and screens
    void checkPermissions() {
        System.out.println(loginEntity.getCurrentPermission()== KioskPermission.NONEMPLOYEE);
        switch (loginEntity.getCurrentPermission()) {
            case NONEMPLOYEE:
                System.out.println("Logged Off, Switching");
                switchButton.setText("Staff Login");

                //hides all but the Map tab from non logged in users
                tabPane.getTabs().clear();
                tabPane.getTabs().add(tabMap);

                mapController.setNodesVisible(false);
                mapController.setEdgesVisible(false);
                System.out.println("Done Switching");
                break;

            case EMPLOYEE:
                switchButton.setText("Logoff");

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabRM, tabRS);
                break;

            case SUPER_USER:
            case ADMIN:
                switchButton.setText("Logoff");

                //default to showing all nodes and edges
                mapController.setNodesVisible(true);
                mapController.setEdgesVisible(true);

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabMB, tabRM, tabRS, tabSettings);
                break;
            default:
                System.out.println("DEFAULT");
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

    /**
     * Sets default Zoom
     * @param defZoom is the default zoom
     */
    public void setDefZoom(double defZoom){
        this.defZoom = defZoom;
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

    protected String getCurrentTabName() {
        return tabPane.getSelectionModel().getSelectedItem().getText();
    }


}
