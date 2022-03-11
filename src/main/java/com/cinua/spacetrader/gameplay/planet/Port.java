package com.cinua.spacetrader.gameplay.planet;
import com.cinua.spacetrader.gameplay.Cargo;
import java.util.HashMap;
import java.util.Vector;

public class Port{
    private String name;
    private Vector<Integer> position;
    public static final int prohibited = -1;
    private HashMap<Cargo, Integer> cargoLookup; //<Cargo,Price Multiplier>

    public Port(String modelName, Vector<Integer> modelPosition, HashMap<Cargo, Integer> modelCargoList){
        name = modelName;
        position = modelPosition;
        cargoLookup = modelCargoList;

    }
    public String getName(){
        return name;
    }

    public Vector<Integer> getPosition(){
        return position;
    }

    public HashMap<Cargo, Integer> getCargoLookup(){
        return cargoLookup;
    }

    public int getDistanceTo(Port destination){
        Vector<Integer> destinationPosition = destination.getPosition();
        return Math.abs(position.get(0) - destinationPosition.get(0)) + Math.abs(position.get(1) - destinationPosition.get(1));
    }
}
