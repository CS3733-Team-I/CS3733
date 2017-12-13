package FoodRequestAPI;

import com.jfoenix.controls.JFXTabPane;
import FoodRequestAPI.controller.RequestManagerController;
import FoodRequestAPI.controller.RequestSubmitterController;
import FoodRequestAPI.entity.LoginEntity;
import FoodRequestAPI.entity.MapEntity;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import FoodRequestAPI.utility.KioskPermission;
import FoodRequestAPI.utility.csv.CsvFileUtil;
import FoodRequestAPI.utility.request.RequestType;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCsvs();

        if (LoginEntity.getInstance().getAllLogins().size() == 0){
            LoginEntity.getInstance().addUser("defaultuser", "Wilson", "Wong", "maps", new ArrayList<>(), KioskPermission.SUPER_USER, RequestType.GENERAL);
            LoginEntity.getInstance().addUser("defaultFood", "Henry", "Dunphy", "password", new ArrayList<>(), KioskPermission.EMPLOYEE, RequestType.FOOD);
        }


        LoginEntity.getInstance().logIn("defaultuser", "maps");
        int some = LoginEntity.getInstance().getCurrentLoginID();

        JFXTabPane tabPane = new JFXTabPane();

        Tab requestTab = new Tab();
        requestTab.setText("Request Submitter");
        requestTab.setContent(new RequestSubmitterController("", "").getContentView());

        Tab managerTab = new Tab();
        managerTab.setText("Request Manager");
        RequestManagerController managerController = new RequestManagerController();
        managerTab.setContent(managerController.getContentView());

        tabPane.getTabs().addAll(managerTab, requestTab);

        primaryStage.setTitle("Food Request Submitter");

        Scene mainScene = new Scene(tabPane, 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();

        managerTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(managerTab.isSelected()) managerController.refreshRequests();
        });
    }

    public void run(int xcoord, int ycoord, int windowWidth, int windowLength, String cssPath, String destNodeID, String originNodeID) throws Exception {
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCsvs();

        if (LoginEntity.getInstance().getAllLogins().size() == 0){
            LoginEntity.getInstance().addUser("defaultuser", "Wilson", "Wong", "maps", new ArrayList<>(), KioskPermission.SUPER_USER, RequestType.GENERAL);
            LoginEntity.getInstance().addUser("defaultFood", "Henry", "Dunphy", "password", new ArrayList<>(), KioskPermission.EMPLOYEE, RequestType.FOOD);
        }


        LoginEntity.getInstance().logIn("defaultuser", "maps");
        int some = LoginEntity.getInstance().getCurrentLoginID();

        JFXTabPane tabPane = new JFXTabPane();

        Tab requestTab = new Tab();
        requestTab.setText("Request Submitter");
        requestTab.setContent(new RequestSubmitterController("", "").getContentView());

        Tab managerTab = new Tab();
        managerTab.setText("Request Manager");
        RequestManagerController managerController = new RequestManagerController();
        managerTab.setContent(managerController.getContentView());

        tabPane.getTabs().addAll(managerTab, requestTab);

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Food Request Submitter");

        Scene mainScene = new Scene(tabPane, 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();

        managerTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(managerTab.isSelected()) managerController.refreshRequests();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}