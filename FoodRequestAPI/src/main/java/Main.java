import com.jfoenix.controls.JFXTabPane;
import controller.RequestManagerController;
import controller.RequestSubmitterController;
import entity.LoginEntity;
import entity.MapEntity;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utility.KioskPermission;
import utility.csv.CsvFileUtil;
import utility.request.RequestType;

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

        AnchorPane root = (AnchorPane) new RequestSubmitterController(destNodeID, originNodeID).getContentView();

        Stage primaryStage = new Stage();
        primaryStage.setX(xcoord);
        primaryStage.setY(ycoord);
        primaryStage.setTitle("Food Request Submitter");

        Scene mainScene = new Scene(root, windowWidth, windowLength);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}