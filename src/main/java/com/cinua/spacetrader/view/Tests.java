package com.cinua.spacetrader.view;
import com.cinua.spacetrader.gamedata.SingleplayerGame;
import com.cinua.spacetrader.database.DatabaseInterface;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Ship;
import com.glabs.tables.SimpleTable;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.planet.Port;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Tests{

    //Test data integrity
    static SimpleTable getPortTable(Port[] ports){
        SimpleTable table = new SimpleTable(3 + ports.length, new String[]{"Ware", "Kapazität", "Basispreis"});
        Arrays.stream(ports).map(Port::getName).forEach(table::append);
        Cargo[] cargos = Arrays.stream(ports).map(Port::getCargoLookup).map(HashMap::keySet).flatMap(Set<Cargo>::stream).distinct().toArray(Cargo[]::new);
        for(Cargo cargo : cargos){
            table.append(cargo.getName());
            table.append(cargo.getWeight());
            table.append(cargo.getBasePrice());
            for(Port port : ports){
                int capacity = port.getCargoLookup().get(cargo);
                table.append(capacity == Port.prohibited ? "Verboten" : capacity);
            }
        }
        return table;
    }

    static void printTable(SimpleTable table){
        System.out.print(table.toString());
    }

    //Test distance calculation algorithm
    static SimpleTable getDistanceTable(Port[] ports){
        SimpleTable table = new SimpleTable(3, new String[]{"Origin", "Destination", "Distance"});
        for(Port origin : ports){
            for(Port destination : ports){
                table.append(origin.getName());
                table.append(destination.getName());
                table.append(origin.getDistanceTo(destination));
            }
        }
        return table;
    }

    static SimpleTable getCargoTableFromDatabase(String file){
        try{
            DatabaseInterface database = DatabaseInterface.connect(file, DatabaseInterface.singleplayer);
            SimpleTable table = new SimpleTable(3, new String[]{"Name","Capacity","BasePrice"});
            Cargo[] cargos = database.getCargos();
            for(Cargo cargo :cargos){
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
            Player player = database.getPlayer();
            return player;
        }catch (SQLException e){
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

    public static void main(String[] args){
        /*SingleplayerGame.instance = new SingleplayerGame(); //Temporary!
        printTable(getPortTable(SingleplayerGame.instance.ports)); //Test data integrity
        printTable(getDistanceTable(SingleplayerGame.instance.ports)); //Test distance calculation algorithm*/
        printTable(getCargoTableFromDatabase("template/newgame.sqlite"));
        printPlayer(getPlayerFromDatabase("template/newgame.sqlite"));
        writePlayerToDatabase("saves/Hund/Hund.sqlite", getPlayerFromDatabase("template/newgame.sqlite"));
    }
}
