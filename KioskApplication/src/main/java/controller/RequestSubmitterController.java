package controller;

import com.jfoenix.controls.*;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import database.objects.Request;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ApplicationScreen;
import utility.Request.Language;
import utility.Node.NodeFloor;
import utility.Request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

public class RequestSubmitterController extends ScreenController {

    public RequestSubmitterController(MainWindowController parent, MapController map) {
        super(parent, map);
        dbController = DatabaseController.getInstance();
    }

    @FXML private JFXTabPane requestTypeTabs;

    /*language related*/
    @FXML private Tab interpreterTab;
    @FXML private JFXComboBox langMenu;
    /*food related*/
    @FXML private Tab foodTab;
    @FXML private JFXComboBox foodMenu;
    /*security related*/
    @FXML private Tab securityTab;
    /*janitor related*/
    @FXML private Tab janitorTab;


    @FXML private JFXButton btnSubmit;
    @FXML private JFXButton btnCancel;
    @FXML private JFXTextField txtLocation;
    @FXML private JFXDatePicker datePicker;
    @FXML private JFXTimePicker timePicker;

    RequestType currentRequestType = RequestType.INTERPRETER;

    DatabaseController dbController;

    @FXML
    public void initialize() {
        Image interpreterIcon = new Image(getClass().getResource("/images/icons/interpreterIcon.png").toString());
        ImageView interpreterIconView = new ImageView(interpreterIcon);
        interpreterIconView.setRotate(90);
        interpreterIconView.setFitHeight(24);
        interpreterIconView.setFitWidth(24);
        interpreterTab.setGraphic(interpreterIconView);

        Image foodIcon = new Image(getClass().getResource("/images/icons/foodIcon.png").toString());
        ImageView foodIconView = new ImageView(foodIcon);
        foodIconView.setRotate(90);
        foodIconView.setFitHeight(24);
        foodIconView.setFitWidth(24);
        foodTab.setGraphic(foodIconView);

        Image securityIcon = new Image(getClass().getResource("/images/icons/securityIcon.png").toString());
        ImageView securityIconView = new ImageView(securityIcon);
        securityIconView.setRotate(90);
        securityIconView.setFitHeight(24);
        securityIconView.setFitWidth(24);
        securityTab.setGraphic(securityIconView);

        Image janitorIcon = new Image(getClass().getResource("/images/icons/janitor.png").toString());
        ImageView janitorIconView = new ImageView(janitorIcon);
        janitorIconView.setRotate(90);
        janitorIconView.setFitHeight(24);
        janitorIconView.setFitWidth(24);
        janitorTab.setGraphic(janitorIconView);

        requestTypeTabs.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == interpreterTab) {
                currentRequestType = RequestType.INTERPRETER;
            } else if (newValue == foodTab) {
                System.out.println("FOOD");
                currentRequestType = RequestType.FOOD;
            } else if (newValue == securityTab) {
                currentRequestType = RequestType.SECURITY;
            } else if (newValue == janitorTab) {
                currentRequestType = RequestType.JANITOR;
            }
        });
    }

    @FXML
    void onInterpreterPressed() throws IOException {
        getParent().switchToScreen(ApplicationScreen.REQUEST_SUBMITTER);
    }

    @FXML
    public void addRequest() throws IOException {
        String location = txtLocation.getText();
        Node nodeLocation = dbController.getNode(location);
        String notes = "";

        Language language = Language.NONE;
        String languageSelected = langMenu.getValue().toString();
        switch (languageSelected){
            case "Spanish":
                language = Language.SPANISH;
                break;
            case "Mandarin":
                language = Language.CHINESE;
                break;
            case "German":
                language = Language.GERMAN;
                break;
        }


        //Finds current admin that is logged in
        //currently a dummy email
        String adminEmail = "boss@hospital.com"; //TODO implement something new for parent.curr_admin_email

        LinkedList<Request> allRequests = RequestEntity.getInstance().getAllRequests();


        System.out.println("location: " + nodeLocation.getLongName() + ". language: " + languageSelected + ". Admin Email: " + adminEmail);

        //node ID, employee, notes, language
        RequestEntity.getInstance().submitInterpreterRequest(nodeLocation.getNodeID(), adminEmail, notes, language);

        //Adds the Interpreter request to the database
//        DatabaseController.addRequest(interpID,nodeLocation.getNodeID(), adminEmail);
//        DatabaseController.addIntepreterRequest(language, interpID, interpID);
        System.out.println(dbController.getAllInterpreterRequests());

        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
    }

    @FXML
    public void onCancelPressed() {

    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestSubmitterView.fxml");
        }

        return contentView;
    }

    @Override
    public void onMapLocationClicked(Point2D location) { }

    @Override
    public void onMapNodeClicked(Node n) {
        txtLocation.setText(n.getNodeID());
    }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,235,0,0);
    }
}