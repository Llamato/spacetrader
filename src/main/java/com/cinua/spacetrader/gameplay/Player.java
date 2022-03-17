package com.cinua.spacetrader.gameplay;

public class Player{
    private String name;
    private Ship ship;
    private int capital;

    public Player(String name, Ship ship, int capital){
        this.name = name;
        this.ship = ship;
        this.capital = capital;

    }

    public String getName(){
        return name;
    }

    public Ship getShip(){
        return ship;
    }

    public int getCapital(){
        return capital;
    }
}
