package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import entity.LoginEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class UserSettingsController {

    @FXML
    private JFXTreeTableView<?> usersList;

    @FXML
    private BorderPane userEditorPane;

    @FXML
    private JFXTextField usernameBox;

    @FXML
    private JFXTextField passwordBoxOld;

    @FXML
    private JFXButton saveButton;

    @FXML
    private Label userDialogLabel;

    @FXML
    private Label errLabel;

    @FXML
    private JFXTextField passwordBoxNew;

    @FXML
    void initialize(){
        usernameBox.setText(LoginEntity.getInstance().getUserName());
        errLabel.setText("");
    }

    @FXML
    void onUserSave() {
        if(passwordBoxOld.getText() == null || passwordBoxOld.getText().equals("")){
            errLabel.setText("Old Password Required");
        }
        else if(passwordBoxNew.getText() == null || passwordBoxNew.getText().equals("")){
            errLabel.setText("New Password Required");
        }
        else if (LoginEntity.getInstance().updatePassword(passwordBoxNew.getText(), passwordBoxOld.getText())){
            errLabel.setText("Password Changed!");
            passwordBoxOld.setText("");
            passwordBoxNew.setText("");
        }
    }

    void resetScreen() {
        usernameBox.setText("");
        passwordBoxOld.setText("");
        passwordBoxNew.setText("");
        errLabel.setText("");
    }

}