package com.cinua.spacetrader.gameplay;
import com.cinua.spacetrader.database.DatabaseObject;

public class Cargo extends DatabaseObject{
    public static final int fuelId = 2;
    private String name;
    private int weight;
    private int base_price;

    public Cargo(int id, String modelName, int modelCapacity, int modelBase_price){
        super(id);
        name = modelName;
        weight = modelCapacity;
        base_price = modelBase_price;
    }

    public String getName(){
        return name;
    }

    public int getWeight(){
        return weight;
    }

    public int getBasePrice(){
        return base_price;
    }
}
