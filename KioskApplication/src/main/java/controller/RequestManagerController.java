package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Request;
import entity.LoginEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import utility.KioskPermission;
import utility.RequestListCell;
import utility.node.NodeFloor;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

import static utility.request.RequestProgressStatus.*;

public class RequestManagerController extends ScreenController {


    LoginEntity l;
    RequestEntity r;
    RequestProgressStatus currentButton;

    @FXML private Label totalRequests,filterLabel;
    @FXML private JFXListView<Request> newRequestList, activeRequests, doneRequestList;
    @FXML private TextField txtID;
    @FXML private JFXButton completeButton;
    @FXML private JFXPopup popup;
    //filter buttons
    @FXML private JFXCheckBox foodFilter,janitorFilter,securityFilter,
            interpreterFilter,maintenanceFilter,itFilter,transportationFilter;
    @FXML private Tab newTab, progressTab, doneTab;
    @FXML private JFXTabPane listTabPane;
    @FXML private AnchorPane sideBar,listAPane,reqManagerPane;


    public RequestManagerController(MainWindowController parent, MapController map) {
        super(parent, map);
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
        setup();
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
     * When an employee is logged in this method checks to see the employee Request Type
     * it takes that information and filters out the requests to show relevant requests
     */
    @FXML
    public void setup(){
        RequestType employeeType = l.getCurrentServiceAbility();
        if(l.getCurrentPermission().equals(KioskPermission.EMPLOYEE) && !employeeType.equals(RequestType.GENERAL)){
            sideBar.setVisible(false);
            foodFilter.setSelected(false);
            janitorFilter.setSelected(false);
            securityFilter.setSelected(false);
            interpreterFilter.setSelected(false);
            maintenanceFilter.setSelected(false);
            itFilter.setSelected(false);
            transportationFilter.setSelected(false);
            switch (employeeType){
                case FOOD:
                    foodFilter.setSelected(true);
                    break;
                case SECURITY:
                    securityFilter.setSelected(true);
                    break;
                case INTERPRETER:
                    interpreterFilter.setSelected(true);
                    break;
                case JANITOR:
                    janitorFilter.setSelected(true);
                    break;
            }
            reqManagerPane.setLeftAnchor(listAPane,0.0);
//            foodFilter.setVisible(false);
//            janitorFilter.setVisible(false);
//            securityFilter.setVisible(false);
//            interpreterFilter.setVisible(false);
//            itFilter.setVisible(false);
//            maintenanceFilter.setVisible(false);
//            transportationFilter.setVisible(false);
//            filterLabel.setVisible(false);
        }else{
            reqManagerPane.setLeftAnchor(listAPane, 150.0);
            sideBar.setVisible(true);
//            foodFilter.setVisible(true);
//            janitorFilter.setVisible(true);
//            securityFilter.setVisible(true);
//            interpreterFilter.setVisible(true);
//            maintenanceFilter.setVisible(true);
//            itFilter.setVisible(true);
//            transportationFilter.setVisible(true);
//            filterLabel.setVisible(true);
        }
    }

    /**
     * Generic method that updates list of requests
     * @param status RequestProgressStatus is passed through to determine which requests to display
     */
    @FXML
    void buttonAction(RequestProgressStatus status, JFXListView listView){
//        setup();
        LinkedList<Request> allRequests = filterRequests();
        showRequests(status, allRequests, listView);
    }

    /**
     * Checks the checkboxes to see what filters to add.
     * filters by request type, but must press a button on
     * the sidebar to see the results of this method
     * @return the list of requests to be displayed
     */
    @FXML
    LinkedList<Request> filterRequests() {
        r.readAllFromDatabase();
        LinkedList<Request> allRequests = new LinkedList<Request>();
        if (securityFilter.isSelected()) {
            for (Request sR : r.getAllSecurity()) {
                allRequests.add(sR);
            }
        }
        if (interpreterFilter.isSelected()) {
            for (InterpreterRequest iR : r.getAllinterpters()) {
                //If the person is an interpreter, then it adds only requests they can speak. Or shows all of them if ADMIN or above
                if(l.getCurrentInterpreterLanguages().contains(iR.getLanguage())||l.getCurrentPermission()==KioskPermission.ADMIN
                        ||l.getCurrentPermission()==KioskPermission.SUPER_USER) {
                    allRequests.add(iR);
                }
            }
        }
        if(foodFilter.isSelected()){
            for(Request fR : r.getAllFoodRequests()){
                allRequests.add(fR);
            }
        }
        LinkedList<Request> inprogressReq = new LinkedList<>();
        if(currentButton == IN_PROGRESS && l.getCurrentPermission().equals(KioskPermission.EMPLOYEE)){
            for(Request item: allRequests){
                if(item.getCompleterID() == (l.getCurrentLoginID())){
                    inprogressReq.add(item);
                }
            }
            allRequests = inprogressReq;
        }
        return allRequests;
    }

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
     * Method to display popup information when a list view cell is selected
     * @param event the mouse click on an ID triggers this method
     */
    @FXML
    public void displayInfo(MouseEvent event){
    }

    /**
     * opens the reports pop up window to display the graphs
     * @throws IOException
     */
    @FXML
    void showReports() throws IOException{
        getParent().openRequestTrackingTable();
    }

    /**
     * sets RequestManagerView as fxml file for this controller
     * @return displays the request Manager
     */
    @Override
    public javafx.scene.Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/RequestManagerView.fxml");
        }

        initialize();
//        newRequests();

        return contentView;
    }

    /**
     * reads requests from a database
     * @throws IOException
     */
    @FXML
    public void refreshRequests() {
        switch (currentButton){
            case TO_DO:
//                inProgressRequests();
                buttonAction(RequestProgressStatus.TO_DO, newRequestList);
                break;
            case IN_PROGRESS:
//                newRequests();
                buttonAction(RequestProgressStatus.IN_PROGRESS, activeRequests);
                break;
            case DONE:
//                doneRequests();
                buttonAction(RequestProgressStatus.DONE, doneRequestList);
                break;
        }
        setup();
    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    public void resetTimer(){
        getParent().resetTimer();
    }

    @Override
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e) { }

    @Override
    public void onMapNodeClicked(database.objects.Node node) { }

    @Override
    public void onMapEdgeClicked(Edge edge) { }

    @Override
    public void onMapFloorChanged(NodeFloor floor) { }

    @Override
    public void resetScreen() {
        getMapController().setAnchor(0,500,0,0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(false);
    }
}
