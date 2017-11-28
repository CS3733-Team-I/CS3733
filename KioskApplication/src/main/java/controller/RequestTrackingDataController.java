package controller;

import database.objects.Edge;
import entity.RequestEntity;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import utility.Node.NodeFloor;
import utility.Request.LanguageFrequency;

import java.util.LinkedList;

public class RequestTrackingDataController {
    @FXML BarChart languageHistogramChart;
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
    XYChart.Series s = new XYChart.Series();
        for (LanguageFrequency lF: r.getLanguageFrequency()) {
            s.getData().add(new XYChart.Data(lF.getLanguage().toString(), lF.getFrequency()));
        }
    }

    public void closePanel(){
        parent.closeRequestTrackingTable();
    }
}
