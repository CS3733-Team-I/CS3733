package controller;

import com.jfoenix.controls.*;
import controller.map.MapController;
import database.connection.NotFoundException;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Request;
import entity.LoginEntity;
import entity.MapEntity;
import entity.RequestEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import utility.KioskPermission;
import utility.RequestListCell;
import utility.node.NodeFloor;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.io.IOException;
import java.util.LinkedList;

import static utility.request.RequestProgressStatus.DONE;
import static utility.request.RequestProgressStatus.IN_PROGRESS;
import static utility.request.RequestProgressStatus.TO_DO;

public class RequestManagerController extends ScreenController {


    LoginEntity l;
    RequestEntity r;
    RequestProgressStatus currentButton;

    @FXML private Label totalRequests,filterLabel;
    @FXML private JFXListView<Request> activeRequests;
    @FXML private TextField txtID;
    @FXML private JFXButton completeButton;
    @FXML private JFXPopup popup;
    //filter buttons
    @FXML private JFXCheckBox foodFilter,janitorFilter,securityFilter,
            interpreterFilter,maintenanceFilter,itFilter,transportationFilter;


    public RequestManagerController(MainWindowController parent, MapController map) {
        super(parent, map);
        r = RequestEntity.getInstance();
        l = LoginEntity.getInstance();
        r.readAllFromDatabase();
        currentButton = TO_DO;
    }

    /**
     * When an employee is logged in this method checks to see the employee Request Type
     * it takes that information and filters out the requests to show relevant requests
     */
    @FXML
    public void setup(){
        RequestType employeeType = l.getCurrentServiceAbility();
        if(l.getCurrentPermission().equals(KioskPermission.EMPLOYEE) && !employeeType.equals(RequestType.GENERAL)){
            foodFilter.setSelected(false);
            foodFilter.setVisible(false);
            janitorFilter.setSelected(false);
            janitorFilter.setVisible(false);
            securityFilter.setSelected(false);
            securityFilter.setVisible(false);
            interpreterFilter.setSelected(false);
            interpreterFilter.setVisible(false);
            maintenanceFilter.setSelected(false);
            maintenanceFilter.setVisible(false);
            itFilter.setSelected(false);
            itFilter.setVisible(false);
            transportationFilter.setSelected(false);
            transportationFilter.setVisible(false);
            filterLabel.setVisible(false);
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
        }else{
            foodFilter.setVisible(true);
            janitorFilter.setVisible(true);
            securityFilter.setVisible(true);
            interpreterFilter.setVisible(true);
            maintenanceFilter.setVisible(true);
            itFilter.setVisible(true);
            transportationFilter.setVisible(true);
            filterLabel.setVisible(true);
        }
    }

    /**
     * unopened request button. Displays all of the new requests
     */
    @FXML
    void newRequests(){
        buttonAction(RequestProgressStatus.TO_DO);
        currentButton = TO_DO;
    }

    /**
     * in Progress request button. Displays all of the current requests
     */
    @FXML
    void inProgressRequests(){
        buttonAction(RequestProgressStatus.IN_PROGRESS);
        currentButton = IN_PROGRESS;
    }

    /**
     * Completed request button. Displays all of the finished requests
     */
    @FXML
    void doneRequests(){
        buttonAction(RequestProgressStatus.DONE);
        currentButton = DONE;
    }

    /**
     * Generic method that updates list of requests
     * @param status RequestProgressStatus is passed through to determine which requests to display
     */
    @FXML
    void buttonAction(RequestProgressStatus status){
        setup();
        LinkedList<Request> allRequests = filterRequests();
        showRequests(status, allRequests);
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

        return allRequests;
    }

    /**
     * Creates a list of request IDs and displays them in the ListView activeRequests
     * @param status RequestProgressStatus so the method knows which requests it is displaying
     * @param allRequests the list from which the method will filter to display a list of requestIDs
     */
    private void showRequests(RequestProgressStatus status, LinkedList<Request> allRequests) {
        activeRequests.setItems(null);
        ObservableList<Request> requests = FXCollections.observableArrayList();
        requests.addAll(r.filterByStatus(allRequests,status));
        activeRequests.setItems(requests);
        activeRequests.setCellFactory(param -> new RequestListCell(this));
    }

    /**
     * Method when a list view cell is selected currently does nothing
     * Creates what goes into the popup when a listview cell is selected
     * @param requestID To determine which request to display the information of
     */
    public void initializePopup(String requestID){

        JFXButton more = new JFXButton("More");
        JFXButton statusUpdater = new JFXButton();
        JFXButton delete = new JFXButton("Delete");
        more.setOnMousePressed(e -> resetTimer());
        more.setOnMouseMoved(e -> resetTimer());
        delete.setOnMouseMoved(e -> resetTimer());
        delete.setOnMousePressed(e -> resetTimer());

        ObservableList<Integer> listOfEmployees = FXCollections.observableArrayList();
        JFXComboBox employees = new JFXComboBox(listOfEmployees);
        employees.setPromptText("Select Employee");

        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                r.deleteRequest(requestID);
                popup.hide();
                refreshRequests();
            }
        });

        VBox vbox = new VBox(more);

        if(!l.getCurrentPermission().equals(KioskPermission.EMPLOYEE)){ //Admin or super
            listOfEmployees.clear();
            listOfEmployees.addAll(l.getAllEmployeeType(r.getRequest(requestID)));
            switch (currentButton){
                case TO_DO:
                    statusUpdater = new JFXButton("Assign");
                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            r.markInProgress((Integer) employees.getValue(),requestID);
                            refreshRequests();
                            popup.hide();
                        }
                    });
                    vbox.getChildren().addAll(employees, statusUpdater);
                    break;
            }
        }else {
            switch (currentButton) {
                case TO_DO:
                    statusUpdater = new JFXButton("Assign Me");
                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            r.markInProgress(l.getCurrentLoginID(), requestID);
                            refreshRequests();
                            popup.hide();
                        }
                    });
                    vbox.getChildren().add(statusUpdater);
                    break;
                case IN_PROGRESS:
                    statusUpdater = new JFXButton("Completed");
                    statusUpdater.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            r.completeRequest(requestID);
                            refreshRequests();
                            popup.hide();
                        }
                    });
                    vbox.getChildren().add(statusUpdater);
                    break;
            }
        }
        more.setPrefWidth(200);
        delete.setPrefWidth(200);
        statusUpdater.setPrefWidth(200);
        employees.setPrefWidth(200);

        vbox.getChildren().add(delete);
        popup = new JFXPopup(vbox);

        more.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vbox.getChildren().clear();
                try {
                    vbox.getChildren().add(displayInformation(requestID));
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public VBox displayInformation(String requestID) throws NotFoundException {
        Request request = r.getRequest(requestID);
        String location = MapEntity.getInstance().getNode(request.getNodeID()).getLongName();
        String assigner = r.getAssigner(requestID).getUsername();
        Label employee = new Label("Requested By: " + assigner);
        Label typeOfRequest = new Label(r.checkRequestType(requestID).toString());
        Label locationOfRequest = new Label(location);
        Label requestNotes = new Label(request.getNote());
        Label extraField;
        RequestType RT = r.checkRequestType(requestID);
        switch (RT){
            case INTERPRETER:
                String language = r.getInterpreterRequest(requestID).getLanguage().toString();
                extraField = new Label("Language: "+language);
                break;
            case FOOD:
                String restaurantID = r.getFoodRequest(requestID).getDestinationID();
                String restaurant = MapEntity.getInstance().getNode(restaurantID).getLongName();
                extraField = new Label("Restaurant: " + restaurant);
                break;
            default: //security
                int priority = r.getSecurityRequest(requestID).getPriority();
                extraField = new Label("Priority: "+ priority);
                break;
        }
        JFXButton close = new JFXButton("close");
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                popup.hide();
            }
        });
        VBox vbox = new VBox(typeOfRequest,locationOfRequest,employee,extraField,requestNotes,close);
        return vbox;
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

        newRequests();

        return contentView;
    }

    /**
     * reads requests from a database
     * @throws IOException
     */
    @FXML
    public void refreshRequests() {
        switch (currentButton){
            case IN_PROGRESS:
                inProgressRequests();
                break;
            case TO_DO:
                newRequests();
                break;
            case DONE:
                doneRequests();
                break;
        }
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
