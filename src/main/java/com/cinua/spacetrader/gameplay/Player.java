package com.cinua.spacetrader.gameplay;

public class Player{
    private Ship ship;
    private int capital;

    public Player(Ship modelShip, int modelCapital){
        ship = modelShip;
        capital = modelCapital;

    }

    public Ship getShip(){
        return ship;
    }

    public int getCapital(){
        return capital;
    }
}
