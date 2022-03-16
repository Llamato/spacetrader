package com.cinua.spacetrader.view;
import com.cinua.spacetrader.gamedata.GameData;
import com.cinua.spacetrader.database.DatabaseInterface;
import com.cinua.spacetrader.gamedata.Savegame;
import com.cinua.spacetrader.gamedata.SingleplayerGame;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Ship;
import com.cinua.spacetrader.gameplay.planet.Market;
import com.glabs.tables.SimpleTable;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.planet.Port;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Tests{

    //Test data integrity
    static SimpleTable getPortTable(Port[] ports) {
        SimpleTable table = new SimpleTable(3 + ports.length, new String[]{"Ware", "Preis multiplier", "Basispreis"});
        Arrays.stream(ports).map(Port::getName).forEach(table::append);
        Cargo[] cargos = Arrays.stream(ports).map(Port::getMarket).map(Market::getItemsInStock).flatMap(Arrays::stream).distinct().toArray(Cargo[]::new);
        for(Cargo cargo : cargos){
            table.append(cargo.getName());
            table.append(cargo.getWeight());
            table.append(cargo.getBasePrice());
            for(Port port : ports){
                int priceMultiplier = port.getMarket().getPriceForItemInStock(cargo);
                table.append(priceMultiplier == Port.prohibited ? "Verboten" : priceMultiplier);
            }
        }
        return table;
    }

    static void printTable(SimpleTable table) {
        System.out.print(table.toString());
    }

    //Test distance calculation algorithm
    static SimpleTable getDistanceTable(Port[] ports){
        SimpleTable table = new SimpleTable(3, new String[]{"Origin", "Destination", "Distance"});
        for (Port origin : ports) {
            for (Port destination : ports) {
                table.append(origin.getName());
                table.append(destination.getName());
                table.append(origin.getDistanceTo(destination));
            }
        }
        return table;
    }

    static SimpleTable getCargoTableFromDatabase(String file) {
        try{
            DatabaseInterface database = DatabaseInterface.connect(file, DatabaseInterface.singleplayer);
            SimpleTable table = new SimpleTable(3, new String[]{"Name", "Capacity", "BasePrice"});
            Cargo[] cargos = database.getCargos();
            for (Cargo cargo : cargos) {
                table.append(cargo.getName());
                table.append(cargo.getWeight());
                table.append(cargo.getBasePrice());
            }
            return table;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    static void printShip(Ship ship){
        System.out.printf("%s:\t%d\n", "Capacity", ship.getCapacity());
        System.out.printf("%s:\t%d\n", "Consumption", ship.getConsumption());
        Vector<Integer> position = ship.getPosition();
        System.out.printf("%s: \tx=%d\ty=%d\n", "Position", position.get(0), position.get(1));
    }

    static void printPlayer(Player player){
        System.out.println("Ship:");
        printShip(player.getShip());
        System.out.println("Player:");
        System.out.printf("%s:\t%d\n", "Capital", player.getCapital());
    }

    static Player getPlayerFromDatabase(String file){
        try{
            DatabaseInterface database = DatabaseInterface.connect(file, DatabaseInterface.singleplayer);
            Player player = database.getPlayerById(1);
            return player;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    static void writePlayerToDatabase(String file, Player player){
        try{
            DatabaseInterface database = DatabaseInterface.connect(file, DatabaseInterface.singleplayer);
            database.setPlayer(player);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    static void writeCargosToDatabase(String file, Cargo[] cargos){
        try{
            DatabaseInterface database = DatabaseInterface.connect(file, DatabaseInterface.singleplayer);
            database.setCargos(cargos);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    static void registerAccount(String url, String name, String password){
        try{
            DatabaseInterface database = DatabaseInterface.connect(url, DatabaseInterface.multiplayer);
            database.register(name, password);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    static int authenticator(String url, String name, String password){
        try{
            DatabaseInterface database = DatabaseInterface.connect(url, DatabaseInterface.multiplayer);
            return database.authenticate(name, password);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return DatabaseInterface.noPlayer;
    }

    public static void main(String[] args){
            /*SingleplayerGame testGame = new SingleplayerGame("..\\template\\newgame.sqllite");
            testGame.login("","");
            printTable(getPortTable(testGame.data.ports)); //Test data integrity
            printTable(getDistanceTable(testGame.data.ports)); //Test distance calculation algorithm*/
        printTable(getCargoTableFromDatabase("template\\newgame.sqlite")); //Test local database connection
        printPlayer(getPlayerFromDatabase("template\\newgame.sqlite")); //Test local database connection
        /*registerAccount("127.0.0.1", "Tina","123456"); //Test remote account registration
        registerAccount("127.0.0.1", "Ousan", "123456"); //Test remote account registration

        System.out.println("Logged in as user with id " + authenticator("127.0.0.1", "Tina", "123456")); //Test account authentication
        System.out.println("Logged in as user with id " + authenticator("127.0.0.1", "Tom", "123456")); //Test account authentication
        System.out.println("Logged in as user with id " + authenticator("127.0.0.1", "Ousan", "123456")); //Test account authentication
        */
    }
}

