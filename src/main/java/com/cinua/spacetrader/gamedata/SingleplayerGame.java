package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.database.DatabaseInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class SingleplayerGame extends Savegame{
    private static final String savesPath = "saves";
    private static final String newGameTemplatePath = "template";
    private static final String saveFileExtension = ".sqlite";

    /*private SingleplayerGame(Cargo[] items, Port[] ports, Player player){
        super(items, ports, player);
    }*/

    private static String getDatabasePathFromSavegameName(String name){
        return savesPath + File.separator + name + File.separator + name + saveFileExtension;
    }

    public SingleplayerGame(String path) throws SQLException{
        super(getDatabasePathFromSavegameName(path));
        database = DatabaseInterface.connect(gameLocation, DatabaseInterface.singleplayer);
    }

    public static void newGame(String name) throws IOException{
        Files.createDirectories(Path.of(savesPath + File.separator + name));
        Files.copy(Path.of(newGameTemplatePath + File.separator + "newgame.sqlite"), new FileOutputStream( savesPath + File.separator + name +  File.separator + name + ".sqlite"));
        Files.copy(Path.of(newGameTemplatePath + File.separator + "identifier.sqlite"), new FileOutputStream(savesPath + File.separator + name + File.separator + "identifier.sqlite"));
    }

    public static String[] getSavegamesList() throws IOException{
        return Files.list(Path.of(savesPath)).map(Path::toString).toArray(String[]::new);
    }

    /*public static SingleplayerGame load(String name) throws SQLException{
        DatabaseInterface database = DatabaseInterface.connect(savesPath + File.separator + name + File.separator + name + saveFileExtension, DatabaseInterface.singleplayer);
        return new SingleplayerGame(database.getCargos(), database.getPorts(), database.getPlayer());
    }*/

    /*public static Savegame load(String name) throws SQLException{
        DatabaseInterface database = DatabaseInterface.connect(savesPath + File.separator + name + File.separator + name + saveFileExtension, DatabaseInterface.singleplayer);
        return new Savegame(database);
    }*/

    public void save(String name) throws SQLException{
        DatabaseInterface database = DatabaseInterface.connect(savesPath + File.separator + name + File.separator + name + saveFileExtension, DatabaseInterface.singleplayer);
        database.setPlayer(data.player); // This will not work in final version.
    }

    public static void delete(String name) throws IOException{
        Files.deleteIfExists(Path.of(savesPath + File.separator + name + File.separator + name + saveFileExtension));
        Files.deleteIfExists(Path.of(savesPath + File.separator + name + File.separator + "identifier" + saveFileExtension));
        Files.deleteIfExists(Path.of(savesPath + File.separator + name));
    }

    @Override
    public void login(String name, String password) throws SQLException {
        database.register(name, "");
        data = load();
    }

    @Override
    public void logout() throws SQLException{
        database.disconnect();
    }

    @Override
    public boolean loggedIn(){
        return true;
    }

    @Override
    public int create(String name, String password) throws SQLException{
        try{
            newGame(gameLocation);
            return 1;
        }catch(IOException e){
            return 0;
        }
    }

    @Override
    public GameData load() throws SQLException{
        return new GameData(database.getCargos(), database.getPorts(), database.getPlayerById(1));
    }
}
