package com.cinua.spacetrader.database;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Ship;
import com.cinua.spacetrader.gameplay.planet.Market;
import com.cinua.spacetrader.gameplay.planet.Port;
import java.sql.*;
import java.util.*;

public class DatabaseInterface{
    private static final String singleplayerPrefix = "jdbc:sqlite:";
    private static final String multiplayerPrefix = "jdbc:mariadb://";
    private static final String multiplayerPostfix = ":3306/spacetrader";

    public static final boolean singleplayer = false;
    public static final boolean multiplayer = true;

    public static int singleplayerAccountId = 1;

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
         ResultSet items = database.createStatement().executeQuery("SELECT id, Name,Capacity,BasePrice FROM Items;");
         while(items.next()){
             cargoList.add(new Cargo(items.getInt("id"), items.getString("Name"), items.getInt("Capacity"), items.getInt("BasePrice")));
      }
         items.close();
         return cargoList.toArray(new Cargo[0]);
    }

    public HashMap<Cargo, Integer> getCargoListByPortName(String portName) throws SQLException{
        HashMap<Cargo, Integer> cargoList = new HashMap<>();
        String command = "SELECT Items.id, Name, Capacity, BasePrice, PriceMultiplier FROM pricemultiplierlookup JOIN Items On Items.id = CargoLookup.CargoId JOIN Ports ON Ports.id = PortId WHERE Ports.Name = '" + portName + "';";
        ResultSet cargoListResult = database.createStatement().executeQuery(command);
        while(cargoListResult.next()){
            cargoList.put(new Cargo(cargoListResult.getInt("id"), cargoListResult.getString("Name"), cargoListResult.getInt("Capacity"), cargoListResult.getInt("BasePrice")), cargoListResult.getInt("PriceMultiplier"));
        }
        cargoListResult.close();
        return cargoList;
    }

    public Port[] getPorts() throws SQLException{
       ResultSet portsTable = database.createStatement().executeQuery("SELECT id, Name, x, y FROM Ports;");
        ArrayList<Port> ports = new ArrayList<>();
       while(portsTable.next()){
            ports.add(new Port(portsTable.getInt("id"), portsTable.getString("Name"), new Vector<>(Arrays.asList(portsTable.getInt("x"), portsTable.getInt("y"))), new Market(null, getCargoListByPortName(portsTable.getString("Name"))))); //Temporary!
       }
        portsTable.close();
        return ports.toArray(new Port[0]);
    }

    public int authenticate(String name, String password) throws SQLException{
        String command = String.format("SELECT accounts.id FROM accounts, players WHERE accounts.id = players.id AND Name='%s' AND password='%s';", name, password);
        ResultSet account = database.createStatement().executeQuery(command);
        return account.next() ? account.getInt(1) : noPlayer;
    }

    public int register(String name, String password) throws SQLException{
        database.setAutoCommit(false);
        int accountId = noPlayer;
        String command;
        if(password != null && !password.equals("")){
            command = "INSERT INTO accounts(password) VALUES('" + password + "');";
            PreparedStatement statement = database.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            statement.execute();
            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            accountId = keys.getInt(1);
        }else{
            accountId = singleplayerAccountId;
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
        String command = "SELECT Ships.id, Capital, Capacity, Consumption, x, y FROM Players, Ships WHERE Ships.id = Players.id AND Players.id = " + id + ";";
        ResultSet playerTable = database.createStatement().executeQuery(command);
        Player player = null;
        playerTable.next();
        player = new Player(new Ship(playerTable.getInt("id"), playerTable.getInt("Capacity"), playerTable.getInt("Consumption"), new Vector<>(Arrays.asList(playerTable.getInt("x"), playerTable.getInt("y")))), playerTable.getInt("Capital"));
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

    public void setPlayer(Player player) throws SQLException{ // Not to be used in production;
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

    //Actual gameplay functions
    public boolean buyItemInPort(Player buyer, Cargo item, Port seller) throws SQLException{
        Ship ship = buyer.getShip();
        Market market = seller.getMarket();
        if(!(ship.getPosition() != seller.getPosition() || ship.getRemainingCapacity() < item.getWeight() || market.getStockByItem(item) <= Market.itemNotInStock || buyer.getCapital() < market.getPriceForItemInStock(item))){
            String command = "INSERT INTO TRUNK(Name,stock,capacity) VALUES " + String.format("('%s',&d,&d);");
            database.setAutoCommit(false);
            database.createStatement().execute(command);
        }
        return false;
    }

    public boolean travelToPort(Ship vessel, Port destination) throws SQLException{
        Vector<Integer> vesselPosition = vessel.getPosition();
        Vector<Integer> destinationPosition = destination.getPosition();
        int distance = Port.getDistanceBetween(vesselPosition, destinationPosition);
        if(vessel.getRange() < distance){
           return false;
        }
        vessel.travelTo(destination);
        String command = String.format("UPDATE trunk SET Quantity=%d WHERE ItemId=%d AND ShipId=%d;", vessel.getFuelRemaining(), Cargo.fuelId, vessel.getId());
        database.setAutoCommit(false);
        database.createStatement().executeUpdate(command);
        command = String.format("UPDATE ships SET x=%d,y=%d WHERE id=%d;", destinationPosition.get(0), destinationPosition.get(1), vessel.getId());
        database.createStatement().executeUpdate(command);
        database.commit();
        return true;
    }
}