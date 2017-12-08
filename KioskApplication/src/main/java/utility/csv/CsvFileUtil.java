package utility.csv;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import database.utility.DatabaseExceptionType;
import entity.MapEntity;
import javafx.scene.control.Alert;
import org.springframework.util.StringUtils;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CsvFileUtil {

    private CsvFileUtil() { }

    private static class SingletonHelper {
        private static CsvFileUtil _instance = new CsvFileUtil();
    }

    public static CsvFileUtil getInstance() { return SingletonHelper._instance; }

    public static final String NODE_CSV_HEAD = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned";
    public static final String EDGE_CSV_HEAD = "edgeID,startNode,endNode";

    /**
     * Reads all CSVs from the jar package
     */
    public void readAllCsvs() {
        readNodesCSV("/csv/MapAnodes.csv");
        readNodesCSV("/csv/MapBnodes.csv");
        readNodesCSV("/csv/MapCnodes.csv");
        readNodesCSV("/csv/MapDnodes.csv");
        readNodesCSV("/csv/MapENodes.csv");
        readNodesCSV("/csv/MapFNodes.csv");
        readNodesCSV("/csv/MapGNodes.csv");
        readNodesCSV("/csv/MapHnodes.csv");
        readNodesCSV("/csv/MapInodes.csv");
        readNodesCSV("/csv/MapWnodes.csv");

        readEdgesCSV("/csv/MapAedges.csv");
        readEdgesCSV("/csv/MapBedges.csv");
        readEdgesCSV("/csv/MapCedges.csv");
        readEdgesCSV("/csv/MapDedges.csv");
        readEdgesCSV("/csv/MapEEdges.csv");
        readEdgesCSV("/csv/MapFEdges.csv");
        readEdgesCSV("/csv/MapGEdges.csv");
        readEdgesCSV("/csv/MapHedges.csv");
        readEdgesCSV("/csv/MapIedges.csv");
        readEdgesCSV("/csv/MapWedges.csv");
    }

    /**
     * Writes all CSVs to files in [jar path]/csv/[filename].csv
     */
    public void writeAllCsvs() {
        // Sort nodes by team
        HashMap<TeamAssigned, List<Node>> nodesByTeam = new HashMap<>();
        HashMap<String, Edge> allEdges = new HashMap<>();

        for (Node node : MapEntity.getInstance().getAllNodes()) {
            TeamAssigned team = TeamAssigned.fromString(node.getTeamAssigned());

            // Check if this is a W node (special case)
            if (node.getNodeID().charAt(0) == 'W') team = TeamAssigned.W;

            // Create nodes list if doesnt exist
            if (!nodesByTeam.containsKey(team)) {
                nodesByTeam.put(team, new LinkedList<>());
            }

            nodesByTeam.get(team).add(node);

            // Add connected edges to list
            for (Edge edge : MapEntity.getInstance().getEdges(node)) {
                allEdges.put(edge.getEdgeID(), edge);
            }
        }

        // Save Nodes to CSV
        for (TeamAssigned team : nodesByTeam.keySet()) {
            writeNodesCSV(team, nodesByTeam.get(team));
        }

        // Sort Edges
        HashMap<TeamAssigned, List<Edge>> edgesByTeam = new HashMap<>();
        for (Edge edge : allEdges.values()) {
            // Detect team assignment
            TeamAssigned node1team =
                    TeamAssigned.fromString(edge.getNode1ID().substring(0, 1));
            TeamAssigned node2team =
                    TeamAssigned.fromString(edge.getNode2ID().substring(0, 1));

            // Take the lowest team assignment in order to sort edges from (n -> w) or (w -> n)
            // into non-w edges files
            TeamAssigned team;
            if (node1team.ordinal() < node2team.ordinal())
                team = node1team;
            else
                team = node2team;

            // Special case for W edges
            if (StringUtils.countOccurrencesOf(edge.getEdgeID(), "ELEV") == 2) {
                team = TeamAssigned.W;
            }

            if (!edgesByTeam.containsKey(team)) {
                edgesByTeam.put(team, new LinkedList<>());
            }

            edgesByTeam.get(team).add(edge);
        }

        // Save Edges to CSV
        for (TeamAssigned team : edgesByTeam.keySet()) {
            writeEdgesCSV(team, edgesByTeam.get(team));
        }
    }

    /**
     * Reads a CSV file from a given path into the MapEntity instance
     * @param path
     */
    public void readNodesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        readNodesCSV(path, map);
    }

    /**
     * Reads a CSV file from a given path and given MapEntity
     * @param path
     * @param map
     */
    public void readNodesCSV(String path, MapEntity map) {
        CsvReader reader = null;
        InputStream input = CsvFileUtil.class.getResourceAsStream(path);
        InputStreamReader isr = new InputStreamReader(input);

        try {
            reader = new CsvReader(isr);
            reader.readHeaders();

            String fields[] = NODE_CSV_HEAD.split(",");
            String headers[] = reader.getHeaders();
            int index = 0;
            for (String header : headers) {
                if (!header.equals(fields[index])) {
                    throw new CsvException("Input file was not of type EDGES_CSV.");
                }

                index++;
            }

            while (reader.readRecord()) {
                String id = reader.get(fields[0]).trim();
                Integer xcoord = Integer.parseInt(reader.get(fields[1]).trim());
                Integer ycoord = Integer.parseInt(reader.get(fields[2]).trim());
                String longName = reader.get(fields[6]).trim();
                String shortName = reader.get(fields[7]).trim();
                String teamAssigned = reader.get(fields[8]).trim();

                String floor = reader.get(fields[3]).trim();
                String building = reader.get(fields[4]).trim();
                String type = reader.get(fields[5]).trim();
                NodeFloor nodeFloor = NodeFloor.LOWERLEVEL_2;
                NodeBuilding nodeBuilding = NodeBuilding.FRANCIS45;
                NodeType nodeType = NodeType.HALL;

                switch (floor) {
                    case "L2":
                        nodeFloor = NodeFloor.LOWERLEVEL_2;
                        break;
                    case "L1":
                        nodeFloor = NodeFloor.LOWERLEVEL_1;
                        break;
                    case "G":
                        nodeFloor = NodeFloor.GROUND;
                        break;
                    case "1":
                        nodeFloor = NodeFloor.FIRST;
                        break;
                    case "2":
                        nodeFloor = NodeFloor.SECOND;
                        break;
                    case "3":
                        nodeFloor = NodeFloor.THIRD;
                        break;
                }

                switch (building) {
                    case "45 Francis":
                        nodeBuilding = NodeBuilding.FRANCIS45;
                        break;
                    case "15 Francis":
                        nodeBuilding = NodeBuilding.FRANCIS15;
                        break;
                    case "Tower":
                        nodeBuilding = NodeBuilding.TOWER;
                        break;
                    case "Sharpiro":
                        nodeBuilding = NodeBuilding.SHAPIRO;
                        break;
                    case "BTM":
                        nodeBuilding = NodeBuilding.BTM;
                        break;
                }

                switch (type) {
                    case "HALL":
                        nodeType = NodeType.HALL;
                        break;
                    case "ELEV":
                        nodeType = NodeType.ELEV;
                        break;
                    case "REST":
                        nodeType = NodeType.REST;
                        break;
                    case "STAI":
                        nodeType = NodeType.STAI;
                        break;
                    case "DEPT":
                        nodeType = NodeType.DEPT;
                        break;
                    case "LABS":
                        nodeType = NodeType.LABS;
                        break;
                    case "INFO":
                        nodeType = NodeType.INFO;
                        break;
                    case "CONF":
                        nodeType = NodeType.CONF;
                        break;
                    case "EXIT":
                        nodeType = NodeType.EXIT;
                        break;
                    case "RETL":
                        nodeType = NodeType.RETL;
                        break;
                    case "SERV":
                        nodeType = NodeType.SERV;
                        break;
                }

                Node node = new Node(id, xcoord, ycoord, nodeFloor, nodeBuilding, nodeType, longName, shortName, teamAssigned);

                try {
                    map.addNode(node);
                } catch (DatabaseException e) {
                    if (e.getType() == DatabaseExceptionType.ID_ALREADY_EXISTS || e.getType() == DatabaseExceptionType.DUPLICATE_ENTRY) {
                        try {
                            map.editNode(node);
                        } catch (DatabaseException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error editing node to DB");
                            alert.setHeaderText("Error occurred while editing node read from CSV.");
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error adding node to DB");
                        alert.setHeaderText("Error occurred while adding node to database from CSV.");
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
                try {
                    isr.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes nodes to a CSV for a given team
     * @param team the team
     * @param nodes the nodes for said team
     */
    public void writeNodesCSV(TeamAssigned team, Collection<Node> nodes) {
        try {
            String basePath = CsvFileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String path = "";
            switch (team) {
                case A:
                    path = "MapAnodes.csv";
                    break;
                case B:
                    path = "MapBnodes.csv";
                    break;
                case C:
                    path = "MapCnodes.csv";
                    break;
                case D:
                    path = "MapDnodes.csv";
                    break;
                case E:
                    path = "MapENodes.csv";
                    break;
                case F:
                    path = "MapFNodes.csv";
                    break;
                case G:
                    path = "MapGNodes.csv";
                    break;
                case H:
                    path = "MapHnodes.csv";
                    break;
                case I:
                    path = "MapInodes.csv";
                    break;
                case W:
                    path = "MapWnodes.csv";
                    break;
            }

            // Create dir if it doesnt exist
            System.out.println("Writing CSV " + basePath + "csv/" + path);
            final File file = new File(basePath + "csv/" + path);
            final File parent_directory = file.getParentFile();
            if (!parent_directory.exists()) {
                parent_directory.mkdirs();
            }

            CsvWriter writer = new CsvWriter(new FileWriter(file, false), ',');

            // Write header
            for (String header : NODE_CSV_HEAD.split(",")) {
                writer.write(header);
            }
            writer.endRecord();

            // Write nodes
            for (Node node : nodes) {
                writer.write(node.getNodeID());
                writer.write(Integer.toString(node.getXcoord()));
                writer.write(Integer.toString(node.getYcoord()));
                writer.write(node.getFloor().toCSVString());
                writer.write(node.getBuilding().toString());
                writer.write(node.getNodeType().toString());
                writer.write(node.getLongName());
                writer.write(node.getShortName());
                writer.write(node.getTeamAssigned());
                writer.endRecord();
            }

            writer.close();

        } catch (URISyntaxException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error writing nodes to SCV");
            alert.setHeaderText("Error occurred while getting path to JAR.");
            alert.setContentText(e.toString());

            e.printStackTrace();

            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error adding nodes to CSV");
            alert.setHeaderText("Error creating file at path.");
            alert.setContentText(e.toString());

            e.printStackTrace();

            alert.showAndWait();
        }
    }

    /**
     * Reads edges from a path into the MapEntity instance
     * @param path
     */
    public void readEdgesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        readEdgesCSV(path, map);
    }

    /**
     * Reads edges from a given path into a MapEntity
     * @param path
     * @param map
     */
    public void readEdgesCSV(String path, MapEntity map) {
        CsvReader reader = null;
        InputStream input = CsvFileUtil.class.getResourceAsStream(path);
        InputStreamReader isr = new InputStreamReader(input);

        try {
            reader = new CsvReader(isr);
            reader.readHeaders();

            String fields[] = EDGE_CSV_HEAD.split(",");
            String headers[] = reader.getHeaders();
            int index = 0;
            for (String header : headers) {
                if (!header.equals(fields[index])) {
                    throw new CsvException("Input file was not of type EDGES_CSV.");
                }

                index++;
            }

            while (reader.readRecord()) {
                String id = reader.get(fields[0]).trim();
                String firstNodeID = reader.get(fields[1]).trim();
                String secondNodeID = reader.get(fields[2]).trim();

                Edge edge = new Edge(id, firstNodeID, secondNodeID);

                try {
                    map.addEdge(edge);
                } catch (DatabaseException e) {
                    if (e.getType() == DatabaseExceptionType.ID_ALREADY_EXISTS || e.getType() == DatabaseExceptionType.DUPLICATE_ENTRY) {
                        try {
                            map.editEdge(edge);
                        } catch (DatabaseException ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error editing edge to DB");
                            alert.setHeaderText("Error occurred while editing edge read from CSV.");
                            alert.setContentText(e.toString());
                            alert.showAndWait();
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error adding edge to DB");
                        alert.setHeaderText("Error occurred while adding edge to database from CSV.");
                        alert.setContentText(e.toString());
                        alert.showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
                try {
                    isr.close();
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Writes edges to a CSV for a given team
     * @param team the team
     * @param edges the edges for the team
     */
    public void writeEdgesCSV(TeamAssigned team, Collection<Edge> edges) {
        try {
            String basePath = CsvFileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String path = "";
            switch (team) {
                case A:
                    path = "MapAedges.csv";
                    break;
                case B:
                    path = "MapBedges.csv";
                    break;
                case C:
                    path = "MapCedges.csv";
                    break;
                case D:
                    path = "MapDedges.csv";
                    break;
                case E:
                    path = "MapEEdges.csv";
                    break;
                case F:
                    path = "MapFEdges.csv";
                    break;
                case G:
                    path = "MapGEdges.csv";
                    break;
                case H:
                    path = "MapHedges.csv";
                    break;
                case I:
                    path = "MapIedges.csv";
                    break;
                case W:
                    path = "MapWedges.csv";
                    break;
            }

            // Create dir if it doesnt exist
            System.out.println("Writing CSV " + basePath + "csv/" + path);
            final File file = new File(basePath + "csv/" + path);
            final File parent_directory = file.getParentFile();
            if (!parent_directory.exists()) {
                parent_directory.mkdirs();
            }

            CsvWriter writer = new CsvWriter(new FileWriter(file, false), ',');

            // Write header
            for (String header : EDGE_CSV_HEAD.split(",")) {
                writer.write(header);
            }
            writer.endRecord();

            // Write nodes
            for (Edge edge : edges) {
                writer.write(edge.getEdgeID());
                writer.write(edge.getNode1ID());
                writer.write(edge.getNode2ID());
                writer.endRecord();
            }

            writer.close();

        } catch (URISyntaxException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error writing edge to SCV");
            alert.setHeaderText("Error occurred while getting path to JAR.");
            alert.setContentText(e.toString());

            e.printStackTrace();

            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error writing edge to CSV");
            alert.setHeaderText("Error creating file at path.");
            alert.setContentText(e.toString());

            e.printStackTrace();

            alert.showAndWait();
        }
    }
}
