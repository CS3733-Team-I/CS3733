package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import database.objects.Employee;
import entity.LoginEntity;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class UserSettingsController {

    @FXML private JFXButton addUserButton;
    @FXML private JFXButton deleteUserButton;
    @FXML private JFXButton editUserButton;

    @FXML private JFXTextField usernameBox;
    @FXML private JFXTextField passwordBox;

    @FXML private JFXComboBox<?> permissionSelect;
    @FXML private JFXComboBox<?> typeSelect;
    @FXML private JFXButton userActionButton;
    @FXML private Label userDialogLabel;

    @FXML private JFXTreeTableView<Employee> usersList;
    private final TreeItem<Employee> root = new TreeItem<>();

    @FXML private GridPane userEditorPane;

    @FXML
    void initialize() {
        userEditorPane.setVisible(false);

        root.setExpanded(true);

        TreeTableColumn<Employee, String> usernameColumn = new TreeTableColumn<>("Username");
        usernameColumn.setPrefWidth(150);
        usernameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getUserName())
        );

        TreeTableColumn<Employee, String> permissionColumn = new TreeTableColumn<>("Permission");
        permissionColumn.setPrefWidth(150);
        permissionColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPermission().toString())
        );

        TreeTableColumn<Employee, String> serviceColumn = new TreeTableColumn<>("Service Availability");
        serviceColumn.setPrefWidth(190);
        serviceColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getServiceAbility().toString())
        );

        usersList.getColumns().setAll(usernameColumn, permissionColumn, serviceColumn);
        usersList.setRoot(root);
        usersList.setShowRoot(false);

        refreshUsers();
    }

    private void refreshUsers() {
        root.getChildren().clear();

        ArrayList<Employee> logins = LoginEntity.getInstance().getAllLogins();
        logins.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });
    }

    @FXML
    void onAddPressed(ActionEvent event) {

    }

    @FXML
    void onDeletePressed(ActionEvent event) {

    }

    @FXML
    void onEditPressed(ActionEvent event) {

    }

    @FXML
    void onUserCancel(ActionEvent event) {

    }

    @FXML
    void onUserSave(ActionEvent event) {

    }
}