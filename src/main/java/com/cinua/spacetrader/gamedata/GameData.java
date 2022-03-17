package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Port;

public class GameData{
    public Cargo[] items;
    public Port[] ports;
    public Player player;

    /*public GameData(){
        this.items = new Cargo[]{
                new Cargo(6,"Polymere",2,20),
                new Cargo(1,"Computer",1,50),
                new Cargo(5,"Waschmittel",4,10),
                new Cargo(2,"Treibstoff", 1,1),
                new Cargo(3,"Halbleiter",3,30),
                new Cargo(4,"Neon",2,15)
        };

        this.ports = new Port[]{
                new Port(1, "Tortuga", new Vector<>(Arrays.asList(0,0)), new Market(null, new HashMap<>(){{put(items[0], 1); put(items[1], Port.prohibited); put(items[2], 3); put(items[3], 1); put(items[4], 2); put(items[5], 2);}}),
                new Port(2,"Trier", new Vector<>(Arrays.asList(0,3)), new Market(null, new HashMap<>(){{put(items[0], 4); put(items[1], 1); put(items[2], 1); put(items[3], 1); put(items[4], 4); put(items[5], 1);}}),
                new Port(3,"Berlin", new Vector<>(Arrays.asList(3,0)), new Market(null, new HashMap<>(){{put(items[0], 2); put(items[1], 3); put(items[2], 4); put(items[3], 2); put(items[4], 2); put(items[5], 4);}}),
                new Port(4,"Neudorf", new Vector<>(Arrays.asList(3,3)), new Market(null, new HashMap<>(){{put(items[0], 3); put(items[1], 2); put(items[2], Port.prohibited); put(items[3], 2); put(items[4], 1); put(items[5], 3);}}))
        };

        this.player = new Player(new Ship(1, 30,1, ports[0].getPosition()),100);
    }*/

    public GameData(Cargo[] items, Port[] ports, Player player){
        this.items = items;
        this.ports = ports;
        this. player = player;
    }
}
