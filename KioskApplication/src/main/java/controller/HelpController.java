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
    ImageView requestImage,mapImage,settingImage,builderImage,adminImage;

    MapController mapController;
    int requestPicture, mapPicture, settingPicture,builderPicture,adminPicture;

    //constructor for new help screen
    public HelpController(MainWindowController parent, MapController map) {
        super(parent, map);
        requestPicture = 0;
        mapPicture = 0;
        settingPicture = 0;
        builderPicture = 0;
        adminPicture = 0;
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

     settingsHelpViews();
     requestHelpViews();
     mapHelpViews();
     builderViews();
     adminViews();
//     checkPermissions();

    }

    @FXML
    public void nextAdmin(){
        if(adminPicture<6){
            adminPicture++;
        }
        else{
            adminPicture = 0;
        }
        adminViews();
    }

    @FXML
    public void previousAdmin() {
        if (helpTabPlane.getTabs().contains(adminHelpTab)) {
            if (adminPicture > 0) {
                adminPicture--;
            } else {
                adminPicture = 6;
            }
            adminViews();
        }
    }


    private void adminViews() {
        if(helpTabPlane.getTabs().contains(mapBuilderHelp)) {
            switch (adminPicture) {
                case 0:
                    Image adminHelp0 = ResourceManager.getInstance().getImage("/images/helpImages/admin0.PNG");
                    adminImage.setImage(adminHelp0);
//                requestImage.setFitWidth(requestHelp.getWidth());
//                requestImage.setFitHeight(requestHelp.getHeight());
                    break;
                case 1:
                    Image adminHelp1 = ResourceManager.getInstance().getImage("/images/helpImages/admin1.PNG");
                    adminImage.setImage(adminHelp1);
                    break;
                case 2:
                    Image adminHelp2 = ResourceManager.getInstance().getImage("/images/helpImages/admin2.PNG");
                    adminImage.setImage(adminHelp2);
                    break;
                case 3:
                    Image adminHelp3 = ResourceManager.getInstance().getImage("/images/helpImages/admin3.PNG");
                    adminImage.setImage(adminHelp3);
                    break;
                case 4:
                    Image adminHelp4 = ResourceManager.getInstance().getImage("/images/helpImages/admin4.PNG");
                    adminImage.setImage(adminHelp4);
                    break;
                case 5:
                    Image adminHelp5 = ResourceManager.getInstance().getImage("/images/helpImages/admin5.PNG");
                    adminImage.setImage(adminHelp5);
                    break;
                case 6:
                    Image adminHelp6 = ResourceManager.getInstance().getImage("/images/helpImages/admin6.PNG");
                    adminImage.setImage(adminHelp6);
                    break;
            }
        }
    }

    @FXML
    public void nextBuilder(){
        if(builderPicture<7){
            builderPicture++;
        }
        else{
            builderPicture = 0;
        }
        builderViews();
    }

    @FXML
    public void previousBuilder(){
        if(builderPicture>0){
            builderPicture--;
        }else{
            builderPicture = 7;
        }
        builderViews();
    }


    private void builderViews() {
        if(helpTabPlane.getTabs().contains(mapBuilderHelp)) {
            switch (builderPicture) {
                case 0:
                    Image builderHelp0 = ResourceManager.getInstance().getImage("/images/helpImages/builder0.PNG");
                    builderImage.setImage(builderHelp0);
                    //                requestImage.setFitWidth(requestHelp.getWidth());
                    //                requestImage.setFitHeight(requestHelp.getHeight());
                    break;
                case 1:
                    Image builderHelp1 = ResourceManager.getInstance().getImage("/images/helpImages/builder1.PNG");
                    builderImage.setImage(builderHelp1);
                    break;
                case 2:
                    Image builderHelp2 = ResourceManager.getInstance().getImage("/images/helpImages/builder2.PNG");
                    builderImage.setImage(builderHelp2);
                    break;
                case 3:
                    Image builderHelp3 = ResourceManager.getInstance().getImage("/images/helpImages/builder3.PNG");
                    builderImage.setImage(builderHelp3);
                    break;
                case 4:
                    Image builderHelp4 = ResourceManager.getInstance().getImage("/images/helpImages/builder4.PNG");
                    builderImage.setImage(builderHelp4);
                    break;
                case 5:
                    Image builderHelp5 = ResourceManager.getInstance().getImage("/images/helpImages/builder5.PNG");
                    builderImage.setImage(builderHelp5);
                    break;
                case 6:
                    Image builderHelp6 = ResourceManager.getInstance().getImage("/images/helpImages/builder6.PNG");
                    builderImage.setImage(builderHelp6);
                    break;
                case 7:
                    Image builderHelp7 = ResourceManager.getInstance().getImage("/images/helpImages/builder7.PNG");
                    builderImage.setImage(builderHelp7);
                    break;
            }
        }
    }

    @FXML
    public void nextSetting(){
        if(settingPicture==1){
            settingPicture=0;
        }
        else{
            settingPicture = 1;
        }
        settingsHelpViews();
    }

    @FXML
    public void previousSetting(){
        if(settingPicture==0){
            settingPicture=1;
        }else{
            settingPicture = 0;
        }
        settingsHelpViews();
    }

    private void settingsHelpViews() {
        if(helpTabPlane.getTabs().contains(settingsHelpTab)) {
            switch (settingPicture) {
                case 0:
                    Image settingsHelp0 = ResourceManager.getInstance().getImage("/images/helpImages/settings0.PNG");
                    settingImage.setImage(settingsHelp0);
                    break;
                case 1:
                    Image settingsHelp1 = ResourceManager.getInstance().getImage("/images/helpImages/settings1.PNG");
                    settingImage.setImage(settingsHelp1);
                    break;
            }
        }
    }

    @FXML
    public void nextMap(){
        if(mapPicture<4){
            mapPicture++;
        }
        else{
            mapPicture = 0;
        }
        mapHelpViews();
    }

    @FXML
    public void previousMap(){
        if(mapPicture>0){
            mapPicture--;
        }else{
            mapPicture = 4;
        }
        mapHelpViews();
    }

    private void mapHelpViews() {
        if(helpTabPlane.getTabs().contains(mapBuilderHelp)) {
            switch (mapPicture) {
                case 0:
                    Image mapHelp0 = ResourceManager.getInstance().getImage("/images/helpImages/map0.png");
                    mapImage.setImage(mapHelp0);
                    //                requestImage.setFitWidth(requestHelp.getWidth());
                    //                requestImage.setFitHeight(requestHelp.getHeight());
                    break;
                case 1:
                    Image mapHelp1 = ResourceManager.getInstance().getImage("/images/helpImages/map1.png");
                    mapImage.setImage(mapHelp1);
                    break;
                case 2:
                    Image mapHelp2 = ResourceManager.getInstance().getImage("/images/helpImages/map2.png");
                    mapImage.setImage(mapHelp2);
                    break;
                case 3:
                    Image mapHelp3 = ResourceManager.getInstance().getImage("/images/helpImages/map3.png");
                    mapImage.setImage(mapHelp3);
                    break;
                case 4:
                    Image mapHelp4 = ResourceManager.getInstance().getImage("/images/helpImages/map4.png");
                    mapImage.setImage(mapHelp4);
                    break;
            }
        }
    }

    @FXML
    public void nextRequest(){
        if(requestPicture < 7){
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
            requestPicture = 7;
        }
        requestHelpViews();
    }

    private void requestHelpViews() {
        if(helpTabPlane.getTabs().contains(mapBuilderHelp)) {
            switch (requestPicture) {
                case 0:
                    Image requestHelp0 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter0.png");
                    requestImage.setImage(requestHelp0);
                    //                requestImage.setFitWidth(requestHelp.getWidth());
                    //                requestImage.setFitHeight(requestHelp.getHeight());
                    break;
                case 1:
                    Image requestHelp1 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter1.png");
                    requestImage.setImage(requestHelp1);
                    break;
                case 2:
                    Image requestHelp2 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter2.png");
                    requestImage.setImage(requestHelp2);
                    break;
                case 3:
                    Image requestHelp3 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter3.png");
                    requestImage.setImage(requestHelp3);
                    break;
                case 4:
                    Image requestHelp4 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter4.png");
                    requestImage.setImage(requestHelp4);
                    break;
                case 5:
                    Image requestHelp5 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter5.png");
                    requestImage.setImage(requestHelp5);
                    break;
                case 6:
                    Image requestHelp6 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter6.png");
                    requestImage.setImage(requestHelp6);
                    break;
                case 7:
                    Image requestHelp7 = ResourceManager.getInstance().getImage("/images/helpImages/requestSubmitter7.png");
                    requestImage.setImage(requestHelp7);
                    break;
            }
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
                break;
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
