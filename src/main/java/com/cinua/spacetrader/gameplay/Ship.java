package com.cinua.spacetrader.gameplay;
import java.util.HashMap;

public class Ship{
    private int capacity;
    private int consumption;
    private HashMap<Cargo, Integer> trunk;

    public Ship(int modelCapacity, int modelConsumption){
        capacity = modelCapacity;
        consumption = modelConsumption;
        trunk = new HashMap<>();

    }

    public Ship(int capacity, int consumption, HashMap<Cargo, Integer> trunk){
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
}