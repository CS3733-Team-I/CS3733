package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import database.objects.Request;
import database.objects.Employee;
import entity.LoginEntity;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import utility.KioskPermission;
import utility.node.NodeType;
import utility.request.RequestType;
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

    @FXML private JFXComboBox<KioskPermission> permissionSelect;
    @FXML private JFXComboBox<RequestType> typeSelect;
    @FXML private JFXButton userActionButton;
    @FXML private Label userDialogLabel;

    @FXML private JFXTreeTableView<Employee> usersList;
    @FXML private GridPane userEditorPane;

    @FXML
    void initialize() {
        userEditorPane.setVisible(false);

        ArrayList<Employee> logins = LoginEntity.getInstance().getAllLogins();

        final TreeItem<Employee> root = new TreeItem<>();
        root.setExpanded(true);

        logins.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<>(employee));
        });

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

        //add items into the combobox
        permissionSelect.getItems().addAll(KioskPermission.values());
        typeSelect.getItems().addAll(RequestType.values());

    }

    @FXML
    void onAddPressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userActionButton.setText("Add");
        userDialogLabel.setText("Add User");

    }

    @FXML
    void onDeletePressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userActionButton.setText("Delete");
        userDialogLabel.setText("Delete User");

    }

    @FXML
    void onEditPressed(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(false);
        userActionButton.setText("Edit");
        userDialogLabel.setText("Edit User");

    }

    @FXML
    void onUserCancel(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(true);

    }

    @FXML
    void onUserSave(ActionEvent event) {
        // Adjust visability
        usersList.setVisible(true);

        // Add/edit/delete from database
        // Check that all fields are filled in
        if (usernameBox.getText() != null && passwordBox.getText() != null && permissionSelect.getValue() != null && typeSelect.getValue() != null) {

            // Check for already existing username

            System.out.println("User: " + usernameBox.getText());
            System.out.println("Pass: " + passwordBox.getText());
            System.out.println("Permission: " + permissionSelect.getValue().toString());
            System.out.println("Type: " + typeSelect.getValue().toString());
        }

    }
}