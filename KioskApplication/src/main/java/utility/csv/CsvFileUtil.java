package utility.csv;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import database.objects.Edge;
import database.objects.Node;
import database.utility.DatabaseException;
import database.utility.DatabaseExceptionType;
import entity.MapEntity;
import javafx.scene.control.Alert;
import utility.node.NodeBuilding;
import utility.node.NodeFloor;
import utility.node.NodeType;
import utility.node.TeamAssigned;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CsvFileUtil {

    private CsvFileUtil() {

    }

    private static class SingletonHelper {
        private static CsvFileUtil _instance = new CsvFileUtil();
    }

    public static CsvFileUtil getInstance() { return SingletonHelper._instance; }

    public static final String NODE_CSV_HEAD = "nodeID,xcoord,ycoord,floor,building,nodeType,longName,shortName,teamAssigned";
    public static final String EDGE_CSV_HEAD = "edgeID,startNode,endNode";

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

    public void writeAllCsvs() {
        // Sort nodes into lists by team
        HashMap<TeamAssigned, List<Node>> nodesByTeam = new HashMap<>();
        for (Node node : MapEntity.getInstance().getAllNodes()) {
            TeamAssigned team = TeamAssigned.fromString(node.getTeamAssigned());
            if (!nodesByTeam.containsKey(team)) {
                nodesByTeam.put(team, new LinkedList<>());
            }

            List<Node> nodeList = nodesByTeam.get(team);
            nodeList.add(node);
        }

        // Save CSVs
        for (TeamAssigned team : nodesByTeam.keySet()) {
            writeNodesCSV(team, nodesByTeam.get(team));
        }
    }

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

    public void readNodesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        readNodesCSV(path, map);
    }

    public void writeNodesCSV(TeamAssigned team, List<Node> nodes) {
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

    public void readEdgesCSV(String path) {
        MapEntity map = MapEntity.getInstance();
        readEdgesCSV(path, map);
    }

    public void writeEdgesCSV(String path, boolean global) {
        // TODO implement this
        /*File csvFile = new File(path);
        FileWriter write = null;
        try {
            write = new FileWriter(csvFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter bWriter = new BufferedWriter(write);
        Connection con = null;
        try {
            bWriter.write(CSVFormat.EDGE_CSV_HEAD);
            bWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            con = DBUtil.getConnection();
            PreparedStatement pstmt = con.prepareStatement(SQLStrings.EDGE_SELECT_ALL);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                if(global) {
                    if(!((rs.getString("startNode").startsWith("W")) && (rs.getString("endNode").startsWith("W")))) {
                        continue;
                    }
                } else {
                    if((rs.getString("startNode").startsWith("W")) && (rs.getString("endNode").startsWith("W"))) {
                        continue;
                    }
                }

                bWriter.write(rs.getString("edgeID") + ",");
                bWriter.write(rs.getString("startNode") + ",");
                bWriter.write(rs.getString("endNode") + ",");

                bWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                bWriter.flush();
                write.close();
                bWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                DBUtil.closeConnection(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
}
