package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import entity.LoginEntity;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import utility.KioskPermission;

import java.io.IOException;

public class LoginController {

    @FXML
    private JFXButton cancelButton;
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

    //To get employee info, construct a new Login Controller
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
        parent.resetTimer();
    }

    @FXML
    public void OnLoginClicked() throws IOException {
        if(l.logIn(tfEmail.getText(),pfPassword.getText())) {
            resetFields();
            parent.closeLoginPopup();
            parent.checkPermissions();
        }
        else {
            errorMsg.setVisible(true);
        }
        parent.resetTimer();
    }

    @FXML
    public void OnBackClicked () throws IOException {
        resetFields();
        disableCancelButton(true);
        parent.closeLoginPopup();
        parent.resetTimer();
    }

    @FXML
    public void resetTimer(){
        parent.resetTimer();
    }

    //Helper function for resetting the internals of the employee popup
    private void resetFields(){
        tfEmail.clear();
        pfPassword.clear();
        errorMsg.setVisible(false);
    }

    public void disableCancelButton(boolean disabled){
        cancelButton.setCancelButton(!disabled);
    }
}
