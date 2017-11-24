package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import entity.Administrator;
import entity.AdministratorList;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import utility.ApplicationScreen;

import java.io.IOException;

public class LoginController {

    @FXML
    JFXTextField tfEmail;
    @FXML
    JFXPasswordField pfPassword;
    @FXML
    Label errorMsg;
    @FXML
    ImageView errorIcon;
    @FXML
    private AnchorPane loginAnchor;

    MainWindowController parent;
    private AdministratorList AdminList;

    //To get login info, construct a new Login Controller
    public LoginController(MainWindowController parent) {
        this.parent = parent;
        this.AdminList = new AdministratorList();
        AdminList.addAdministrator(new Administrator("boss@hospital.com", "123"));
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

        if(AdminList.isValidLogin(tfEmail.getText(), pfPassword.getText())) {
            parent.switchToScreen(ApplicationScreen.ADMIN_MENU);
            // TODO replace this
            // parent.adminWindow.curr_admin_email = tfEmail.getText(); //set the admin email field in AdminWindowController
            parent.removeLoginPopup();
            parent.currentScreen = ApplicationScreen.ADMIN_MENU;
//            parent.lbAdminInfo.setText("Logged in as" + tfEmail.getText());
        }
        else {
            errorIcon.setVisible(true);
            errorMsg.setText("Invalid Login");
        }
    }

    @FXML
    public void OnBackClicked () throws IOException {
        parent.switchToScreen(ApplicationScreen.PATHFINDING);
        parent.removeLoginPopup();
    }

    public double getLoginAnchorWidth() {
        return loginAnchor.getWidth();
    }

    public double getLoginAnchorHeight() {
        return loginAnchor.getHeight();
    }
}
