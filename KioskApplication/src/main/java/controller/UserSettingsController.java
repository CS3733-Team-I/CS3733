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
    private JFXTextField passwordBoxNew;

    @FXML
    void initialize(){
        usernameBox.setText(LoginEntity.getInstance().getUserName());
    }

    @FXML
    void onUserSave() {
        if (LoginEntity.getInstance().updatePassword(passwordBoxNew.getText(), passwordBoxOld.getText())){
            System.out.println("Password Changed!");
        }
    }

}