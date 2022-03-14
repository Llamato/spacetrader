package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.database.DatabaseInterface;
import java.sql.SQLException;

public class MultiplayerGame extends Savegame{
    private int playerId = DatabaseInterface.noPlayer;

    public MultiplayerGame(String url) throws SQLException{
        super(url);
        database = DatabaseInterface.connect(gameLocation, DatabaseInterface.multiplayer);
    }

    @Override
    public boolean loggedIn(){
        return playerId != DatabaseInterface.noPlayer;
    }

    @Override
    public int create(String name, String password) throws SQLException{
        playerId = database.register(name, password);
        return playerId;
    }

    @Override
    public GameData load() throws SQLException{
        return new GameData(database.getCargos(), database.getPorts(), database.getPlayerById(playerId));
    }

    @Override
    public void login(String name, String password) throws SQLException{
        playerId = database.authenticate(name,password);
        data = load();
    }

    @Override
    public void logout() throws SQLException{
        playerId = DatabaseInterface.noPlayer;
        database.disconnect();
    }
}
