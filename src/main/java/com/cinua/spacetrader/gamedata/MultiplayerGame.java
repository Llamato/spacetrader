package com.cinua.spacetrader.gamedata;
import com.cinua.spacetrader.gameplay.Cargo;
import com.cinua.spacetrader.gameplay.Player;
import com.cinua.spacetrader.gameplay.planet.Port;

public class MultiplayerGame extends GameData {

    public MultiplayerGame(Cargo[] items, Port[] ports, Player player) {
        super(items, ports, player);
    }

    public static GameData load(String gameIdentifier) {
        return null;
    }
}
