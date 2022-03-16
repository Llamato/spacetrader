package com.cinua.spacetrader.gameplay;

import com.cinua.spacetrader.gamedata.Savegame;

public class gameLoop{
    public static final int success = 0;
    public static final int invalidSavegame = 1;

    public static int loop(Savegame game){
        if (game == null){
            return invalidSavegame;
        }
        boolean gameRunning = true;
        while (gameRunning) {

            gameRunning = false;
        }
        return success;
    }
}
