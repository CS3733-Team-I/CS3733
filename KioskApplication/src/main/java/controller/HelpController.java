package controller;

import com.jfoenix.controls.JFXTabPane;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import utility.ApplicationScreen;
import utility.ResourceManager;
import utility.node.NodeFloor;

import java.io.IOException;

public class HelpController extends ScreenController{

    @FXML JFXTabPane helpTabPlane;
    @FXML Tab mapHelpTab;
    @FXML Tab requestHelpTab;
    @FXML Tab settingsHelpTab;
    @FXML Tab mapBuilderHelp;
    @FXML Tab adminHelpTab;

    MapController mapController;

    //constructor for new help screen
    public HelpController(MainWindowController parent, MapController map) {
        super(parent, map);
    }

    @FXML
    protected void initialize() throws IOException {
        checkPermissions();
     /*   helpTabPlane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue == null) return;
            switch (newValue.getId().toString()) {
                case "Map Help":
                    break;
                case "tabMB":
                    break;
                case "tabRM":
                    break;
                case "tabRS":
                    break;
                case "tabSettings":
                    break;
            }
        });*/
    }

    @FXML
    public void onHelpTabSelection(){
        checkPermissions();
    }

    public void checkPermissions() {
        switch (LoginEntity.getInstance().getCurrentPermission()) {
            case ADMIN:
                helpTabPlane.getTabs().clear();
                helpTabPlane.getTabs().addAll(adminHelpTab,mapBuilderHelp,requestHelpTab,mapHelpTab,settingsHelpTab);
                break;
            case SUPER_USER:
                helpTabPlane.getTabs().clear();
                helpTabPlane.getTabs().addAll(adminHelpTab,mapBuilderHelp,requestHelpTab,mapHelpTab,settingsHelpTab);
                break;
            case EMPLOYEE:
                helpTabPlane.getTabs().clear();
                helpTabPlane.getTabs().addAll(requestHelpTab,mapHelpTab,settingsHelpTab);

            case NONEMPLOYEE:
                helpTabPlane.getTabs().clear();
                helpTabPlane.getTabs().addAll(mapHelpTab,settingsHelpTab);
                break;
        }
    }

    public javafx.scene.Node getContentView(){
        if (contentView == null) {
            contentView = loadView("/view/HelpScreen.fxml");
        }

        return contentView;
    }

    public void onMapLocationClicked(javafx.scene.input.MouseEvent e){}
    public void onMapNodeClicked(Node node){}
    public void onMapEdgeClicked(Edge edge){}
    public void onMapFloorChanged(NodeFloor floor){}

    public void onScreenChanged() {}
    public void resetScreen(){
        getMapController().setAnchor(0, 0, 0, 0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();
        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);
        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(true);
    }

}
