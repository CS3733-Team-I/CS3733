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
import entity.SystemSettings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import utility.ApplicationScreen;
import utility.KioskPermission;
import utility.Memento.MainWindowMemento;
import utility.node.NodeFloor;

import java.io.IOException;
import java.util.*;

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
    @FXML private Tab tabHelp;

    private LoginEntity loginEntity;
    private SystemSettings systemSettings;

    private MainWindowMemento memento;

    private Timer timer = new Timer();
    int countdown;
    int maxCountdown = 500;
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
        //mapController.setFloorSelector(NodeFloor.THIRD);

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

                case HELP:
                    controller = new HelpController(this, mapController);
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
        tabHelp.setText(languageBundle.getString("help"));
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
            tabHelp.setText(languageBundle.getString("help"));
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
                case "tabHelp":
                    switchToScreen(ApplicationScreen.HELP);
                    break;
            }
        });

        initializeLoginPopup();
        //initalizeHelpPopup();
        initializeTrackingTable();
        defZoom = mapController.getZoomSlider().getValue();

        checkPermissions();

        // Create Memento for reversion
        ArrayList<Tab> tabs = new ArrayList<Tab>();
        tabs.add(tabMap);
        tabs.add(tabSettings);
        String lang = "English";
        memento = new MainWindowMemento(tabs, NodeFloor.THIRD, defZoom, lang);

        // Timer initialization
        countdown = maxCountdown;
        startTimer();
    }

    /**
     * Used to run methods after the screen is displayed
     */
    public void postDisplaySetup(){
        mapController.recenterPressed();
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

    /**
     * Initial timer start function
     */
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
     * Sets the maxCountdown
     * @param i maxcount to be set
     */
    public void setTimeout(int i){
        maxCountdown = i;
    }

    /**
     * Gets the maxCountdown value
     */
    public int getMaxcountdown(){
        return maxCountdown;
    }

    /**
     * Reset time when countdown isnt at max or min values
     * (prevents from calling too often or when reset is occurring)
     */
    public void resetTimer(){
        if(countdown != maxCountdown && countdown != 0) {
            System.out.println("TIMER RESET");
            countdown = maxCountdown;
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
        if(countdown == 0){
            timeout();
            countdown = maxCountdown;
        }
        else {
            countdown--;
            //System.out.println(countdown);
        }
    }

    /**
     * Called by peSec when the countdown reaches 0
     */
    public void timeout() {
        System.out.println("TIMEOUT");
        // Close the Login panel if open
        closeLoginPopup();
        // Change language
        SystemSettings.getInstance().setResourceBundle(memento.getLanguage());

        // Log out
        LoginEntity.getInstance().logOut();
        switchButton.setText("Staff Login");
        // Clears Tabs
        tabPane.getTabs().clear();
        // Reset tabs
        for (Tab t: memento.getTabs()) {
            tabPane.getTabs().add(t);
        }

        // Adjust node visability
        mapController.setNodesVisible(false);
        mapController.setEdgesVisible(false);

        // Reset floor
        mapController.setFloorSelector(memento.getFloor());
        // Recenter
        mapController.recenterPressed();
        // Adjust Zoom
        mapController.getZoomSlider().setValue(memento.getZoom());
        // Icon key close
        mapController.keyClosed();

        System.out.println("TIMEOUT DONE");
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
                tabPane.getTabs().add(tabHelp);

                mapController.setNodesVisible(false);
                mapController.setEdgesVisible(false);
                break;

            case EMPLOYEE:
                switchButton.setText(SystemSettings.getInstance().getResourceBundle().getString("my.stafflogoff"));

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabRM, tabRS,tabHelp);
                break;

            case SUPER_USER:
            case ADMIN:
                switchButton.setText(systemSettings.getResourceBundle().getString("my.stafflogoff"));

                //default to showing all nodes and edges
                mapController.setNodesVisible(true);
                mapController.setEdgesVisible(true);

                tabPane.getTabs().clear();
                tabPane.getTabs().addAll(tabMap, tabMB, tabRM, tabRS, tabSettings,tabHelp);
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
       // contentWindow.getChildren().add(helpPopup);
        contentWindow.getChildren().add(contentNode);
        contentWindow.getChildren().add(histogram);

        // Fit sidebar to window
        AnchorPane.setTopAnchor(contentNode, 0.0);
        AnchorPane.setBottomAnchor(contentNode, 0.0);
        AnchorPane.setLeftAnchor(contentNode, 0.0);

        // Reset map controller content anchor
        mapController.setContentLeftAnchor(0);

        // Reset controller's view
        controller.resetScreen();

        this.currentScreen = screen;
    }


    /**
     * initalizes the login pop up window using FXML loader and setting as the top anchor plane
     * @throws IOException
     */
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
        mbc.addConnectionByNodes(Integer.parseInt(nodeID1), Integer.parseInt(nodeID2));
    }

    /**
     * Will run on program close by X in top right
     */
    @FXML
    public void shutdown(){
        System.out.println("SHUTDOWN");
        timer.cancel();
    }
}
