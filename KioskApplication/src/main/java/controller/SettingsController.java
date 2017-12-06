package controller;

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
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import utility.csv.CsvFileUtil;
import utility.node.NodeFloor;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class SettingsController extends ScreenController {

    @FXML private JFXTabPane settingTabPane;

    @FXML private Tab aboutTab;
    @FXML private Tab displayTab;
    @FXML private Tab pathfindingTab;
    @FXML private Tab userTab;

    @FXML private Tab databaseTab;
    @FXML private Tab employeesTab;


    @FXML private RadioButton astarButton;
    @FXML private RadioButton dijkstraButton;
    @FXML private RadioButton bfsButton;
    @FXML private RadioButton dfsButton;
    @FXML private RadioButton beamButton;
    @FXML private RadioButton bestFirstButton;

    @FXML private RadioButton englishButton;
    @FXML private RadioButton franceSelected;

    @FXML private AnchorPane userPane;
    @FXML private AnchorPane employeesPane;

    @FXML private TextField beamWidth;

    ToggleGroup searchAlgToggleGroup = new ToggleGroup();
    ToggleGroup languageSelectToggleGroup = new ToggleGroup();
    public SettingsController(MainWindowController parent, MapController mapController) {
        super(parent, mapController);
    }

    public void initialize() throws IOException{
        SystemSettings systemSettings = SystemSettings.getInstance();
        englishButton.setToggleGroup(languageSelectToggleGroup);
        englishButton.setUserData("English");
        franceSelected.setToggleGroup(languageSelectToggleGroup);
        franceSelected.setUserData("France");
        //Load saved selection; select appropriate radio button.
        for(Toggle toggle: languageSelectToggleGroup.getToggles()) {
            if(toggle.getUserData().equals(systemSettings.getPrefs().get("Internationalization", "English")))
                languageSelectToggleGroup.selectToggle(toggle);
        }
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeSettingsView.fxml"));
        loader.setRoot(employeesPane);
        loader.load();

        // Add Users Settings Screen
        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/view/UserSettingsView.fxml"));
        loader2.setRoot(userPane);
        loader2.load();

        checkPermissions();

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
    }

    public void checkPermissions() {
        switch (LoginEntity.getInstance().getCurrentPermission()) {
            case ADMIN:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, displayTab, pathfindingTab, userTab, databaseTab);
                break;
            case SUPER_USER:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab, displayTab, pathfindingTab, userTab, databaseTab, employeesTab);
                break;
            case NONEMPLOYEE:
                settingTabPane.getTabs().clear();
                settingTabPane.getTabs().addAll(aboutTab,displayTab);
                break;
        }
    }

    @FXML
    void onSearchAlgorithmSelected(){
        SystemSettings systemSettings = SystemSettings.getInstance();
        systemSettings.setAlgorithm(searchAlgToggleGroup.getSelectedToggle().getUserData().toString());
    }

    /**
     * select language from settings
     */
    @FXML
    void onLanguageSelected(){
        SystemSettings systemSettings = SystemSettings.getInstance();
        systemSettings.setResourceBundle(languageSelectToggleGroup.getSelectedToggle().getUserData().toString());
    }

    @FXML
    void readCSV() {
        try {
            MapEntity.getInstance().removeAll();

            CsvFileUtil.getInstance().readAllCSVs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getMapController().reloadDisplay();
    }

    @FXML
    void saveCSV() {

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
    public void onMapLocationClicked(javafx.scene.input.MouseEvent e, Point2D location) { }

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
