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

import javax.swing.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        int progress =0;
        // Throw a nice little title page up on the screen first
        SplashScreen splash = new SplashScreen();
        //Splash Screen
        splash.showSplash();
        MapEntity.getInstance().readAllFromDatabase();
        splash.propertyChange(10);
        if (MapEntity.getInstance().getAllNodes().size() == 0)
            CsvFileUtil.getInstance().readAllCsvs();
        splash.propertyChange(20);
        SystemSettings.getInstance();
        MainWindowController mainWindowController = new MainWindowController();
        splash.propertyChange(30);
        FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("/view/MainWindowView.fxml"));
        splash.propertyChange(40);
        mainWindowLoader.setController(mainWindowController);
        splash.propertyChange(50);
        mainWindowLoader.load();
        splash.propertyChange(60);
        primaryStage.setTitle("Final Iteration");
        splash.propertyChange(70);
        Scene mainScene = new Scene(mainWindowLoader.getRoot(), 1280, 720);
        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        splash.propertyChange(80);
        stylesheets.addAll(getClass().getResource("/css/application.css").toExternalForm());
        splash.propertyChange(90);
        primaryStage.setScene(mainScene);
        splash.propertyChange(100);
        primaryStage.show();
        mainWindowController.postDisplaySetup();
        EmailSender.init();
        /*Email email = new Email.Builder("jflparrick@gmail.com")
                .setAttachment(null)
                .setBody("THIS IS A TEST")
                .setFrom("me")
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
        //splash.propertyChange(progress++);
        splash.Exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
