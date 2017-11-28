package controller;

import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import utility.Request.LanguageFrequency;

import java.util.LinkedList;

public class RequestTrackingDataController {
    @FXML
    BarChart LanguageHistogram;
    RequestEntity r;

    public RequestTrackingDataController(ScreenController parent, LinkedList<LanguageFrequency> lHData){
        r=RequestEntity.getInstance();
        XYChart.Series s = new XYChart.Series();
        for (LanguageFrequency lF: lHData
             ) {
            s.getData().add(new XYChart.Data(lF.getLanguage().toString(), lF.getFrequency()));
        }
    }
}
