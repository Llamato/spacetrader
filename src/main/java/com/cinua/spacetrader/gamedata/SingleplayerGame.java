package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.planet.Port;
import com.cinua.spacetrader.database.DatabaseInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class SingleplayerGame extends GameData{
    private static final String savesPath = "saves";
    private static final String newGameTemplatePath = "template";
    private static final String saveFileExtension = ".sqlite";

    private SingleplayerGame(Cargo[] items, Port[] ports, Player player){
        super(items, ports, player);
    }

    public static void newGame(String name) throws IOException{
        Files.createDirectories(Path.of(savesPath + File.separator + name));
        Files.copy(Path.of(newGameTemplatePath + File.separator + "newgame.sqlite"), new FileOutputStream( savesPath + File.separator + name +  File.separator + name + ".sqlite"));
        Files.copy(Path.of(newGameTemplatePath + File.separator + "identifier.sqlite"), new FileOutputStream(savesPath + File.separator + name + File.separator + "identifier.sqlite"));
    }

    public static String[] getSavegamesList() throws IOException{
        return Files.list(Path.of(savesPath)).map(Path::toString).toArray(String[]::new);
    }

    public static SingleplayerGame load(String name) throws SQLException{
        DatabaseInterface database = DatabaseInterface.connect(savesPath + File.separator + name + File.separator + name + saveFileExtension, DatabaseInterface.singleplayer);
        return new SingleplayerGame(database.getCargos(), database.getPorts(), database.getPlayer());
    }

    public void save(String name) throws SQLException{
        DatabaseInterface database = DatabaseInterface.connect(savesPath + File.separator + name + File.separator + name + saveFileExtension, DatabaseInterface.singleplayer);
        database.setPlayer(player);
    }

    public static void delete(String name) throws IOException{
        Files.deleteIfExists(Path.of(savesPath + File.separator + name + File.separator + name + saveFileExtension));
        Files.deleteIfExists(Path.of(savesPath + File.separator + name + File.separator + "identifier" + saveFileExtension));
        Files.deleteIfExists(Path.of(savesPath + File.separator + name));
    }
}
