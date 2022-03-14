package com.cinua.spacetrader.gamedata;

import com.cinua.spacetrader.database.DatabaseInterface;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.planet.Port;
import java.sql.SQLException;
import java.util.List;

public  abstract class Savegame{
    protected DatabaseInterface database;
    protected GameData data;
    protected String gameLocation;

    public Savegame(String gameLocation){
        this.gameLocation = gameLocation;
    }

    public abstract void login(String name, String password) throws SQLException;
    public abstract void logout() throws SQLException;
    public abstract boolean loggedIn();
    public abstract int create(String name, String password) throws SQLException;
    public abstract GameData load() throws SQLException;

    public List<Cargo> getCargosInPort(Port port){
        return null;
    }

    public boolean travelToPort(Port port){
        return false;
    }

    public boolean buyCargoInPort(Cargo item, Port port){
        return false;
    }

    public boolean sellCargoInPort(Cargo item, Port port){
        return false;
    }

    public int getBalance(Player player){
        return 0;
    }
}
