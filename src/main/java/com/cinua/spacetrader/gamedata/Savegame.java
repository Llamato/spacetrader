package com.cinua.spacetrader.gamedata;

import com.cinua.spacetrader.database.DatabaseInterface;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.Port;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public  abstract class Savegame{
    protected int playerId = DatabaseInterface.noPlayer;

    protected DatabaseInterface database;
    public GameData data;
    protected String gameLocation;

    public Savegame(String gameLocation){
        this.gameLocation = gameLocation;
    }

    public GameData load() throws SQLException{
        return new GameData(database.getCargos(), database.getPorts(), database.getPlayerById(playerId));
    }

    public abstract void create(String name, String password) throws SQLException, IOException;

    public abstract void login(String name, String password) throws SQLException;


    public void logout() throws SQLException{
        playerId = DatabaseInterface.noPlayer;
        database.disconnect();
    }
    public boolean loggedIn(){
        return playerId != DatabaseInterface.noPlayer;
    }


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
