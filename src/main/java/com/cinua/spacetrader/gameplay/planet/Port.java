package com.cinua.spacetrader.gameplay.planet;
import com.cinua.spacetrader.database.DatabaseObject;
import java.util.Vector;

public class  Port extends DatabaseObject{
    private String name;
    private Vector<Integer> position;
    public static final int prohibited = -1;
    private Market market;

    public Port(int id, String modelName, Vector<Integer> modelPosition, Market market){
        super(id);
        name = modelName;
        position = modelPosition;
        this.market = market;

    }
    public String getName(){
        return name;
    }

    public Vector<Integer> getPosition(){
        return position;
    }

    public Market getMarket(){
        return market;
    }

    public static int getDistanceBetween(Vector<Integer> origin, Vector<Integer> destination){
        return Math.abs(Math.abs(destination.get(0) - origin.get(0)) + Math.abs(destination.get(0) - origin.get(0)));
    }

    public int getDistanceTo(Port destination){
        Vector<Integer> destinationPosition = destination.getPosition();
        return Math.abs(position.get(0) - destinationPosition.get(0)) + Math.abs(position.get(1) - destinationPosition.get(1));
    }
}
