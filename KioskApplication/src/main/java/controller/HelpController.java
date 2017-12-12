package controller;

import com.jfoenix.controls.JFXTabPane;
import entity.LoginEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import utility.ApplicationScreen;

import java.io.IOException;

public class HelpController {

    @FXML protected JFXTabPane helpTabPlane;
    @FXML Tab mapHelpTab;
    @FXML Tab requestHelpTab;
    @FXML Tab settingsHelpTab;
    @FXML Tab mapBuilderHelp;
    @FXML Tab adminHelpTab;

    MainWindowController parent;
    LoginEntity loginEntity;

    //constructor for new help screen
    public HelpController(MainWindowController parent) {
        this.parent = parent;
        loginEntity = LoginEntity.getInstance();
    }
    @FXML
    protected void initialize() throws IOException {
       // helpTabPlane.
/*
        helpTabPlane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.getId().toString()) {
                case "Map Help":
                    break;
                case "tabMB":
                    break;
                case "tabRM":
                    break;
                case "tabRS":
                    break;
                case "tabSettings":
                    break;
            }
        });*/
    }

    @FXML
    public void onHelpTabSelection(){
        //helpTabPlane.getTabs();

    }

}
