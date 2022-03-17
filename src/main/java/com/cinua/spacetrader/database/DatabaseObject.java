package com.cinua.spacetrader.database;

public class DatabaseObject{
    protected int id;

    protected DatabaseObject(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
