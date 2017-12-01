package controller;

import com.jfoenix.controls.JFXButton;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import utility.request.LanguageFrequency;

import java.util.HashMap;

public class RequestTrackingDataController {
    @FXML BarChart<String, Integer> langFreqBC;
    @FXML JFXButton cancelButton;
    @FXML PieChart pieChart;
    RequestEntity r;
    MainWindowController parent;

    public RequestTrackingDataController(MainWindowController parent){
        this.parent=parent;
        r=RequestEntity.getInstance();
    }

    @FXML
    public void initialize(){
        refreshTable();
    }

    public void refreshTable(){
        r.readAllFromDatabase();
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
        disableCancelButton(true);
        parent.closeRequestTrackingTable();
    }

    public void refreshPieChart(){
        pieChart.setLegendVisible(false);
        pieChart.setData(r.getRequestDistribution());
    }

    public void disableCancelButton(boolean disabled){
        cancelButton.setCancelButton(!disabled);
    }
}
