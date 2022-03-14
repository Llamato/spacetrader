package com.cinua.spacetrader.database;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Ship;
import com.cinua.spacetrader.gameplay.planet.Port;
import java.sql.*;
import java.util.*;

public class DatabaseInterface{
    private static final String singleplayerPrefix = "jdbc:sqlite:";
    private static final String multiplayerPrefix = "jdbc:mariadb://";
    private static final String multiplayerPostfix = ":3306/spacetrader";

    public static final boolean singleplayer = false;
    public static final boolean multiplayer = true;

    public static final int noPlayer = 0;

    private Connection database;

    private DatabaseInterface(Connection connection){
        database = connection;
    }

    public static DatabaseInterface connect(String path, boolean multiplayer) throws SQLException{
         return multiplayer ? new DatabaseInterface(DriverManager.getConnection(multiplayerPrefix + path + multiplayerPostfix, "root", "")) : new DatabaseInterface(DriverManager.getConnection(singleplayerPrefix + path));
    }

    public void disconnect() throws SQLException{
        database.close();
    }

    public Cargo[] getCargos() throws SQLException{
        ArrayList<Cargo> cargoList = new ArrayList<>();
         ResultSet items = database.createStatement().executeQuery("SELECT Name,Capacity,BasePrice FROM Items;");
         while(items.next()){
             cargoList.add(new Cargo(items.getString(1),items.getInt(2),items.getInt(3)));
      }
         items.close();
         return cargoList.toArray(new Cargo[0]);
    }


    public HashMap<Cargo, Integer> getCargoListByPortName(String portName) throws SQLException{
        HashMap<Cargo, Integer> cargoList = new HashMap<>();
        String command = "SELECT Items.Name,Items.Capacity, Items.BasePrice, PriceMultiplier FROM CargoLookup JOIN Items On Items.id = CargoLookup.CargoId JOIN Ports ON Ports.id = PortId WHERE Ports.Name = '" + portName + "';";
        ResultSet cargoListResult = database.createStatement().executeQuery(command);
        while(cargoListResult.next()){
            cargoList.put(new Cargo(cargoListResult.getString(1), cargoListResult.getInt(2), cargoListResult.getInt(3)), cargoListResult.getInt(4));
        }
        cargoListResult.close();
        return cargoList;
    }

    public Port[] getPorts() throws SQLException{
       ResultSet portsTable = database.createStatement().executeQuery("SELECT Name, x, y FROM Ports;");
        ArrayList<Port> ports = new ArrayList<>();
       while(portsTable.next()){
            ports.add(new Port(portsTable.getString(1), new Vector<>(Arrays.asList(portsTable.getInt(2), portsTable.getInt(3))), getCargoListByPortName(portsTable.getString(1))));
       }
        portsTable.close();
        return ports.toArray(new Port[0]);
    }

    public int authenticate(String name, String password) throws SQLException{
        String command = String.format("SELECT accounts.id FROM accounts, players WHERE accounts.id = players.id AND Name='%s' AND password='%s';", name, password);
        ResultSet account = database.createStatement().executeQuery(command);
        while(account.next()){
            return account.getInt(1);
        }
        return noPlayer;
    }

    public int register(String name, String password) throws SQLException{
        database.setAutoCommit(false);
        String command = "INSERT INTO accounts(password) VALUES('" + password + "');";
        PreparedStatement statement = database.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
        statement.execute();
        ResultSet keys = statement.getGeneratedKeys();
        int accountId = 0;
        while(keys.next()){
            accountId = keys.getInt(1);
        }
        try{
            command = String.format("INSERT INTO Players(id, Name) VALUES(%d,'%s');", accountId, name);
            database.createStatement().execute(command);
        }catch(SQLIntegrityConstraintViolationException e){
            accountId = noPlayer;
            return accountId;
        }
        command = String.format("INSERT INTO Ships(id, Capacity, Consumption) VALUES(%d,%d,%d);",accountId,30,1);
        database.createStatement().execute(command);
        database.commit();
        return accountId;
    }
    public Player getPlayerById(int id) throws SQLException{
        ResultSet playerTable = database.createStatement().executeQuery("SELECT Capital, Capacity, Consumption FROM Players, Ships WHERE Ships.id = Players.id AND Players.id = " + id + ";");
        Player player = new Player(new Ship(playerTable.getInt(2),playerTable.getInt(3)),playerTable.getInt(1));
        playerTable.close();
        return player;
    }

    public int setShip(Ship ship) throws SQLException{
        database.setAutoCommit(false);
        database.createStatement().execute("DELETE FROM Ships;");
        String command = "INSERT INTO Ships(Capacity, Consumption) VALUES(" + ship.getCapacity() + "," + ship.getConsumption() + ");";
        PreparedStatement statement = database.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
        statement.execute();
        database.commit();
        return statement.getGeneratedKeys().getInt(1);
    }

    public void setPlayer(Player player) throws SQLException{
        database.setAutoCommit(false);
        int newShipKey = setShip(player.getShip());
        database.createStatement().execute("DELETE FROM Players;");
        String command = "INSERT INTO Players(Capital, Ship) VALUES(" + player.getCapital() + "," + newShipKey + ");";
        database.createStatement().execute(command);
        database.commit();
    }

    public void setCargos(Cargo[] cargos) throws SQLException{ // Not to be used in production.
        database.setAutoCommit(false);
        database.createStatement().execute("DELETE FROM Items");
        for(Cargo Table : cargos){
            String command = "INSERT INTO Items(Name, Capacity, BasePrice) VALUES " + String.format("('%s',%d,%d);",Table.getName(), Table.getWeight(), Table.getBasePrice());
            database.createStatement().execute(command);
        }
        database.commit();
    }
}
