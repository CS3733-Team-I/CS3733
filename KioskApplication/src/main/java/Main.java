import controller.MainWindowController;
import email.EmailSender;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utility.csv.CsvFileUtil;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCsvs();

        SystemSettings.getInstance();

        MainWindowController mainWindowController = new MainWindowController();
        FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));
        mainWindowLoader.setController(mainWindowController);
        mainWindowLoader.load();
        primaryStage.setTitle("Final Iteration");

        Scene mainScene = new Scene(mainWindowLoader.getRoot(), 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
        mainWindowController.postDisplaySetup();

        EmailSender.init();
        /*Email email = new Email.Builder("jflparrick@gmail.com")
                .setAttachment(null)
                .setBody("THIS IS A TEST")
                .setSubject("TEST")
                .build();
        SMS sms = new SMS.Builder("2077455316", Providers.VERIZON)
                .setAttachment(null)
                .setBody("THIS IS A TEST")
                .setSubject("TEST")
                .setFrom("me")
                .build();
        EmailSender.sendEmail(sms);
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
