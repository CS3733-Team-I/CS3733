package controller;

import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import database.connection.NotFoundException;
import database.objects.Employee;
import entity.LoginEntity;
import entity.MapEntity;
import entity.SearchEntity.ISearchEntity;
import entity.SearchEntity.SearchNode;
import entity.SystemSettings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import utility.KioskPermission;
import utility.node.NodeType;
import utility.request.Language;
import utility.request.RequestType;
import utility.validators.MatchingFieldValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static utility.request.RequestType.DOCTOR;
import static utility.request.RequestType.GENERAL;
import static utility.request.RequestType.INTERPRETER;

public class EmployeeSettingsController {

    @FXML private Label employeesLabel;

    @FXML private JFXButton addUserButton;
    @FXML private JFXButton deleteUserButton;

    @FXML private GridPane addUserPane;
    @FXML private Label registerEmployeeLabel, interpreterLanguageLabel, doctorOfficeLabel;
    @FXML private Label errLabel;
    @FXML private JFXTextField firstNameBox;
    @FXML private JFXTextField lastNameBox;
    @FXML private JFXTextField usernameBox;
    @FXML private JFXPasswordField passwordBox;
    @FXML private JFXPasswordField passwordConfirmBox;
    @FXML private JFXComboBox<KioskPermission> permissionSelect;
    @FXML private JFXComboBox<RequestType> serviceSelect;
    @FXML private JFXButton addUserActionButton, addUserCancelButton;

    @FXML private VBox interpreterLanguageBox;

    @FXML private AnchorPane officePane;

    private SearchController searchController;

    private javafx.scene.Node searchView;

    @FXML private GridPane deletePane;
    @FXML private Label deleteText;
    @FXML private JFXButton deleteUserActionButton, deleteUserCancelButton;

    @FXML private GridPane additionalInformationPane;
    @FXML private VBox additionalInformationBox;

    @FXML private JFXTreeTableView<Employee> usersList;
    private final TreeItem<Employee> root = new TreeItem<>();

    Employee selectedEmployee;

    public EmployeeSettingsController(){
    }

    @FXML
    void initialize() throws IOException{
        root.setExpanded(true);

        //initialize search
        ArrayList<ISearchEntity> searchNode = new ArrayList<>();
        SystemSettings.getInstance().updateDistance();
        for(database.objects.Node targetNode : MapEntity.getInstance().getAllNodes()) {
            if(targetNode.getNodeType() != NodeType.HALL) {
                searchNode.add(new SearchNode(targetNode));
            }
        }
        searchController = new SearchController(this, searchNode);
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/view/searchView.fxml"));
        searchLoader.setController(searchController);
        searchView = searchLoader.load();
        searchController.resizeSearchbarWidth(150.0);
        searchController.setSearchFieldPromptText("Search office");
        officePane.getChildren().add(searchView);

        TreeTableColumn<Employee, String> usernameColumn = new TreeTableColumn<>("Username");
        usernameColumn.setResizable(false);
        usernameColumn.setPrefWidth(200);
        usernameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getUsername())
        );
        TreeTableColumn<Employee, String> firstNameColumn = new TreeTableColumn<>("First Name");
        firstNameColumn.setResizable(false);
        firstNameColumn.setPrefWidth(175);
        firstNameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getFirstName())
        );
        TreeTableColumn<Employee, String> lastNameColumn = new TreeTableColumn<>("Last Name");
        lastNameColumn.setResizable(false);
        lastNameColumn.setPrefWidth(175);
        lastNameColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getLastName())
        );

        TreeTableColumn<Employee, String> permissionColumn = new TreeTableColumn<>("Permission");
        permissionColumn.setResizable(false);
        permissionColumn.setPrefWidth(100);
        permissionColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getPermission().toString())
        );

        TreeTableColumn<Employee, String> serviceColumn = new TreeTableColumn<>("Service Availability");
        serviceColumn.setResizable(false);
        serviceColumn.setPrefWidth(150);
        serviceColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getServiceAbility().toString())
        );
        /*TreeTableColumn<Employee, String> optionsColumn = new TreeTableColumn<>("Options");
        optionsColumn.setResizable(false);
        optionsColumn.setPrefWidth(150);
        optionsColumn.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<Employee, String> param) ->
                        new ReadOnlyStringWrapper(param.getValue().getValue().getOptions())
        );*/

        usersList.getColumns().setAll(usernameColumn,firstNameColumn,lastNameColumn, permissionColumn, serviceColumn);
        usersList.setRoot(root);
        usersList.setShowRoot(false);

        //Tree Table listener
        usersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LoginEntity e = LoginEntity.getInstance();
                selectedEmployee = newValue.getValue();
                additionalInformationBox.getChildren().clear();
                for (String office : selectedEmployee.getOptions()) {
                    String officeName = "Not Found";
                    try {
                        officeName = MapEntity.getInstance().getNode(office).getLongName();
                    }catch(NotFoundException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Can't found Location" + office);
                        alert.setHeaderText("Error occured while looking for office");
                        alert.setContentText(ex.toString());
                        alert.showAndWait();
                    }
                    Label officeLabel = new Label(officeName);

                    additionalInformationBox.getChildren().add(officeLabel);
                }
                additionalInformationPane.setVisible(true);
                if (selectedEmployee.getServiceAbility()==INTERPRETER){
                    for (String language :
                            selectedEmployee.getOptions()) {
                        Label langLabel = new Label(Language.values()[Integer.parseInt(language)].toString());
                        additionalInformationBox.getChildren().add(langLabel);
                    }

                }
                // Don't allow deletion if the selected user is self
                if (selectedEmployee.getUsername().equals(e.getCurrentUsername())) {
                    deleteUserButton.setDisable(true);
                }
                else {
                    deleteUserButton.setDisable(false);
                }
            }
        });

        refreshUsers();

        errLabel.setText("");
        //add items into the combobox
        permissionSelect.getItems().addAll(KioskPermission.values());
        permissionSelect.getItems().remove(KioskPermission.NONEMPLOYEE); // Except NONEMPLOYEE
        serviceSelect.getItems().addAll(RequestType.values());
        serviceSelect.getItems().remove(RequestType.GENERAL);
        //Interpreter language selector setup
        //adds the language checkboxes to the language selection menu
        for (Language language: Language.values()
             ) {
            if(language!=Language.NONE) {
                JFXCheckBox langCheckBox = new JFXCheckBox(language.toString());
                langCheckBox.setPrefWidth(100.0);
                langCheckBox.setPrefHeight(25.0);
                interpreterLanguageBox.getChildren().add(langCheckBox);
            }
        }
        setupValidatorsForAddUserPane();
        //Internationalization listener
        SystemSettings.getInstance().addObserver((o, arg) -> {
            ResourceBundle rB = SystemSettings.getInstance().getResourceBundle();
            employeesLabel.setText(rB.getString("employees"));
            usernameColumn.setText(rB.getString("username"));
            firstNameColumn.setText(rB.getString("firstName"));
            lastNameColumn.setText(rB.getString("lastName"));
            permissionColumn.setText(rB.getString("permission"));
            serviceColumn.setText(rB.getString("serviceAbility"));
            //optionsColumn.setText(rB.getString("options"));
            usernameBox.setPromptText(rB.getString("username"));
            firstNameBox.setPromptText(rB.getString("firstName"));
            lastNameBox.setPromptText(rB.getString("lastName"));
            passwordBox.setPromptText(rB.getString("password"));
            passwordConfirmBox.setPromptText(rB.getString("password"));
            permissionSelect.setPromptText(rB.getString("permission"));
            serviceSelect.setPromptText(rB.getString("serviceAbility"));
            registerEmployeeLabel.setText(rB.getString("registerEmployee"));
            doctorOfficeLabel.setText(rB.getString("office"));
        });
    }

    public void refreshUsers() {
        root.getChildren().clear();
        selectedEmployee = null;

        ArrayList<Employee> logins = LoginEntity.getInstance().getAllLogins();
        logins.stream().forEach((employee) -> {
            root.getChildren().add(new TreeItem<Employee>(employee));
        });

        deleteUserButton.setDisable(true);
    }

    /**
     * Sets up input validators for the add user pane
     */
    private void setupValidatorsForAddUserPane(){
        RequiredFieldValidator firstNameVal = new RequiredFieldValidator();
        firstNameVal.setMessage("First Name Required");
        firstNameBox.getValidators().add(firstNameVal);
        firstNameBox.focusedProperty().addListener((o,oldVal,newVal)->{
            if(!newVal) firstNameBox.validate();
        });
        RequiredFieldValidator lastNameVal = new RequiredFieldValidator();
        lastNameVal.setMessage("Last Name Required");
        lastNameBox.getValidators().add(lastNameVal);
        lastNameBox.focusedProperty().addListener((o,oldVal,newVal)->{
            if(!newVal) lastNameBox.validate();
        });
        RequiredFieldValidator usernameVal = new RequiredFieldValidator();
        usernameVal.setMessage("Username Required");
        usernameBox.getValidators().add(usernameVal);
        usernameBox.focusedProperty().addListener((o,oldVal,newVal)->{
            if (!newVal) usernameBox.validate();
        });
        MatchingFieldValidator passwordConfirmVal = new MatchingFieldValidator();
        passwordConfirmVal.setMessage("Passwords not matching");
        passwordConfirmBox.getValidators().add(passwordConfirmVal);
        passwordConfirmBox.focusedProperty().addListener((o,oldVal,newVal)->{
            if (!newVal) {
                passwordConfirmVal.compareTo(passwordBox.getText());
                passwordConfirmBox.validate();
            }
        });
        RequiredFieldValidator passwordReqVal = new RequiredFieldValidator();
        passwordReqVal.setMessage("Password Required");
        passwordBox.getValidators().add(passwordReqVal);
        passwordBox.focusedProperty().addListener((o,oldVal,newVal)->{
            if (!newVal) {
                passwordBox.validate();
                passwordConfirmVal.compareTo(passwordBox.getText());
                passwordConfirmBox.validate();
            }
        });
        addUserActionButton.disableProperty().bind(
                firstNameVal.hasErrorsProperty().or(
                lastNameVal.hasErrorsProperty().or(
                usernameVal.hasErrorsProperty().or(
                passwordReqVal.hasErrorsProperty().or(
                passwordConfirmVal.hasErrorsProperty()
                )))));/*
        addUserActionButton.disableProperty().bind((firstNameBox.textProperty().isNotEmpty().and(
                lastNameBox.textProperty().isNotEmpty().and(
                usernameBox.textProperty().isNotEmpty().and(
                passwordBox.textProperty().isNotEmpty().and(
                permissionSelect.valueProperty().isNotNull().and(
                passwordConfirmBox.textProperty().isEqualTo(passwordBox.textProperty()))))))).not()
        );*/
    }

    @FXML
    void onAddPressed(ActionEvent event) {
        // Adjust visability
        closeAdditionalInformationPane();
        openAddUserPane();
        addUserActionButton.setText("Add");
        errLabel.setText("");
    }

    @FXML
    void onDeletePressed(ActionEvent event) {
        // Set delete text
        deleteText.setText("Delete " + selectedEmployee.getUsername() + "?");

        // Adjust visability
        closeAdditionalInformationPane();
        openDeleteUserPane();
    }

    @FXML
    void deleteSelectedUser(ActionEvent even) {
        // Delete user
        LoginEntity.getInstance().deleteLogin(selectedEmployee.getUsername());

        refreshUsers();

        // Adjust visability
        closeDeleteUserPane();
    }

    @FXML
    void onUserCancel(ActionEvent event) {
        closeAddUserPane();
        closeDeleteUserPane();
    }

    @FXML
    void onUserSave(ActionEvent event) {
        if(lastNameBox.validate() &&
                firstNameBox.validate() &&
                usernameBox.validate() &&
                passwordBox.validate()){

        }
        ArrayList<String> options = new ArrayList<>();
        // Check that all fields are filled in
        if (firstNameBox.getText() != null &&
                !firstNameBox.getText().equals("") &&
                !lastNameBox.getText().equals("")&&
                !usernameBox.getText().equals("") &&
                !passwordBox.getText().equals("") &&
                permissionSelect.getValue() != null &&
                //usernameBox.getText() != null &&
                //passwordBox.getText() != null &&
                serviceSelect.getValue() != null) {
            switch (serviceSelect.getValue()){
                case INTERPRETER:
                    for (Node intLangBoxItem: interpreterLanguageBox.getChildren()) {
                        if(intLangBoxItem instanceof JFXCheckBox) {
                            JFXCheckBox langBox = ((JFXCheckBox) intLangBoxItem);
                            if (langBox.isSelected()) {
                                options.add(langBox.getText());
                            }
                        }
                    }
                    //if there are no languages selected  it'll fail
                    if(options.size()==0) {
                        errLabel.setText("No languages selected for this interpreter");
                        return;
                    }
                    break;
                case DOCTOR:
                    if (searchController.getSelected()==null){
                        errLabel.setText("No office selected");
                        return;
                    }
                    else {
                        options.add(((database.objects.Node)(searchController.getSelected())).getNodeID());
                    }
                    break;
                default:
                    break;
                }
            }
            RequestType serviceAbility = GENERAL;
            if(permissionSelect.getValue()==KioskPermission.EMPLOYEE){
                serviceAbility=serviceSelect.getValue();
            }
            // Add user
            if (LoginEntity.getInstance().addUser(usernameBox.getText(),lastNameBox.getText(),firstNameBox.getText(),
                    passwordBox.getText(),options, permissionSelect.getValue(), serviceAbility)) {
                refreshUsers();
                // Adjust visability
                closeAddUserPane();
                errLabel.setText("User Added");
            }
        else{
            if(usernameBox.getText().equals("")){
                errLabel.setText("Username Required");
            }
            else if(passwordBox.getText().equals("")){
                errLabel.setText("Password Required");
            }
            else if(permissionSelect.getValue() == null){
                errLabel.setText("Permission Selection Required");
            }
            else if(serviceSelect.getValue() == null){
                errLabel.setText("User Type Select Required");
            }
        }
    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    void resetTimer(){

    }

    void resetScreen(){
        usernameBox.setText("");
        passwordBox.setText("");
        permissionSelect.setValue(null);
        serviceSelect.setValue(null);

        //reset search
        SystemSettings.getInstance().updateDistance();
        ArrayList<ISearchEntity> searchNode = new ArrayList<>();
        for(database.objects.Node targetNode : MapEntity.getInstance().getAllNodes()) {
            if(targetNode.getNodeType() != NodeType.HALL) {
                searchNode.add(new SearchNode(targetNode));
            }
        }
        searchController.reset(searchNode);
    }

    /**
     * Called by the request type JFXComboBox
     * Used currently to show or hide the interpreter language selection area and label
     */
    @FXML
    public void checkEmployeeServiceType(){
        RequestType employeeType = serviceSelect.getValue();
        officePane.setVisible(true);
        if(employeeType==INTERPRETER) {
            System.out.println(serviceSelect.getValue());
            doctorOfficeLabel.setVisible(false);
            interpreterLanguageBox.setVisible(true);
            interpreterLanguageLabel.setVisible(true);
            searchController.setVisible(false);
        }
        else if(employeeType==DOCTOR) {
            System.out.println(serviceSelect.getValue());
            interpreterLanguageBox.setVisible(false);
            interpreterLanguageLabel.setVisible(false);
            clearInterpreterLanguageBox();
            doctorOfficeLabel.setVisible(true);
            searchController.setVisible(true);
        }
        else {
            interpreterLanguageBox.setVisible(false);
            interpreterLanguageLabel.setVisible(false);
            clearInterpreterLanguageBox();
            doctorOfficeLabel.setVisible(false);
            searchController.setVisible(false);
        }
    }

    @FXML
    private void checkEmployeePermission(){
        KioskPermission permission = permissionSelect.getValue();
        if(permission==KioskPermission.EMPLOYEE) {
            serviceSelect.setVisible(true);
        }
        else {
            serviceSelect.setVisible(false);
            serviceSelect.setValue(null);
        }
    }

    /**
     * Helper method to clear the languages selected for interpreters
     */
    private void clearInterpreterLanguageBox(){
        for (Node intLangBoxItem: interpreterLanguageBox.getChildren()) {
            if (intLangBoxItem instanceof JFXCheckBox) {
                JFXCheckBox langBox = ((JFXCheckBox) intLangBoxItem);
                if (langBox.isSelected()) {
                    langBox.setSelected(false);
                }
            }
        }
    }

    /**
     * Helper method for opening the add user pane
     * All actions to properly open the pane reside here
     * This includes focus requests, disable and cancel button setting, and disabling the table view
     */
    private void openAddUserPane(){
        usersList.setDisable(true);
        addUserButton.setVisible(false);
        deleteUserButton.setVisible(false);
        //defaults the submit button to a disabled state
        firstNameBox.validate();
        firstNameBox.resetValidation();
        addUserActionButton.setDefaultButton(true);
        addUserCancelButton.setCancelButton(true);
        addUserPane.setVisible(true);
        firstNameBox.requestFocus();
    }

    /**
     * Helper method for closing the add user pane
     * Includes clearing the input fields
     */
    private void closeAddUserPane(){
        addUserButton.setVisible(true);
        deleteUserButton.setVisible(true);
        addUserActionButton.setDefaultButton(false);
        addUserCancelButton.setCancelButton(false);
        addUserPane.setVisible(false);
        usersList.setDisable(false);
        usersList.requestFocus();
        firstNameBox.clear();
        firstNameBox.resetValidation();
        lastNameBox.clear();
        lastNameBox.resetValidation();
        usernameBox.clear();
        usernameBox.resetValidation();
        passwordBox.clear();
        passwordBox.resetValidation();
        passwordConfirmBox.clear();
        passwordConfirmBox.resetValidation();
        serviceSelect.valueProperty().set(null);
        permissionSelect.valueProperty().set(null);
    }

    /**
     * Helper method for opening the delete user pane
     */
    private void openDeleteUserPane(){
        usersList.setDisable(true);
        addUserButton.setVisible(false);
        deleteUserButton.setVisible(false);
        deleteUserActionButton.setDefaultButton(true);
        deleteUserCancelButton.setCancelButton(true);
        deletePane.setVisible(true);
    }

    /**
     * Helper method for closing the delete user pane
     */
    private void closeDeleteUserPane(){
        addUserButton.setVisible(true);
        deleteUserButton.setVisible(true);
        deleteUserActionButton.setDefaultButton(false);
        deleteUserCancelButton.setCancelButton(false);
        deletePane.setVisible(false);
        usersList.setDisable(false);
        usersList.requestFocus();
    }

    /**
     * Helper method for opening the additional information pane
     */
    private void openAdditionalInformationPane(){
        additionalInformationPane.setVisible(true);
    }

    /**
     * Helper method for closing the additional information pane
     */
    private void closeAdditionalInformationPane(){
        additionalInformationPane.setVisible(false);
    }
}