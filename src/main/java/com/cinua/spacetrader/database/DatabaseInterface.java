package com.cinua.spacetrader.database;
import com.cinua.spacetrader.gamedata.Savegame;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Ship;
import com.cinua.spacetrader.gameplay.Port;
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

    public HashMap<Cargo, Integer> getCargoPriceMultipliersByPortName(String portName) throws SQLException{ //to be removed and replaced by getCargoPriceMultipliersByPortId
        HashMap<Cargo, Integer> cargoList = new HashMap<>();
        String command = "SELECT Items.id, Items.Name, Capacity, BasePrice, PriceMultiplier FROM pricemultiplierlookup JOIN Items On Items.id = pricemultiplierlookup.CargoId JOIN Ports ON Ports.id = PortId WHERE Ports.Name = '" + portName + "';";
        ResultSet cargoListResult = database.createStatement().executeQuery(command);
        while(cargoListResult.next()){
            cargoList.put(new Cargo(cargoListResult.getInt("id"), cargoListResult.getString("Name"), cargoListResult.getInt("Capacity"), cargoListResult.getInt("BasePrice")), cargoListResult.getInt("PriceMultiplier"));
        }
        cargoListResult.close();
        return cargoList;
    }

    public HashMap<Cargo, Integer> getCargoPriceMultipliersByPortId(int id) throws SQLException{
        HashMap<Cargo, Integer> cargoList = new HashMap<>();
        String command = String.format("SELECT Items.id, Items.Name, Capacity, BasePrice, PriceMultiplier FROM Items LEFT JOIN pricemultiplierlookup ON Items.id = CargoId WHERE PortId=%d;", id);
        ResultSet cargoListResult = database.createStatement().executeQuery(command);
        while(cargoListResult.next()){
            cargoList.put(new Cargo(cargoListResult.getInt("id"), cargoListResult.getString("Name"), cargoListResult.getInt("Capacity"), cargoListResult.getInt("BasePrice")), cargoListResult.getInt("PriceMultiplier"));
        }
        cargoListResult.close();
        return cargoList;
    }

    public HashMap<Cargo, Integer> getCargoStockByPortId(int id) throws SQLException{
        HashMap<Cargo, Integer> cargoList = new HashMap<>();
        String command = String.format("SELECT id, Items.Name, Capacity, BasePrice, Quantity FROM Items LEFT JOIN stocklookup ON id = ItemId WHERE PortId = %d;", id);
        ResultSet cargoListResult = database.createStatement().executeQuery(command);
        while(cargoListResult.next()){
            cargoList.put(new Cargo(cargoListResult.getInt("id"), cargoListResult.getString("Name"), cargoListResult.getInt("Capacity"), cargoListResult.getInt("BasePrice")), cargoListResult.getInt("Quantity"));
        }
        cargoListResult.close();
        return cargoList;
    }

    public Port[] getPorts() throws SQLException{
       ResultSet portsTable = database.createStatement().executeQuery("SELECT id, Name, x, y FROM Ports;");
        ArrayList<Port> ports = new ArrayList<>();
       while(portsTable.next()){
            ports.add(new Port(portsTable.getInt("id"), portsTable.getString("Name"), new Vector<>(Arrays.asList(portsTable.getInt("x"), portsTable.getInt("y"))), getCargos(), getCargoPriceMultipliersByPortId(portsTable.getInt("id")).values().stream().mapToInt(i -> i).toArray(), getCargoStockByPortId(portsTable.getInt("id")).values().stream().mapToInt(i -> i).toArray()));
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
        String command = "SELECT Ships.id, Players.Name, Capital, Capacity, Consumption, x, y FROM Players, Ships WHERE Ships.id = Players.id AND Players.id = " + id + ";";
        ResultSet playerTable = database.createStatement().executeQuery(command);
        playerTable.next();
        Player player = new Player(playerTable.getString("Name"), new Ship(playerTable.getInt("id"), playerTable.getInt("Capacity"), playerTable.getInt("Consumption"), new Vector<>(Arrays.asList(playerTable.getInt("x"), playerTable.getInt("y")))), playerTable.getInt("Capital"));
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

    public void setCargos(Cargo[] cargos) throws SQLException{ //Not to be used in production.
        database.setAutoCommit(false);
        database.createStatement().execute("DELETE FROM Items");
        for(Cargo Table : cargos){
            String command = "INSERT INTO Items(Name, Capacity, BasePrice) VALUES " + String.format("('%s',%d,%d);",Table.getName(), Table.getWeight(), Table.getBasePrice());
            database.createStatement().execute(command);
        }
        database.commit();
    }

    //Actual gameplay functions
    public void populatePorts(Savegame world, int itemsInWorld) throws SQLException{ //Soon to be singleplayer only.
        String command = String.format("SELECT PortId, CargoId, PriceMultiplier FROM pricemultiplierlookup WHERE PriceMultiplier > %d;", Port.prohibited);
        ResultSet allowedCombinations = database.createStatement().executeQuery(command);
        int allowedCombinationsCount = 0;
        ArrayList<Integer> portIds = new ArrayList<>();
        ArrayList<Integer> cargoIds = new ArrayList<>();
        while(allowedCombinations.next()){
            portIds.add(allowedCombinations.getInt(1));
            cargoIds.add(allowedCombinations.getInt(2));
            allowedCombinationsCount++;
        }
        database.setAutoCommit(false);
        Random random = new Random();
        ArrayList<Integer> usedPortIds = new ArrayList<>();
        ArrayList<Integer> usedCargoIds = new ArrayList<>();
        for(int currentItem = 0; currentItem < itemsInWorld; currentItem++){
            int result = random.nextInt(0,allowedCombinationsCount);
            int portId = portIds.get(result);
            int cargoId = cargoIds.get(result);
            int quantity = 0;
            for(int currentCombination = 0; currentCombination < usedCargoIds.size(); currentCombination++){
                if(usedPortIds.get(currentCombination) == portId && usedCargoIds.get(currentCombination) == cargoId){
                    quantity++;
                }
            }
            if(quantity == 0){
                command = String.format("INSERT INTO stocklookup(ItemId, PortId, Quantity) VALUES(%d, %d, %d);", portId, cargoId, 1);
            }else{
                command = String.format("UPDATE stocklookup SET Quantity=Quantity+1 WHERE PortId=%d AND ItemId=%d;", portId, cargoId);
            }
            database.createStatement().execute(command);
            usedPortIds.add(portId);
            usedCargoIds.add(cargoId);
        }
        database.commit();
    }

    public boolean buyItemInPort(Player buyer, Cargo item, Port seller) throws SQLException{ //Something is wrong here, Ousan.
        Ship ship = buyer.getShip();
        if(!(ship.getPosition() != seller.getPosition() || ship.getRemainingCapacity() < item.getWeight() || seller.getStockByItem(item) <= Port.itemNotInStock || buyer.getCapital() < seller.getPriceForItemInStock(item))){
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