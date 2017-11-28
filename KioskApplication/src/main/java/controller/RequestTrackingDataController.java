package controller;

import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import utility.Request.LanguageFrequency;

public class RequestTrackingDataController {
    @FXML BarChart<String, Integer> langFreqBC;
    RequestEntity r;
    MainWindowController parent;

    public RequestTrackingDataController(MainWindowController parent){
        this.parent=parent;
        r=RequestEntity.getInstance();
    }

    @FXML
    public void initialize(){
    }

    public void refreshTable(){
        System.out.println("refreshing table");
        XYChart.Series s = new XYChart.Series<String,Integer>();
        s.setName("Languages");
        for (LanguageFrequency lF: r.getLanguageFrequency()) {
            s.getData().add(new XYChart.Data(lF.getLanguage().toString(), lF.getFrequency()));
        }
        if(langFreqBC.getData().size()!=0) {
            langFreqBC.getData().clear();
        }
        langFreqBC.getData().add(s);
        System.out.println("done");
    }

    public void closePanel(){
        parent.closeRequestTrackingTable();
    }
}
