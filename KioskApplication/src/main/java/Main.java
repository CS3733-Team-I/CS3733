import email.Email;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        /*
        MapEntity.getInstance().readAllFromDatabase();
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCSVs();

        SystemSettings.getInstance();

        Parent root = FXMLLoader.load(getClass().getResource("/view/MainWindowView.fxml"));
        primaryStage.setTitle("Iteration 2");

        Scene mainScene = new Scene(root, 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setScene(mainScene);
        primaryStage.show();
        */

        Email email = new Email("jflparrick@gmail.com", "jfparrick@wpi.edu");
        email.setBody("This is a test");
        email.setSubject("TEST");
        Sender.sendEmail(email);
    }

    public static void main(String[] args) {
        launch(args);
    }
}