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

    @FXML protected JFXTabPane helpTabPlane;
    @FXML Tab mapHelpTab;
    @FXML Tab requestHelpTab;
    @FXML Tab settingsHelpTab;
    @FXML Tab mapBuilderHelp;
    @FXML Tab adminHelpTab;

    MainWindowController parent;
    LoginEntity loginEntity;
    MapController mapController;

    //constructor for new help screen
    public HelpController(MainWindowController parent, MapController map) {
        super(parent, map);
        loginEntity = LoginEntity.getInstance();
    }
    @FXML
    protected void initialize() throws IOException {

        helpTabPlane.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
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
        });
    }

    @FXML
    public void onHelpTabSelection(){
        //helpTabPlane.getTabs();

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
    public void resetScreen(){}

}
