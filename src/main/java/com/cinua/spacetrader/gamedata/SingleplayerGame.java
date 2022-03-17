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
    private static final String identifierName = "identifier.sqlite";

    private static String getDatabasePathFromSavegameName(String name){
        return savesPath + File.separator + name + File.separator + name + saveFileExtension;
    }

    private static String getDirFromFilePath(String filepath){
        return filepath.substring(0, filepath.lastIndexOf(File.separator));
    }

    private static String getIdentifierPathFromSavegamePath(String path){
        return getDirFromFilePath(path) + File.separator + identifierName;
    }

    public SingleplayerGame(String path) throws SQLException{
        super(getDatabasePathFromSavegameName(path));
    }

    private static void newGame(String name) throws IOException{
        Files.createDirectories(Path.of(getDirFromFilePath(name)));
        Files.copy(Path.of(newGameTemplatePath + File.separator + "newgame.sqlite"), new FileOutputStream(name));
        Files.copy(Path.of(newGameTemplatePath + File.separator + identifierName), new FileOutputStream(getIdentifierPathFromSavegamePath(name)));
    }

    public static String[] getSavegamesList() throws IOException{
        return Files.list(Path.of(savesPath)).map(Path::toString).toArray(String[]::new);
    }

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
    public void create(String name, String password) throws SQLException, IOException{
            newGame(gameLocation);
            database = DatabaseInterface.connect(gameLocation, DatabaseInterface.singleplayer);
    }

    public void login(String name, String password) throws SQLException{
        if(database == null)
            database = DatabaseInterface.connect(gameLocation, DatabaseInterface.singleplayer);
        playerId = DatabaseInterface.singleplayerAccountId;
        data = load();
    }
}
