package controller;

import database.DatabaseController;
import database.objects.Edge;
import database.objects.Node;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import utility.ApplicationScreen;
import utility.NodeFloor;

import java.io.IOException;

public class InterpreterRequestController extends ScreenController{

    public InterpreterRequestController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML private Button btnSubmit;
    @FXML private Button btnCancel;
    @FXML private TextField txtLocation;
    @FXML private ChoiceBox langMenu;


    @FXML
    void onInterpreterPressed() throws IOException {
        System.out.println("Interpreter Request Pressed\n");

        getParent().switchToScreen(ApplicationScreen.ADMIN_INTERPRETER);
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

        String language = "None";
        if(langMenu.getValue().toString().equals("Spanish")){
            language = "Spanish";
        }else if(langMenu.getValue().toString().equals("Chinese")) {
            language = "Chinese";
        }

        //Finds current admin that is logged in
        String adminEmail = ""; //TODO implement something new for parent.curr_admin_email

        if(DatabaseController.getAllInterpreterRequests().isEmpty()){
            interpID = 0;
        }else{
            interpID = DatabaseController.getAllInterpreterRequests().get(DatabaseController.getAllInterpreterRequests().size()-1).getInterpreterID() + 1;
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
            contentView = loadView("/view/InterpreterRequestView.fxml");
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
