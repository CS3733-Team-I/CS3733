import email.Email;
import email.EmailSender;
import email.Providers;
import email.SMS;
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
        EmailSender.init();
        /*Email email = new Email.Builder("jflparrick@gmail.com")
                .setAttachment(null)
                .setBody("THIS IS A TEST")
                .setFrom("me")
                .setSubject("TEST")
                .build(); */
        SMS sms = new SMS.Builder("3108691781", Providers.PROJECTFI)
                .setAttachment(null)
                .setBody("THIS IS A TEST")
                .setSubject("TEST")
                .setFrom("me")
                .build();
        EmailSender.sendEmail(sms);
    }

    public static void main(String[] args) {
        launch(args);
    }
}