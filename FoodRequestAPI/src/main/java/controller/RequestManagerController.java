package controller;

import com.jfoenix.controls.*;
import database.objects.InterpreterRequest;
import database.objects.Request;
import entity.LoginEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utility.KioskPermission;
import utility.RequestListCell;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

import static utility.request.RequestProgressStatus.*;

public class RequestManagerController {


    LoginEntity l;
    RequestEntity r;
    RequestProgressStatus currentButton;

    @FXML private Label totalRequests,filterLabel;
    @FXML private JFXListView<Request> newRequestList, activeRequests, doneRequestList;
    @FXML private TextField txtID;
    @FXML private JFXButton completeButton;
    @FXML private Tab newTab, progressTab, doneTab;
    @FXML private JFXTabPane listTabPane;

    javafx.scene.Node contentView;

    public RequestManagerController() {
        r = RequestEntity.getInstance();
        l = LoginEntity.getInstance();
        r.readAllFromDatabase();
        currentButton = TO_DO;
    }

    /**
     * Sets up the list views in each tab
     */
    @FXML
    public void initialize() {
        buttonAction(RequestProgressStatus.TO_DO, newRequestList);
        buttonAction(RequestProgressStatus.IN_PROGRESS, activeRequests);
        buttonAction(RequestProgressStatus.DONE, doneRequestList);

        listTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == newTab) {
                currentButton = TO_DO;
                refreshRequests();
            } else if (newValue == progressTab) {
                currentButton = IN_PROGRESS;
                refreshRequests();
            } else if (newValue == doneTab) {
                currentButton = DONE;
                refreshRequests();
            }
        });
    }

    /**
     * Generic method that updates list of requests
     * @param status RequestProgressStatus is passed through to determine which requests to display
     */
    @FXML
    void buttonAction(RequestProgressStatus status, JFXListView listView){
        LinkedList<Request> allRequests = r.getAllRequests();
        showRequests(status, allRequests, listView);
    }

    /**
     * Checks the checkboxes to see what filters to add.
     * filters by request type, but must press a button on
     * the sidebar to see the results of this method
     * @return the list of requests to be displayed
     */
//    @FXML
//    LinkedList<Request> filterRequests() {
//        r.readAllFromDatabase();
//        LinkedList<Request> allRequests = r.getAllRequests();
//        LinkedList<Request> inprogressReq = new LinkedList<>();
//        if(currentButton == IN_PROGRESS && l.getCurrentPermission().equals(KioskPermission.EMPLOYEE)){
//            for(Request item: allRequests){
//                if(item.getCompleterID() == (l.getCurrentLoginID())){
//                    inprogressReq.add(item);
//                }
//            }
//        }
//        return inprogressReq;
//    }

    /**
     * Creates a list of request IDs and displays them in the ListView activeRequests
     * @param status RequestProgressStatus so the method knows which requests it is displaying
     * @param allRequests the list from which the method will filter to display a list of requestIDs
     */
    private void showRequests(RequestProgressStatus status, LinkedList<Request> allRequests, JFXListView listView) {
        listView.setItems(null);
        ObservableList<Request> requests = FXCollections.observableArrayList();
        requests.addAll(r.filterByStatus(allRequests,status));
        listView.setItems(requests);
        listView.setCellFactory(param -> new RequestListCell(this));
    }

    /**
     * sets RequestManagerView as fxml file for this controller
     * @return displays the request Manager
     */
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerView.fxml");
        }

        return contentView;
    }

    protected javafx.scene.Node loadView(String path) {
        javafx.scene.Node view;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            loader.setController(this);
            view = loader.load();
        } catch (IOException e) {
            System.out.println("Load " + this + "view failed," + " initialize with empty view");
            e.printStackTrace();

            view = new AnchorPane(); // Initialize contentView as an empty view
        }

        return view;
    }


    /**
     * reads requests from a database
     * @throws IOException
     */
    @FXML
    public void refreshRequests() {
        switch (currentButton){
            case TO_DO:
                buttonAction(RequestProgressStatus.TO_DO, newRequestList);
                break;
            case IN_PROGRESS:
                buttonAction(RequestProgressStatus.IN_PROGRESS, activeRequests);
                break;
            case DONE:
                buttonAction(RequestProgressStatus.DONE, doneRequestList);
                break;
        }
    }
}
