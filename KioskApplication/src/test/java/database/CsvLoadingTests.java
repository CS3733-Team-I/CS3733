package database;

import controller.MapController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Node;
import database.utility.DatabaseException;
import entity.MapEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utility.csv.CsvFileUtil;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoadingTests {

    DatabaseController dbController;

    public CsvLoadingTests() {
        dbController = DatabaseController.getInstance();
    }

    @Test
    public void loadCSVs() throws URISyntaxException{
        try {
            MapEntity.getInstance().removeAll();

            CsvFileUtil.getInstance().readNodesCSV("/csv/MapAnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapBnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapCnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapDnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapEnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapFnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapGnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapHnodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapInodes.csv");
            CsvFileUtil.getInstance().readNodesCSV("/csv/MapWnodes.csv");

            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapAedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapBedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapCedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapDedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapEedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapFedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapGedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapHedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapIedges.csv");
            CsvFileUtil.getInstance().readEdgesCSV("/csv/MapWedges.csv");

            Assert.assertTrue(true); // If we get here we're good!
        } catch (DatabaseException e) {
            e.printStackTrace();

            Assert.assertTrue(false); // :(
        }
    }

    @After
    public void cleanup() throws DatabaseException {
        MapEntity.getInstance().removeAll();
    }
}
