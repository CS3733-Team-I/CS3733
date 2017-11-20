package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RequestSubmitterController extends ScreenController implements Initializable{

    public RequestSubmitterController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML private JFXButton btnSubmit;
    @FXML private JFXButton btnCancel;
    @FXML private JFXTextField txtLocation;
    @FXML private JFXComboBox langMenu;
    @FXML private Tab InterpreterTab;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Image InterpreterIcn = new Image(getClass().getResource("/images/invalid_input.png").toString());
        InterpreterTab.setGraphic(new ImageView(InterpreterIcn));
    }



    @FXML
    void onInterpreterPressed() throws IOException {
        System.out.println("Interpreter Request Pressed\n");

        getParent().switchToScreen(ApplicationScreen.REQUEST_INTERFACE);
    }


    @FXML
    void onCancelPressed() throws IOException{
        System.out.println("Cancel Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_MENU);
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
        // TODO implement this
    }
}