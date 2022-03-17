package com.cinua.spacetrader.gameplay;
import com.cinua.spacetrader.database.DatabaseObject;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

public class Ship extends DatabaseObject{
    private int capacity;
    private int consumption;
    private HashMap<Cargo, Integer> trunk;
    private Vector<Integer> position;

    public Ship(int id, int capacity, int consumption, Vector<Integer> position){
        super(id);
        this.capacity = capacity;
        this.consumption = consumption;
        this.position = position;
        trunk = new HashMap<>();
    }

    public Ship(int id, int capacity, int consumption, HashMap<Cargo, Integer> trunk){
        super(id);
        this.capacity = capacity;
        this.consumption = consumption;
        this.trunk = trunk;
    }

    public int getCapacity(){
        return capacity;
    }

    public int getConsumption(){
        return consumption;
    }

    public Vector<Integer> getPosition(){
        return position;
    }


    public int getRemainingCapacity(){
        return capacity - trunk.keySet().stream().mapToInt(item -> item.getWeight()*trunk.get(item)).sum();
    }

    private Stream<Cargo> getFuelInTrunk(){
        return trunk.keySet().stream().filter(item -> item.getId() == Cargo.fuelId);
    }

    public int getFuelRemaining(){
        return Math.toIntExact(getFuelInTrunk().count());
    }

    private int getFuelRemaining(double distanceTraveled){
        return Math.toIntExact(Math.round(getFuelRemaining() - distanceTraveled * consumption));
    }

    public int getRange(){
        return getFuelRemaining(0) / consumption;
    }

    private void burnFuel(long amount){
        while(amount > 0){
            trunk.remove(getFuelInTrunk().findFirst().get());
            amount--;
        }
    }

    private static Vector<Integer> getTrackVector(Vector<Integer> origin, Vector<Integer> destination){
        return new Vector<>(List.of(destination.get(0) - origin.get(0), destination.get(1) - origin.get(1)));
    }

    private static double getVectorLength(Vector<Integer> vector){
        return Math.sqrt(Math.pow(vector.get(0),2) + Math.pow(vector.get(1), 2));
    }

    private static Vector<Double> getNormalizedVector(Vector<Integer> vector){
        int x = vector.get(0);
        int y = vector.get(1);
        int sum = x+y;
        Vector<Double> normalizedVector = new Vector<>();
        normalizedVector.add((double) (x / sum));
        normalizedVector.add((double) (y / sum));
        return normalizedVector;
    }

    public void travelTo(Vector<Integer> position){
        Vector<Integer> plannedTrack =  getTrackVector(this.position, position);
        int fuel = getFuelRemaining(getVectorLength(plannedTrack));
        if(fuel >= 0){
             burnFuel(fuel);
             this.position = position;
        }else{
            int range = getRange();
            burnFuel(getFuelRemaining(0));
            Vector<Double> track = getNormalizedVector(plannedTrack);
            this.position = new Vector<>(List.of(Math.toIntExact(Math.round(track.get(0) * range)), Math.toIntExact(Math.round(track.get(1) * range))));
        }
    }

    public void travelTo(Port port){
        Vector<Integer> portPosition = port.getPosition();
       int distance =  Port.getDistanceBetween(this.position, portPosition);
       int range = getRange();
       if(range >= distance){
           burnFuel(getFuelInTrunk().count() - getFuelRemaining(distance));
           this.position = portPosition;
       }else{

       }
    }

    public void loadCargo(Cargo cargo){
        if(trunk.containsKey(cargo)){
            trunk.put(cargo, trunk.get(cargo) +1);
        }
    }

    public void unloadCargo(Cargo cargo){
        trunk.put(cargo, trunk.get(cargo) -1);
        if(trunk.get(cargo) <= 0){
            trunk.remove(cargo);
        }
    }
}