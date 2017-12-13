package controller;

import com.jfoenix.controls.JFXTabPane;
import controller.map.MapController;
import database.objects.Edge;
import database.objects.Node;
import entity.LoginEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    @FXML AnchorPane requestPane;
    @FXML
    ImageView requestImage;

    MapController mapController;
    int requestPicture;

    //constructor for new help screen
    public HelpController(MainWindowController parent, MapController map) {
        super(parent, map);
        int requestPicture = 0;
    }

    @FXML
    protected void initialize() throws IOException {
//        checkPermissions();

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

     requestHelpViews();

    }

    @FXML
    public void nextRequest(){
        if(requestPicture < 5){
            requestPicture++;
        }else{
            requestPicture = 0;
        }
        requestHelpViews();
    }
    @FXML
    public void previousRequest(){
        if(requestPicture>0){
            requestPicture--;
        }else{
            requestPicture = 5;
        }
        requestHelpViews();
    }

    private void requestHelpViews() {
        switch (requestPicture){
            case 0:
                Image requestHelp0 = ResourceManager.getInstance().getImage("/images/requestSubmitter0.png");
                requestImage.setImage(requestHelp0);
//                requestImage.setFitWidth(requestHelp.getWidth());
//                requestImage.setFitHeight(requestHelp.getHeight());
                break;
            case 1:
                Image requestHelp1 = ResourceManager.getInstance().getImage("/images/requestSubmitter1.png");
                requestImage.setImage(requestHelp1);
                break;
            case 2:
                Image requestHelp2 = ResourceManager.getInstance().getImage("/images/requestSubmitter2.png");
                requestImage.setImage(requestHelp2);
                break;
            case 3:
                Image requestHelp3 = ResourceManager.getInstance().getImage("/images/requestSubmitter3.png");
                requestImage.setImage(requestHelp3);
                break;
            case 4:
                Image requestHelp4 = ResourceManager.getInstance().getImage("/images/requestSubmitter4.png");
                requestImage.setImage(requestHelp4);
                break;
            case 5:
                Image requestHelp5 = ResourceManager.getInstance().getImage("/images/requestSubmitter5.png");
                requestImage.setImage(requestHelp5);
                break;
        }
    }

    @FXML
    public void onHelpTabSelection(){
//        checkPermissions();
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
        getMapController().setAnchor(0, 1000, 0, 0);
        getMapController().setPath(null);
        getMapController().reloadDisplay();
        // Set default nodes/edges visibility
        getMapController().setNodesVisible(true);
        getMapController().setEdgesVisible(true);
        // Set if the options box is visible
        getMapController().setOptionsBoxVisible(true);
    }

}
