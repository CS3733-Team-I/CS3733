package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.sun.javafx.property.adapter.PropertyDescriptor;
import controller.map.MapBuilderController;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
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
        initializeListener();


        checkPermissions();
        resetTimer();
    }

    private void initializeListener(){
        timeout.addListener((observable, oldValue, newValue) -> {
            System.out.println("Listener Triggered");
            loginEntity.logOut();
            checkPermissions();
            System.out.println("Listener Finished");
        });
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

    @FXML
    void resetTimer(){
        System.out.println("TIMER RESET");
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeout.setValue(true);
                funct();
            }
        }, 10000);
    }

    public void funct(){


        System.out.println("funct");
        /*
        if(countdown != 0)
            countdown--;
        System.out.println(countdown);
        LoginEntity.getInstance().logOut();
        System.out.println("Logged Off, Switching");

        try {
            switchButton.setText("Staff Login");
            System.out.println(switchButton.getText());
            //hides all but the Map tab from non logged in users
            //tabPane.getTabs().clear();
            //tabPane.getTabs().add(tabMap);

            mapController.setNodesVisible(false);
            mapController.setEdgesVisible(false);

        }catch (IllegalStateException e) {
            System.out.println("EXCEPTION");
        }
        System.out.println("Done Switching");
        */

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

        resetTimer();

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
        resetTimer();
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
        resetTimer();
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
        resetTimer();
    }

    public void onMapNodeClicked(Node n) {
        controllers.get(currentScreen).onMapNodeClicked(n);
        resetTimer();
    }

    public void onMapEdgeClicked(Edge e) {
        controllers.get(currentScreen).onMapEdgeClicked(e);
        resetTimer();
    }

    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) {
        controllers.get(currentScreen).onMapLocationClicked(e, location);
        resetTimer();
    }

    public void onMapFloorChanged(NodeFloor selectedFloor) {
        controllers.get(currentScreen).onMapFloorChanged(selectedFloor);
        resetTimer();
    }

    protected String getCurrentTabName() {
        return tabPane.getSelectionModel().getSelectedItem().getText();
    }


}
