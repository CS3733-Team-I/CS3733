package controller;

import com.jfoenix.controls.*;
import database.objects.Edge;
import database.objects.Node;
import database.objects.Request;
import entity.MapEntity;
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
    }

    @FXML private JFXTabPane requestTypeTabs;
    @FXML private JFXButton btnSubmit;
    @FXML private JFXButton btnCancel;
    @FXML private JFXTextField intLocation;
    @FXML private JFXComboBox langMenu;
    @FXML private JFXTextField secLocationField;
    @FXML private JFXComboBox priorityMenu;
    @FXML private Tab interpreterTab;
    @FXML private Tab foodTab;
    @FXML private Tab securityTab;
    @FXML private Tab janitorTab;
    @FXML private JFXDatePicker datePicker;
    @FXML private JFXTimePicker timePicker;

    RequestType currentRequestType = RequestType.INTERPRETER;

    //Finds current admin that is logged in
    //currently a dummy email
    String adminEmail = "boss@hospital.com"; //TODO implement something new for parent.curr_admin_email

    @FXML
    public void initialize() {
        Image interpreterIcon = new Image(getClass().getResource("/images/icons/interpreterIcon.png").toString());
        ImageView interpreterIconView = new ImageView(interpreterIcon);
        interpreterIconView.setFitHeight(24);
        interpreterIconView.setFitWidth(24);
        interpreterTab.setGraphic(interpreterIconView);

        Image foodIcon = new Image(getClass().getResource("/images/icons/foodIcon.png").toString());
        ImageView foodIconView = new ImageView(foodIcon);
        foodIconView.setFitHeight(24);
        foodIconView.setFitWidth(24);
        foodTab.setGraphic(foodIconView);

        Image securityIcon = new Image(getClass().getResource("/images/icons/securityIcon.png").toString());
        ImageView securityIconView = new ImageView(securityIcon);
        securityIconView.setFitHeight(24);
        securityIconView.setFitWidth(24);
        securityTab.setGraphic(securityIconView);

        Image janitorIcon = new Image(getClass().getResource("/images/icons/janitor.png").toString());
        ImageView janitorIconView = new ImageView(janitorIcon);
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
                currentRequestType = RequestType.SERUITUY;
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
    public void addIntRequest() throws IOException {
        String location = intLocation.getText();
        Node nodeLocation = MapEntity.getInstance().getNode(location);
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

        System.out.println("location: " + nodeLocation.getLongName() + ". language: " + languageSelected + ". Admin Email: " + adminEmail);

        //node ID, employee, notes, language
        RequestEntity.getInstance().submitInterpreterRequest(nodeLocation.getNodeID(), adminEmail, notes, language);


        LinkedList<Request> allRequests = RequestEntity.getInstance().getAllRequests();
        System.out.println(RequestEntity.getInstance().getAllRequests());

//        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
    }

    @FXML
    public void intClear() {

    }

    @FXML
    public void addSecRequest()throws IOException {
        String location = secLocationField.getText();
        Node nodeLocation = MapEntity.getInstance().getNode(location);
        String notes = "";

        int priority = 0;
        priority = Integer.parseInt(priorityMenu.getValue().toString());


        System.out.println("location: " + nodeLocation.getLongName() + ". priority: " + priority + ". Admin Email: " + adminEmail);

        //node ID, employee, notes, priority
        RequestEntity.getInstance().submitSecurityRequest(nodeLocation.getNodeID(), adminEmail, notes, priority);

//        getParent().switchToScreen(ApplicationScreen.MAP_BUILDER);
    }

    @FXML
    public void clearSecPressed(){
        secLocationField.setText("");

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
        switch (currentRequestType){
            case INTERPRETER:
                intLocation.setText(n.getNodeID());
                break;
            case SERUITUY:
                secLocationField.setText(n.getNodeID());
                break;
            case FOOD:
                System.out.println("map clicked in Food tab");
                break;
            case JANITOR:
                System.out.println("map clicked in Janitor tab");
                break;
        }
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