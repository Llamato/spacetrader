package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Ship;
import com.cinua.spacetrader.gameplay.planet.Port;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class GameData{
    public Cargo[] items;
    public Port[] ports;
    public Player player;

    public GameData(){
        this.items = new Cargo[]{
                new Cargo("Polymere",2,20),
                new Cargo("Computer",1,50),
                new Cargo("Waschmittel",4,10),
                new Cargo("Treibstoff", 1,1),
                new Cargo("Halbleiter",3,30),
                new Cargo("Neon",2,15)
        };

        this.ports = new Port[]{
                new Port("Tortuga", new Vector<>(Arrays.asList(0,0)), new HashMap<>(){{put(items[0], 1); put(items[1], Port.prohibited); put(items[2], 3); put(items[3], 1); put(items[4], 2); put(items[5], 2);}}),
                new Port("Trier", new Vector<>(Arrays.asList(0,3)), new HashMap<>(){{put(items[0], 4); put(items[1], 1); put(items[2], 1); put(items[3], 1); put(items[4], 4); put(items[5], 1);}}),
                new Port("Berlin", new Vector<>(Arrays.asList(3,0)), new HashMap<>(){{put(items[0], 2); put(items[1], 3); put(items[2], 4); put(items[3], 2); put(items[4], 2); put(items[5], 4);}}),
                new Port("Neudorf", new Vector<>(Arrays.asList(3,3)), new HashMap<>(){{put(items[0], 3); put(items[1], 2); put(items[2], Port.prohibited); put(items[3], 2); put(items[4], 1); put(items[5], 3);}})
        };

        this.player = new Player(new Ship(30,1),100);
    }
    public GameData(Cargo[] items, Port[] ports, Player player){
        this.items = items;
        this.ports = ports;
        this. player = player;
    }
}
