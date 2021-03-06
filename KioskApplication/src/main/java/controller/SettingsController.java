package controller;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTabPane;
import controller.map.MapController;
import database.objects.Edge;
import entity.LoginEntity;
import entity.MapEntity;
import entity.SystemSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import utility.csv.CsvFileUtil;
import utility.node.NodeFloor;

import java.io.IOException;

public class SettingsController extends ScreenController {

    @FXML private JFXTabPane settingTabPane;

    @FXML private Tab languageTab;
    @FXML private Tab aboutTab;
    @FXML private Tab pathfindingTab;
    @FXML private Tab userTab;
    @FXML private Tab generalTab;

    @FXML private Tab databaseTab;
    @FXML private Tab employeesTab;

    @FXML private Tab unitTab;

    @FXML private Tab logTab;
    @FXML private AnchorPane logPane;


    @FXML private RadioButton astarButton;
    @FXML private RadioButton dijkstraButton;
    @FXML private RadioButton bfsButton;
    @FXML private RadioButton dfsButton;
    @FXML private RadioButton beamButton;
    @FXML private RadioButton bestFirstButton;

    @FXML private RadioButton englishButton;
    @FXML private RadioButton franceSelected;

    @FXML private JFXRadioButton rbArabic;
    @FXML private JFXRadioButton rbElbonian;

    @FXML private JFXRadioButton rbMetric;
    @FXML private JFXRadioButton rbEnglish;

    @FXML private AnchorPane userPane;
    @FXML private AnchorPane employeesPane;

    @FXML private TextField beamWidth;
    @FXML private TextField timeoutLength;

    private FXMLLoader employeeLoader;

    ToggleGroup searchAlgToggleGroup = new ToggleGroup();

    //unit toggle group
    ToggleGroup unitToggleGroup = new ToggleGroup();
    ToggleGroup numberSystemToggleGroup = new ToggleGroup();

    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    public void initialize() throws IOException{
        SystemSettings systemSettings = SystemSettings.getInstance();
        //Load saved selection; select appropriate radio button.
        astarButton.setToggleGroup(searchAlgToggleGroup);
        astarButton.setUserData("A*");
        dijkstraButton.setToggleGroup(searchAlgToggleGroup);
        dijkstraButton.setUserData("Dijkstra");
        bfsButton.setToggleGroup(searchAlgToggleGroup);
        bfsButton.setUserData("BFS");
        dfsButton.setToggleGroup(searchAlgToggleGroup);
        dfsButton.setUserData("DFS");
        beamButton.setToggleGroup(searchAlgToggleGroup);
        beamButton.setUserData("Beam");
        bestFirstButton.setToggleGroup(searchAlgToggleGroup);
        bestFirstButton.setUserData("BestFS");
        //Load saved selection; select appropriate radio button.
        for(Toggle toggle: searchAlgToggleGroup.getToggles()) {
            if(toggle.getUserData().equals(systemSettings.getPrefs().get("searchAlgorithm", "A*")))
                searchAlgToggleGroup.selectToggle(toggle);
        }

        // Add Employee Settings Screen
        employeeLoader = new FXMLLoader(getClass().getResource("/view/EmployeeSettingsView.fxml"));
        employeeLoader.setRoot(employeesPane);
        employeeLoader.load();

        // Add Users Settings Screen
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/view/UserSettingsView.fxml"));
        loader2.setRoot(userPane);
        loader2.load();

        // Add Activity Logging Screen
        FXMLLoader logLoader = new FXMLLoader(getClass().getResource("/view/ActivityLogView.fxml"));
        logLoader.setRoot(logPane);
        logLoader.load();

        timeoutLength.setText(Integer.toString(getParent().getMaxcountdown()));

        checkPermissions();

        //unit and number system
        rbMetric.setToggleGroup(unitToggleGroup);
        rbEnglish.setToggleGroup(unitToggleGroup);

        rbArabic.setToggleGroup(numberSystemToggleGroup);
        rbElbonian.setToggleGroup(numberSystemToggleGroup);

        rbMetric.setSelected(true);
        rbArabic.setSelected(true);

        systemSettings.setIsMetric(true);
        systemSettings.setIsArabic(true);

        // Listen for TextField text changes
        beamWidth.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                // check if it is a text or decimal and don't accept it
                if (!beamWidth.getText().matches("\\d*")) {
                    beamWidth.setText(beamWidth.getText().replaceAll("[^\\d]", ""));
                    return;
                } // dont accept beam width over 9
                else if (beamWidth.getText().length()>=2){
                    beamWidth.setText(beamWidth.getText().replace(newValue,oldValue));
                    return;
                }
                else if (beamWidth.getText().equals(Integer.toString(0))){
                    // dont accept 0's
                    beamWidth.setText(beamWidth.getText().replaceAll("[^\\d]", ""));
                    return;
                }
                else if (beamWidth.getText().isEmpty()){
                    // set to default
                    SystemSettings systemSettings = SystemSettings.getInstance();
                    systemSettings.setBeamWidth("3");
                    return;
                }
                  else  {// else it is a number and accept
                        SystemSettings systemSettings = SystemSettings.getInstance();
                        systemSettings.setBeamWidth(beamWidth.getText());
                return;
                }

            }
        });
        // fixes bug where employees wouldn't show up in the table after initialization
        settingTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
                if(newTab == employeesTab){
                    if(employeeLoader.getController() instanceof EmployeeSettingsController){
                        EmployeeSettingsController eSC = ((EmployeeSettingsController) employeeLoader.getController());
                        eSC.refreshUsers();
                    }
                }
            }
        });
    }

    /**
     * Resets the timer in the MainWindowController
     */
    @FXML
    void resetTimer(){
        getParent().resetTimer();
    }

    public void checkPermissions() {
        switch (LoginEntity.getInstance().getCurrentPermission()) {
            case ADMIN:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab,  unitTab, pathfindingTab, userTab, databaseTab, generalTab, logTab);
                break;
            case SUPER_USER:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, unitTab, pathfindingTab, userTab, databaseTab, employeesTab, generalTab, logTab);
                break;
            case NONEMPLOYEE:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, unitTab);
                break;
        }
    }

    @FXML
    void onSearchAlgorithmSelected(){
        SystemSettings systemSettings = SystemSettings.getInstance();
        systemSettings.setAlgorithm(searchAlgToggleGroup.getSelectedToggle().getUserData().toString());
    }

    /**
     * select unit
     */
    @FXML
    void onUnitSelected() {
        SystemSettings systemSettings = SystemSettings.getInstance();
        if(unitToggleGroup.getSelectedToggle() == rbMetric) {
            systemSettings.setIsMetric(true);
        }
        else if(unitToggleGroup.getSelectedToggle() == rbEnglish) {
            systemSettings.setIsMetric(false);
        }
    }

    /**
     * select number system
     */
    @FXML
    void onNumberSystemSelected() {
        SystemSettings systemSettings = SystemSettings.getInstance();
        if(numberSystemToggleGroup.getSelectedToggle() == rbArabic) {
            systemSettings.setIsMetric(true);
        }
        else if(numberSystemToggleGroup.getSelectedToggle() ==rbElbonian) {
            systemSettings.setIsArabic(false);
        }
    }

    @FXML
    void readCSV() {
        try {
            MapEntity.getInstance().removeAll();

            CsvFileUtil.getInstance().readAllCsvs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getMapController().reloadDisplay();
    }

    @FXML
    void saveCSV() {
        CsvFileUtil.getInstance().writeAllCsvs();
    }

    /**
     * Save the settings in the general screen
     */
    @FXML
    void saveGeneral(){
        try{
            getParent().setTimeout(Integer.parseInt(timeoutLength.getText()));
        }catch(Exception e){
            System.out.println("Exception caught");
        }
        timeoutLength.setText(Integer.toString(getParent().getMaxcountdown()));
    }

    @Override
    public Node getContentView() {
        if (contentView == null) {
            contentView = loadView("/view/SettingsView.fxml");
        }

        checkPermissions();

        return contentView;
    }

    private void resetPressed() {
        System.out.println("Reset");
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
        getMapController().setAnchor(0, 0, 0, 0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();

        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);

        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(false);
    }
}
