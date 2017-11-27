package database;

import controller.MapController;
import database.objects.Edge;
import database.objects.InterpreterRequest;
import database.objects.Node;
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
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapAnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapBnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapCnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapDnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapEnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapFnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapGnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapHnodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapInodes.csv").toURI().getPath());
        CsvFileUtil.readNodesCSV(getClass().getResource("/csv/MapWnodes.csv").toURI().getPath());

        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapAedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapBedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapCedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapDedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapEedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapFedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapGedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapHedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapIedges.csv").toURI().getPath());
        CsvFileUtil.readEdgesCSV(getClass().getResource("/csv/MapWedges.csv").toURI().getPath());

        Assert.assertTrue(true); // If we get here we're good!
    }

    @Before
    @After
    public void removeAllFromDB() {
        List<Node> nodes = MapEntity.getInstance().getAllNodes();
        for (Node node : nodes) {
            MapEntity.getInstance().removeNode(node.getNodeID());

            ArrayList<Edge> edges = MapEntity.getInstance().getEdges(node);
            for (Edge edge : edges) MapEntity.getInstance().removeEdge(edge.getEdgeID());
        }
    }
}
