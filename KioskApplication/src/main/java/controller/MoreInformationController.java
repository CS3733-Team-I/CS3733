package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import utility.node.NodeFloor;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

public class MoreInformationController extends ScreenController {

    @FXML private JFXButton button1, button2, button3, button4, button5;
    @FXML private JFXTextArea displayNotes;

    String requestID;
    RequestEntity r;
    LoginEntity l;

    public MoreInformationController(MainWindowController parent, MapController mapController, String requestID) {
        super(parent, mapController);
        r = RequestEntity.getInstance();
        l = LoginEntity.getInstance();
        this.requestID = requestID;
    }

    @FXML
    public void initialize(){



        Request request = r.getRequest(requestID);
        button1.setText(requestID);
//        Label id = new Label(requestID);
        RequestType RT = r.checkRequestType(requestID);
        switch (RT){
            case INTERPRETER:
                String language = r.getInterpreterRequest(requestID).getLanguage().toString();
                button2.setText("Language: "+language);
                break;
            default: //security
                int priority = r.getSecurityRequest(requestID).getPriority();
                button2.setText("Priority: "+ priority);
                break;
        }
        String location = MapEntity.getInstance().getNode(request.getNodeID()).getLongName();
        button3.setText(location);
        if(request.getStatus().equals(RequestProgressStatus.TO_DO)){
            button4.setText("Assigned by: "+request.getAssigner());
            button5.setText("Sent in at: "+request.getSubmittedTime().toString());
        }else{
            button4.setText("Completed by: "+request.getCompleter());
            button5.setText("Completed at"+request.getCompletedTime().toString());
        }
        displayNotes.setText(request.getNote());
    }

    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerMoreInformationView.fxml");
        }
        return contentView;
    }

    @Override
    public void onMapLocationClicked(MouseEvent e, Point2D location) {

    }

    @Override
    public void onMapNodeClicked(database.objects.Node node) {

    }

    @Override
    public void onMapEdgeClicked(Edge edge) {

    }

    @Override
    public void onMapFloorChanged(NodeFloor floor) {

    }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,500,0,0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}
