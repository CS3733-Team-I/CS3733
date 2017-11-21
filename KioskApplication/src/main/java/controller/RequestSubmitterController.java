package controller;

import com.jfoenix.controls.*;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import database.objects.Request;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ApplicationScreen;
import utility.Language;
import utility.NodeFloor;
import utility.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class RequestSubmitterController extends ScreenController implements Initializable{

    public RequestSubmitterController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML private JFXTabPane requestTypeTabs;
    @FXML private JFXButton btnSubmit;
    @FXML private JFXButton btnCancel;
    @FXML private JFXTextField txtLocation;
    @FXML private JFXComboBox langMenu;
    @FXML private Tab InterpreterTab;
    @FXML private Tab FoodTab;
    @FXML private Tab SecurityTab;
    @FXML private Tab JanitorTab;
    @FXML private JFXDatePicker timeDP;

    RequestType currentRequestType = RequestType.INTERPRETER;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image InterpreterIcn = new Image(getClass().getResource("/images/interpreterIcon.png").toString());
        ImageView InterpreterIcnView = new ImageView(InterpreterIcn);
        InterpreterIcnView.setFitHeight(24);
        InterpreterIcnView.setFitWidth(24);
        InterpreterTab.setGraphic(InterpreterIcnView);

        Image FoodIcn = new Image(getClass().getResource("/images/foodIcon.png").toString());
        ImageView FoodIcnIcnView = new ImageView(FoodIcn);
        FoodIcnIcnView.setFitHeight(24);
        FoodIcnIcnView.setFitWidth(24);
        FoodTab.setGraphic(FoodIcnIcnView);

        Image SecurityIcn = new Image(getClass().getResource("/images/securityIcon.png").toString());
        ImageView SecurityIcnView = new ImageView(SecurityIcn);
        SecurityIcnView.setFitHeight(24);
        SecurityIcnView.setFitWidth(24);
        SecurityTab.setGraphic(SecurityIcnView);

        Image JanitorIcn = new Image(getClass().getResource("/images/janitor.png").toString());
        ImageView JanitorIcnView = new ImageView(JanitorIcn);
        JanitorIcnView.setFitHeight(24);
        JanitorIcnView.setFitWidth(24);
        JanitorTab.setGraphic(JanitorIcnView);

        requestTypeTabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab oldValue, Tab newValue) {
                if (newValue == InterpreterTab) {
                    currentRequestType = RequestType.INTERPRETER;
                }
                else if (newValue == FoodTab) {
                    System.out.println("FOOD");
                    currentRequestType = RequestType.FOOD;
                }
                else if (newValue == SecurityTab) {
                    currentRequestType = RequestType.SERUITUY;
                }
                else if (newValue == JanitorTab) {
                    currentRequestType = RequestType.JANITOR;
                }
            }

        });
    }

    @FXML
    void onInterpreterPressed() throws IOException {
        getParent().switchToScreen(ApplicationScreen.REQUEST_INTERFACE);
    }

    @FXML
    public void addRequest() throws IOException {
        String location = txtLocation.getText();
        Node nodeLocation = DatabaseController.getNode(location);
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


        System.out.println(DatabaseController.getAllInterpreterRequests());

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
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
        getMapController().setAnchor(0,200,0,0);
    }
    }
