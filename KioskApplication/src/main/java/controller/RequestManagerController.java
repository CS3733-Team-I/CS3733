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
import entity.SearchEntity.ISearchEntity;
import entity.SearchEntity.SearchRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utility.KioskPermission;
import utility.RequestListCell;
import utility.ResourceManager;
import utility.node.NodeFloor;
import utility.request.RequestProgressStatus;
import utility.request.RequestType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import static utility.request.RequestProgressStatus.*;

public class RequestManagerController extends ScreenController {


    LoginEntity l;
    RequestEntity r;
    RequestProgressStatus currentButton;

    @FXML private BorderPane borderPane;
    @FXML private Label totalRequests,filterLabel;
    @FXML private JFXListView<Request> newRequestList, activeRequests, doneRequestList;
    @FXML private TextField txtID;
    @FXML private JFXButton completeButton;
    @FXML private HBox clearPathBox;
    //filter buttons
    @FXML private JFXCheckBox foodFilter,janitorFilter,securityFilter,
            interpreterFilter,maintenanceFilter,itFilter,transportationFilter;
    @FXML private Tab newTab, progressTab, doneTab;
    @FXML private JFXTabPane listTabPane;
    @FXML private AnchorPane sideBar,listAPane,reqManagerPane;

    //Anchor Pane to contain the search bar
    @FXML private AnchorPane searchAnchor;
    private SearchController searchController;
    private javafx.scene.Node searchView;


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
    public void initialize() throws IOException {
        listTabPane.tabMinWidthProperty().bind(listAPane.widthProperty().divide(listTabPane.getTabs().size()).subtract(10));


        Image newRequestIcon = ResourceManager.getInstance().getImage("/images/icons/newReq.png");
        ImageView newReqIconView = new ImageView(newRequestIcon);
        newReqIconView.setFitHeight(48);
        newReqIconView.setFitWidth(48);
        newTab.setGraphic(newReqIconView);

        Image progRequestIcon = ResourceManager.getInstance().getImage("/images/icons/progressReq.png");
        ImageView progRequestIconView = new ImageView(progRequestIcon);
        progRequestIconView.setFitHeight(48);
        progRequestIconView.setFitWidth(48);
        progressTab.setGraphic(progRequestIconView);

        Image doneRequestIcon = ResourceManager.getInstance().getImage("/images/icons/doneReq.png");
        ImageView doneRequestIconView = new ImageView(doneRequestIcon);
        doneRequestIconView.setFitHeight(48);
        doneRequestIconView.setFitWidth(48);
        doneTab.setGraphic(doneRequestIconView);

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

        //search related
        FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/view/searchView.fxml"));
        ArrayList<ISearchEntity> searchRequest = new ArrayList<>();
        for(Request targetRequest : r.getAllRequests()) {
            searchRequest.add(new SearchRequest(targetRequest));
        }
        searchController = new SearchController(this, searchRequest);
        searchLoader.setController(searchController);
        searchView = searchLoader.load();
        this.searchAnchor.getChildren().add(searchView);
        searchController.setSearchFieldPromptText("Search Request");
        searchController.resizeSearchbarWidth(350.0);

    }

    /**
     * When an employee is logged in this method checks to see the employee Request Type
     * it takes that information and filters out the requests to show relevant requests
     */
    @FXML
    public void setup(){
        RequestType employeeType = l.getCurrentServiceAbility();
        if(l.getCurrentPermission().equals(KioskPermission.EMPLOYEE) && !employeeType.equals(RequestType.GENERAL)){
//            sideBar.setVisible(false);
            borderPane.setLeft(null);

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
        }else{
            borderPane.setLeft(sideBar);
            reqManagerPane.setLeftAnchor(listAPane, 150.0);
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
        if(janitorFilter.isSelected()){
            for(Request jR: r.getAllJanitorRequests()){
                allRequests.add(jR);
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

        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                buttonAction(RequestProgressStatus.TO_DO, newRequestList);
                break;
            case IN_PROGRESS:
                buttonAction(RequestProgressStatus.IN_PROGRESS, activeRequests);
                break;
            case DONE:
                buttonAction(RequestProgressStatus.DONE, doneRequestList);
                break;
        }
        //update search
        ArrayList<ISearchEntity> searchRequest = new ArrayList<>();
        for(Request targetRequest : r.getAllRequests()) {
            searchRequest.add(new SearchRequest(targetRequest));
        }
        searchController.reset(searchRequest);
        setup();
    }

    @FXML
    public void clearPathsButton(){
        clearPathBox.getChildren().clear();
        JFXButton clearPath = new JFXButton("Clear Path");
        clearPath.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                getMapController().clearMap();
                clearPathBox.getChildren().clear();
            }
        });
        clearPathBox.getChildren().add(clearPath);
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
