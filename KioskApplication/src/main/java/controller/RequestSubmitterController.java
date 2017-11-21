package controller;

import com.jfoenix.controls.*;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ApplicationScreen;
import utility.NodeFloor;
import utility.RequestType;

import java.io.IOException;
import java.net.URL;
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
    public void addRequest() throws IOException {
        int interpID;
        String location = txtLocation.getText();

        //sets nodeLocation to default my location
        Node nodeLocation = DatabaseController.getNode(location);

        //TODO this doesn't return a language... returns null
        String language = "None";
        if(langMenu.getValue().toString().equals("Spanish")){
            language = "Spanish";
        }else if(langMenu.getValue().toString().equals("Mandarin")) {
            language = "Mandarin";
        }else if(langMenu.getValue().toString().equals("German")){
            language= "German";
        }


        //Finds current admin that is logged in
        //currently a dummy email
        String adminEmail = "boss@hospital.com"; //TODO implement something new for parent.curr_admin_email


        if(DatabaseController.getAllRequests().isEmpty()){
            interpID = 0;
        }else{
            interpID = DatabaseController.getAllRequests().get(DatabaseController.getAllRequests().size()-1).getRequestID() + 1;
        }


        System.out.println("location: " + nodeLocation.getLongName() + ". language: " + language + ". Admin Email: " + adminEmail + ". Interpreter ID: " + interpID);

        //Adds the Interpreter request to the database
        DatabaseController.addRequest(interpID,nodeLocation.getNodeID(), adminEmail);
        DatabaseController.addIntepreterRequest(language, interpID, interpID);
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
