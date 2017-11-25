package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import entity.Administrator;
import entity.AdministratorList;
import entity.LoginEntity;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import utility.ApplicationScreen;
import utility.KioskPermission;

import java.io.IOException;

public class LoginController {

    @FXML
    private JFXTextField tfEmail;
    @FXML
    private JFXPasswordField pfPassword;
    @FXML
    private Label errorMsg;
    @FXML
    private AnchorPane loginAnchor;

    MainWindowController parent;
    LoginEntity l;

    //To get login info, construct a new Login Controller
    public LoginController(MainWindowController parent) {
        this.parent = parent;
        l= LoginEntity.getInstance();
    }

    @FXML
    public void initialize() {

        tfEmail.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    tfEmail.validate();
                }
            }
        });
        pfPassword.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue) {
                    pfPassword.validate();
                }
            }
        });

        //Puts focus on email textfield
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tfEmail.requestFocus();
            }
        });
    }

    @FXML
    public void OnLoginClicked() throws IOException {
        KioskPermission access = l.validate(tfEmail.getText(),pfPassword.getText());
        if(access != KioskPermission.NONEMPLOYEE) {
            resetFields();
            parent.closeLoginPopup();
            parent.checkPermissions();
        }
        else {
            errorMsg.setVisible(true);
        }
    }

    @FXML
    public void OnBackClicked () throws IOException {
        resetFields();
        parent.closeLoginPopup();
    }

    //Helper function for resetting the internals of the login popup
    private void resetFields(){
        tfEmail.clear();
        pfPassword.clear();
        errorMsg.setVisible(false);
    }

    public double getLoginAnchorWidth() {
        return loginAnchor.getWidth();
    }

    public double getLoginAnchorHeight() {
        return loginAnchor.getHeight();
    }
}
