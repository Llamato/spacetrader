package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.database.DatabaseInterface;

import java.sql.SQLException;

public class MultiplayerGame extends Savegame{

    public MultiplayerGame(String url) throws SQLException{
        super(url);
    }

    @Override
    public void create(String name, String password) throws SQLException{
        database = DatabaseInterface.connect(gameLocation, DatabaseInterface.multiplayer);
        playerId = database.register(name, password);
    }

    public void login(String name, String password) throws SQLException{
        if(database == null){
            database = DatabaseInterface.connect(gameLocation, DatabaseInterface.multiplayer);
        }
        playerId = database.authenticate(name,password);
        data = load();
    }
}
