package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import entity.Administrator;
import entity.AdministratorList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ApplicationScreen;

import java.io.IOException;

public class LoginController {

    @FXML
    JFXTextField tfEmail;
    @FXML
    JFXPasswordField pfPassword;
    @FXML
    Label errorMsg;

    MainWindowController parent;
    AdministratorList AdminList;

    //To get login info, construct a new Login Controller
    public LoginController(MainWindowController parent) {
        this.parent = parent;
        this.AdminList = new AdministratorList();
        AdminList.addAdministrator(new Administrator("boss@hospital.com", "123"));
    }

    @FXML
    public void initialize() {
        RequiredFieldValidator EmailValidator = new RequiredFieldValidator();
        RequiredFieldValidator PasswordValidator = new RequiredFieldValidator();

        tfEmail.getValidators().add(EmailValidator);
        pfPassword.getValidators().add(PasswordValidator);
        EmailValidator.setMessage("Email Required");
        PasswordValidator.setMessage("Password Required");

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


        Image invalidInputIcn = new Image(getClass().getResource("/images/invalid_input.png").toString());
        EmailValidator.setIcon(new ImageView(invalidInputIcn));
        PasswordValidator.setIcon(new ImageView(invalidInputIcn));

    }

    @FXML
    public void OnLoginClicked() throws IOException {

        if(AdminList.isValidLogin(tfEmail.getText(), pfPassword.getText())) {
            parent.switchToScreen(ApplicationScreen.MAP_BUILDER);
            // TODO replace this
            // parent.adminWindow.curr_admin_email = tfEmail.getText(); //set the admin email field in AdminWindowController
            parent.LoginPopup.getChildren().clear();
            parent.LoginPopup.getChildren().add(parent.switchButton);
            parent.currentScreen = ApplicationScreen.MAP_BUILDER;
//            parent.lbAdminInfo.setText("Logged in as" + tfEmail.getText());
        }
        else {
            errorMsg.setText("Invalid Login. ");
        }
    }

    @FXML
    public void OnBackClicked () throws IOException {
        parent.switchToScreen(ApplicationScreen.PATHFINDING);
        parent.LoginPopup.getChildren().clear();
        parent.LoginPopup.getChildren().add(parent.switchButton);
    }
}
