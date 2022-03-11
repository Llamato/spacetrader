package com.cinua.spacetrader.gameplay;

public class Cargo{
    private String name;
    private int weight;
    private int base_price;

    public Cargo(String modelName, int modelCapacity, int modelBase_price){
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
